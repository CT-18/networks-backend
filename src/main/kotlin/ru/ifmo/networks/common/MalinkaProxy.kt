package ru.ifmo.networks.common

import org.apache.commons.io.IOUtils.toByteArray
import java.net.URL

class MalinkaProxy(private val baseUrl: String) {

    fun download(fragment: String): ByteArray {
        System.out.println("Loading: $fragment")
        return download(URL("$baseUrl/$fragment"))
    }

    private fun download(url: URL): ByteArray {
        val connection = url.openConnection()
        connection.connectTimeout = 4000
        return toByteArray(connection.getInputStream())
    }
}
