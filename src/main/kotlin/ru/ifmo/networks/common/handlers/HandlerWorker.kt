package ru.ifmo.networks.common.handlers

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

interface HandlerWorker {
    fun getStreams(serverRequest: ServerRequest): Mono<ServerResponse>

    fun getFragment(serverRequest: ServerRequest): Mono<ServerResponse>
}

