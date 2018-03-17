package ru.ifmo.networks

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import reactor.core.publisher.Mono
import ru.ifmo.networks.download.Downloader

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
//        val name = serverRequest.pathVariable("name") ?: return badRequest().build()
        val fragment = serverRequest.pathVariable("fragment") ?: return badRequest().build()

        val response = MalinkaProxy("http://10.8.0.3/").download(fragment)
        return ok().header("Access-Control-Allow-Origin", "*").hls(response)
    }
}