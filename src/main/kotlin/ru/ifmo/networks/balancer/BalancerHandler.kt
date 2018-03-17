package ru.ifmo.networks.balancer

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import ru.ifmo.networks.common.jsonSuccess
import ru.ifmo.networks.common.withDefaultHeader
import java.io.InputStreamReader
import java.util.*

@Component
class BalancerHandler {

    private val urls = InputStreamReader(Thread.currentThread().contextClassLoader.
            getResourceAsStream("slaves.txt")).readLines().map { t -> Pair(t, true) }

    private val rnd = Random()

    fun getNodeUrl(serverRequest: ServerRequest): Mono<ServerResponse> =
            ServerResponse.ok()
                    .withDefaultHeader()
                    .jsonSuccess(mapOf("result" to urls.filter { t -> t.second }.shuffled().firstOrNull()?.first))
}