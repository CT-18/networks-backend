package ru.ifmo.networks.master

import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class SelfClearingMap {

    private val allStreams = ConcurrentHashMap<String, StreamBaseUrlAndFragment>()

    private val updateTimes = ConcurrentHashMap<String, Long>()

    private val writers = ConcurrentHashMap<String, Process>()

    private val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        allStreams["petrovich"] = StreamBaseUrlAndFragment("http://10.8.0.3", "live.m3u8")

        val task = object : TimerTask() {
            override fun run() {
                selfClean()
            }
        }

        val timer = Timer()
        timer.schedule(task, CLEANING_PERIOD, CLEANING_PERIOD)
    }

    fun update(name: String, baseUrlAndFragment: StreamBaseUrlAndFragment) {
        allStreams[name] = baseUrlAndFragment
        updateTimes[name] = System.currentTimeMillis()
        val dir = Paths.get("$CACHE_DIR$name")
        if (!Files.exists(dir))
            try {
                Files.createDirectories(dir)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        writers[name] = ProcessBuilder("ffmpeg", "-i", "${baseUrlAndFragment.baseUrl}${baseUrlAndFragment.fragment}",
                "-vcodec", "copy", "-acodec", "copy", "$CACHE_DIR$name/file_${System.currentTimeMillis()}.mp4").start()
    }

    fun getStream(name: String): StreamBaseUrlAndFragment? {
        return allStreams[name]
    }

    fun asList(): List<Pair<String, StreamBaseUrlAndFragment>> {
        return allStreams.toList()
    }

    private fun selfClean() {
        updateTimes.forEach { s, lastSeen ->
            if (System.currentTimeMillis() - lastSeen > CLEANING_PERIOD) {
                allStreams.remove(s)
                updateTimes.remove(s)
                writers.remove(s)?.destroy()
            }
        }
    }

    data class StreamBaseUrlAndFragment(val baseUrl: String, val fragment: String)

    companion object {
        private val CLEANING_PERIOD = TimeUnit.MINUTES.toMillis(10)
        private const val CACHE_DIR = "cache/"
    }

}