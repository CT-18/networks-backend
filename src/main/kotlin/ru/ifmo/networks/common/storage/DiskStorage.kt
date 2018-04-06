package ru.ifmo.networks.common.storage

import ru.ifmo.networks.common.StreamInfo
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class DiskStorage(override val fallback: Storage): CachingStorage(fallback) {

    override fun getCachedFragment(streamInfo: StreamInfo): ByteArray? {
        val dir = createStorage(streamInfo.name)
        val path = dir.resolve(streamInfo.fragment)
        if (Files.exists(path)) {
            return Files.readAllBytes(path)
        }
        return null
    }

    override fun cacheFragment(streamInfo: StreamInfo, byteArray: ByteArray) {
        val dir = createStorage(streamInfo.name)
        Files.newOutputStream(dir.resolve(streamInfo.fragment)).use {
            it.write(byteArray)
        }
    }

    private fun createStorage(name: String): Path {
        val path = Paths.get(name)
        if (Files.isDirectory(path)) {
            return path
        }
        if (!Files.exists(path)) {
            return Files.createDirectory(path)
        }
        throw IllegalStateException("Can't create directory for $name")
    }
}