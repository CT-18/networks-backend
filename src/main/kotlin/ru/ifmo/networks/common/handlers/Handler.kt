package ru.ifmo.networks.common.handlers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import ru.ifmo.networks.common.handlers.HandlerWorker

/**
 * BalancerHandler of requests
 *
 * @author Danil Kolikov
 */
@Component
class Handler {

    @Autowired
    lateinit var handlerWorker : HandlerWorker

    fun getStreams(serverRequest: ServerRequest): Mono<ServerResponse> {
        return handlerWorker.getStreams(serverRequest)
    }

    fun getFragment(serverRequest: ServerRequest): Mono<ServerResponse> {
        return handlerWorker.getFragment(serverRequest)
    }
}