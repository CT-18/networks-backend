package ru.ifmo.networks.master.handlers

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class SlaveHandlerWorker : HandlerWorker {
    override fun getFragment(serverRequest: ServerRequest): Mono<ServerResponse> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getStreams(serverRequest: ServerRequest): Mono<ServerResponse> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}