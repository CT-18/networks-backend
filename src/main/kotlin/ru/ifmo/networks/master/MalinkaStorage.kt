package ru.ifmo.networks.master

import ru.ifmo.networks.common.MalinkaProxy
import ru.ifmo.networks.common.StreamInfo
import ru.ifmo.networks.common.storage.Storage
import java.time.Clock
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

class MalinkaStorage(private val urlExtractor: (String) -> String?,
                     private val durationCallback: (Duration, StreamInfo) -> Unit)
    : Storage {

    private val lastStreamInfo = ConcurrentHashMap<String, StreamInfo>()

    override fun getFragment(streamInfo: StreamInfo): ByteArray? {
        return urlExtractor(streamInfo.name)?.let {
            try {
                val before = Clock.systemUTC().instant()
                val download = MalinkaProxy(it).download(streamInfo.fragment)
                val after = Clock.systemUTC().instant()
                durationCallback(Duration.between(before, after), streamInfo)
                lastStreamInfo[streamInfo.name] = streamInfo
                download
            } catch (ignored: Exception) {
                null
            }
        } ?: if (lastStreamInfo[streamInfo.name] != streamInfo) {
            getFragment(lastStreamInfo[streamInfo.name]!!)
        } else null
    }
}