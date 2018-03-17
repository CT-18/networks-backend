package ru.ifmo.networks

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono
import java.nio.charset.Charset

/**
 * Handler of requests
 *
 * @author Danil Kolikov
 */
@Component
class Handler {

    fun getStreams(serverRequest: ServerRequest): Mono<ServerResponse> =
            ok().jsonSuccess(StreamsResponse(listOf(
                    StreamInfo("petrovich", "http://10.8.0.3/live.m3u8")
            )))

    fun getFragment(serverRequest: ServerRequest): Mono<ServerResponse> {
        val name = serverRequest.pathVariable("name") ?: return badRequest().build()
        val fragment = serverRequest.pathVariable("fragment") ?: return badRequest().build()

        if (fragment.endsWith(".m3u8")) {
            return getM3U8Fragment(name, fragment)
        } else {
            return getTSFragment(name, fragment)
        }
    }

    private fun getM3U8Fragment(name: String, fragment: String): Mono<ServerResponse> {
        assert(fragment.endsWith(".m3u8"))

        val response = MalinkaProxy("http://10.8.0.3/").download(fragment)
        val contentLength = calculateContentLength(response)

        return ok()
                .header("Accept-Ranges", "bytes")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Expose-Headers", "Content-Length")
                .contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
                .contentLength(contentLength)
                .hls(response)
    }

    private fun getTSFragment(name: String, fragment: String): Mono<ServerResponse> {
        assert(fragment.endsWith(".ts"))

        val response = MalinkaProxy("http://10.8.0.3/").download(fragment)
        val contentLength = calculateContentLength(response)

        return ok()
                .header("Accept-Ranges", "bytes")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Expose-Headers", "Content-Length")
                .contentType(MediaType.parseMediaType("video/mp2t"))
                .contentLength(contentLength)
                .hls(response)
    }

    companion object {
        private fun calculateContentLength(content: String): Long =
                content.toByteArray(Charset.forName("UTF-8")).size.toLong()
    }
}