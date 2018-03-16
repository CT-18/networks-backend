package ru.ifmo.networks

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import reactor.core.publisher.Mono

/**
 * Handler of requests
 *
 * @author Danil Kolikov
 */
@Component
class Handler {

    fun getStreams(serverRequest: ServerRequest): Mono<ServerResponse> =
            ok().jsonSuccess(StreamsResponse(listOf(
                    StreamInfo("someStream", "stream.url")
            )))

    fun getFragment(serverRequest: ServerRequest): Mono<ServerResponse> {
        val name = serverRequest.pathVariable("name") ?: return badRequest().build()
        val fragment = serverRequest.pathVariable("fragment") ?: return badRequest().build()

        return ok().jsonSuccess(mapOf(
                "name" to name,
                "fragment" to fragment
        ))
    }
}