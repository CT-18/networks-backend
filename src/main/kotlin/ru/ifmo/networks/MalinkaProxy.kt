package ru.ifmo.networks

import com.google.common.io.CharStreams
import java.io.InputStreamReader
import java.net.URL

//import net.chrislongo.hls.PlaylistDownloader;


class MalinkaProxy(private val baseUrl: String) {

    fun download(fragment: String): String {
        System.out.println("Loading: $fragment")
        return download(URL("$baseUrl/$fragment"))
    }

    private fun download(url: URL): String {
        return CharStreams.toString(InputStreamReader(url.openStream()))
    }
}
