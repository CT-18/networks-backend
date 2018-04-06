package ru.ifmo.networks.common.storage

import ru.ifmo.networks.common.StreamInfo

class LruStorage(capacity: Int, override val fallback: Storage) : CachingStorage(fallback) {

    private val linkedHashMap = LruMap<StreamInfo, ByteArray>(capacity)

    override fun getCachedFragment(streamInfo: StreamInfo): ByteArray? = linkedHashMap[streamInfo]

    override fun cacheFragment(streamInfo: StreamInfo, byteArray: ByteArray) {
        linkedHashMap[streamInfo] = byteArray
    }
}