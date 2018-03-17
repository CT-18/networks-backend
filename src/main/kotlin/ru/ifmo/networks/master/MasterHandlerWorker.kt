package ru.ifmo.networks.master

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import reactor.core.publisher.Mono
import ru.ifmo.networks.common.*
import ru.ifmo.networks.common.handlers.HandlerWorker

class MasterHandlerWorker : HandlerWorker {

    private val map = SelfClearingMap()

    override fun malinkaHeartbeat(serverRequest: ServerRequest): Mono<ServerResponse> {
        return serverRequest.body(BodyExtractors.toMono(HeartbeatRequest::class.java))
                .map { request ->
                    map.update(request.name, SelfClearingMap.StreamBaseUrlAndFragment(request.baseUrl, request.fragment))
                }
                .flatMap {
                    ok().jsonSuccess("Ok")
                }
    }

    override fun getStreams(serverRequest: ServerRequest): Mono<ServerResponse> =
            ok()
                    .withDefaultHeader()
                    .jsonSuccess(StreamsResponse(
                    map.asList()
                            .map { pair -> StreamInfo(pair.first, pair.second.fragment) }
            ))

    override fun getFragment(serverRequest: ServerRequest): Mono<ServerResponse> {
        val name = serverRequest.pathVariable("name") ?: return badRequest().build()
        val fragment = serverRequest.pathVariable("fragment") ?: return badRequest().build()

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
                executor = { url ->
                    val response = MalinkaProxy(url).download(fragment)
                    ok().withDefaultHeader()
                            .contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
                            .writeByteContent(response)
                }
        )
    }

    private fun getTSFragment(name: String, fragment: String): Mono<ServerResponse> {
        assert(fragment.endsWith(".ts"))
        return withStreamCheck(
                name = name,
                executor = { url ->
                    val response = MalinkaProxy(url).download(fragment)
                    ok().withDefaultHeader()
                            .contentType(MediaType.parseMediaType("video/mp2t"))
                            .writeByteContent(response)
                }
        )
    }

    private fun withStreamCheck(
            name: String,
            executor: (String) -> Mono<ServerResponse>): Mono<ServerResponse> {
        val url = map.getStream(name)?.baseUrl ?:
                return status(HttpStatus.NOT_FOUND)
                        .withDefaultHeader()
                        .jsonFail(ErrorResponse("Not Found", "No stream with such name!"))

        return executor(url)
    }

}