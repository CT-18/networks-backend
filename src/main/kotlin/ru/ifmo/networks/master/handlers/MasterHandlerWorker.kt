package ru.ifmo.networks.master.handlers

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import ru.ifmo.networks.*
import ru.ifmo.networks.master.*
import java.nio.charset.Charset

class MasterHandlerWorker() : HandlerWorker {
    override fun getStreams(serverRequest: ServerRequest): Mono<ServerResponse> =
            ServerResponse.ok().jsonSuccess(StreamsResponse(listOf(
                    StreamInfo("petrovich", "http://10.8.0.3/live.m3u8")
            )))

    override fun getFragment(serverRequest: ServerRequest): Mono<ServerResponse> {
        val name = serverRequest.pathVariable("name") ?: return ServerResponse.badRequest().build()
        val fragment = serverRequest.pathVariable("fragment") ?: return ServerResponse.badRequest().build()

        if (fragment.endsWith(".m3u8")) {
            return getM3U8Fragment(name, fragment)
        } else {
            return getTSFragment(name, fragment)
        }
    }

    private fun getM3U8Fragment(name: String, fragment: String): Mono<ServerResponse> {
        assert(fragment.endsWith(".m3u8"))

        val response = MalinkaProxy("http://10.8.0.3/").download(fragment)

        return ServerResponse.ok()
                .header("Accept-Ranges", "bytes")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Expose-Headers", "Content-Length")
                .contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
                .contentLength(response.size.toLong())
                .hls(response)
    }

    private fun getTSFragment(name: String, fragment: String): Mono<ServerResponse> {
        assert(fragment.endsWith(".ts"))

        val response = MalinkaProxy("http://10.8.0.3/").download(fragment)

        return ServerResponse.ok()
                .header("Accept-Ranges", "bytes")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Expose-Headers", "Content-Length")
                .contentType(MediaType.parseMediaType("video/mp2t"))
                .contentLength(response.size.toLong())
                .hls(response)
    }

    companion object {
        private fun calculateContentLength(content: String): Long =
                content.toByteArray(Charset.forName("UTF-8")).size.toLong()
    }
}