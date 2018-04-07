package ru.ifmo.networks.common.storage

import ru.ifmo.networks.common.StreamInfo

interface Storage {
    fun getFragment(streamInfo: StreamInfo): ByteArray?
}