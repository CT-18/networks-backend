package ru.ifmo.networks.download

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL

class Downloader {

    fun downloadPlaylistInfo(url: String): String {
        return fetchPlaylistMeta(URL(url)).joinToString("\n")
    }

    @Throws(IOException::class)
    private fun fetchPlaylistMeta(url: URL): List<String> {
        val playlist = mutableListOf<String>()
        val reader = BufferedReader(InputStreamReader(url.openStream()))
        var isMaster = false
        var maxRate = 0L
        var maxRateIndex = 0

        var line: String
        var index = 0

        while (true) {
            line = reader.readLine() ?: break
            playlist.add(line)

            if (line.contains(BANDWIDTH))
                isMaster = true

            if (isMaster && line.contains(BANDWIDTH)) {
                try {
                    var pos = line.lastIndexOf("=")
                    val bandwidth = java.lang.Long.parseLong(line.substring(++pos))

                    maxRate = Math.max(bandwidth, maxRate)

                    if (bandwidth == maxRate)
                        maxRateIndex = index + 1
                } catch (ignore: NumberFormatException) {
                }

            }

            index++
        }

        reader.close()

        if (isMaster) {
            System.out.printf("Found master playlist, fetching highest stream at %dKb/s\n", maxRate / 1024)
            val updatedUrl = updateUrlForSubPlaylist(url, playlist[maxRateIndex])
            fetchPlaylistMeta(updatedUrl)
        }

        return playlist
    }

    companion object {
        private val BANDWIDTH = "BANDWIDTH"

        @Throws(MalformedURLException::class)
        private fun updateUrlForSubPlaylist(url: URL, sub: String): URL {
            val newUrl: String

            if (!sub.startsWith("http")) {
                newUrl = getBaseUrl(url) + sub
            } else {
                newUrl = sub
            }

            return URL(newUrl)
        }

        private fun getBaseUrl(url: URL): String {
            val urlString = url.toString()
            var index = urlString.lastIndexOf('/')
            return urlString.substring(0, ++index)
        }
    }
}