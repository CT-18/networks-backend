package ru.ifmo.networks.slave

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import ru.ifmo.networks.common.*
import ru.ifmo.networks.common.handlers.HandlerWorker

class SlaveHandlerWorker : HandlerWorker {
    val streamsMap = mapOf(
            "petrovich" to StreamBaseUrlAndFragment("http://10.8.0.3", "live.m3u8")
    )

    override fun getStreams(serverRequest: ServerRequest): Mono<ServerResponse> =
            ServerResponse.ok().jsonSuccess(StreamsResponse(
                    streamsMap.toList()
                            .map { pair -> StreamInfo(pair.first, pair.second.fragment) }
            ))

    override fun getFragment(serverRequest: ServerRequest): Mono<ServerResponse> {
        val name = serverRequest.pathVariable("name") ?: return ServerResponse.badRequest().build()
        val fragment = serverRequest.pathVariable("fragment") ?: return ServerResponse.badRequest().build()

        return if (fragment.endsWith(".m3u8")) {
            getM3U8Fragment(name, fragment)
        } else {
            getTSFragment(name, fragment)
        }
    }

    private fun getM3U8Fragment(name: String, fragment: String): Mono<ServerResponse> {
        assert(fragment.endsWith(".m3u8"))

        return withStreamCheck(
                name = name,
                fragment = fragment,
                executor = { url ->
                    val response = MalinkaProxy(url).download(fragment)
                    ServerResponse.ok().accessControlAllowOrigin()
                            .contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
                            .writeByteContent(response)
                }
        )
    }

    private fun getTSFragment(name: String, fragment: String): Mono<ServerResponse> {
        assert(fragment.endsWith(".ts"))
        return withStreamCheck(
                name = name,
                fragment = fragment,
                executor = { url ->
                    val response = MalinkaProxy(url).download(fragment)
                    ServerResponse.ok().accessControlAllowOrigin()
                            .contentType(MediaType.parseMediaType("video/mp2t"))
                            .writeByteContent(response)
                }
        )
    }

    private fun withStreamCheck(
            name: String,
            fragment: String,
            executor: (String) -> Mono<ServerResponse>): Mono<ServerResponse> {
        val url = streamsMap[name]?.baseUrl ?:
        return ServerResponse.status(HttpStatus.NOT_FOUND)
                .jsonFail(ErrorResponse("Not Found", "No stream with such name!"))

        return executor(url)
    }

    data class StreamBaseUrlAndFragment(val baseUrl: String, val fragment: String)
}