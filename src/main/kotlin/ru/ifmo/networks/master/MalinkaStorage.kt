package ru.ifmo.networks.master

import ru.ifmo.networks.common.MalinkaProxy
import ru.ifmo.networks.common.StreamInfo
import ru.ifmo.networks.common.storage.Storage

class MalinkaStorage(private val urlExtractor: (String) -> String?): Storage {
    override fun getFragment(streamInfo: StreamInfo): ByteArray? {
        return urlExtractor(streamInfo.name)?.let {
            MalinkaProxy(it).download(streamInfo.fragment)
        }
    }
}