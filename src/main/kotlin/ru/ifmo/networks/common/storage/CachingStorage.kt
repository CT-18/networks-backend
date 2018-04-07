package ru.ifmo.networks.common.storage

import ru.ifmo.networks.common.StreamInfo
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

abstract class CachingStorage(protected open val fallback: Storage): Storage {

//    private val streamInfoLock = ConcurrentHashMap<StreamInfo, ReentrantLock>()
    private val streamInfoLock = Collections.synchronizedMap(LruMap<StreamInfo, ReentrantLock>(1024))

    override fun getFragment(streamInfo: StreamInfo): ByteArray? {
        val lock = streamInfoLock.computeIfAbsent(streamInfo) { ReentrantLock() }

        lock.withLock {
            var byteArray = getCachedFragment(streamInfo)
            if (byteArray == null) {
                byteArray = fallback.getFragment(streamInfo)
                if (byteArray != null) {
                    cacheFragment(streamInfo, byteArray)
                }
            }
            return byteArray
        }
    }

    protected abstract fun getCachedFragment(streamInfo: StreamInfo): ByteArray?

    protected abstract fun cacheFragment(streamInfo: StreamInfo, byteArray: ByteArray)
}