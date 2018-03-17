package ru.ifmo.networks.common

import org.apache.commons.io.IOUtils.toByteArray
import java.net.URL

//import net.chrislongo.hls.PlaylistDownloader;


class MalinkaProxy(private val baseUrl: String) {

    fun download(fragment: String): ByteArray {
        System.out.println("Loading: $fragment")
        return download(URL("$baseUrl/$fragment"))
    }

    private fun download(url: URL): ByteArray {
        return toByteArray(url.openStream())
    }
}
