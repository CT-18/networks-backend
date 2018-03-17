package ru.ifmo.networks.master

import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class SelfClearingMap {

    private val allStreams = ConcurrentHashMap<String, StreamBaseUrlAndFragment>()

    private val updateTimes = ConcurrentHashMap<String, Long>()

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
        allStreams.put(name, baseUrlAndFragment)
        updateTimes.put(name, System.currentTimeMillis())
    }

    fun getStream(name: String): StreamBaseUrlAndFragment? {
        return allStreams[name];
    }

    fun asList(): List<Pair<String, StreamBaseUrlAndFragment>> {
        return allStreams.toList();
    }

    private fun selfClean() {
        updateTimes.forEach { s, lastSeen ->
            if (System.currentTimeMillis() - lastSeen > CLEANING_PERIOD) {
                allStreams.remove(s)
                updateTimes.remove(s)
            }
        }
    }

    data class StreamBaseUrlAndFragment(val baseUrl: String, val fragment: String)

    companion object {
        private val CLEANING_PERIOD = TimeUnit.MINUTES.toMillis(10)
    }

}