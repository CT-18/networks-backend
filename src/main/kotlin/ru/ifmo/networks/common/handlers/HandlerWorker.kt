package ru.ifmo.networks.common.handlers

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import ru.ifmo.networks.common.ErrorResponse
import ru.ifmo.networks.common.jsonFail

interface HandlerWorker {
    fun malinkaHeartbeat(serverRequest: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.badRequest().jsonFail(
                ErrorResponse("Not supported", "Does not support heartbeat!")
        )
    }

    fun getStreams(serverRequest: ServerRequest): Mono<ServerResponse>

    fun getFragment(serverRequest: ServerRequest): Mono<ServerResponse>
}

