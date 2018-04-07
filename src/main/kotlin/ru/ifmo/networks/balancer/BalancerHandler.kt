package ru.ifmo.networks.balancer

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import ru.ifmo.networks.common.jsonSuccess
import ru.ifmo.networks.common.withDefaultHeader
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max

@Component
class BalancerHandler {

    companion object {
        private var urls = ConcurrentHashMap<String, Long>()
        private val rnd = Random()
        var cleaningPeriodInMillis: Long = 30_000
    }

    private fun filterDeadUrls() {
        urls.forEach(urls.size.toLong()) { addr, lastUpdate ->
            if (Date().time - lastUpdate > cleaningPeriodInMillis) {
                urls.remove(addr, lastUpdate)
            }
        }
    }

    fun getNodeUrl(serverRequest: ServerRequest): Mono<ServerResponse> {
        filterDeadUrls()
        val res = urls.keys.toList().getOrNull(rnd.nextInt(max(1, urls.size)))
        return if (res == null) {
            ServerResponse.notFound().build()
        } else {
            ServerResponse.ok()
                    .withDefaultHeader()
                    .jsonSuccess(mapOf("result" to res))
        }
    }

    fun heartBeat(serverRequest: ServerRequest): Mono<ServerResponse> {
        val slaveAddr = (serverRequest.attribute("request-ip").get() as InetSocketAddress)
                .address.hostAddress
        val slavePort = serverRequest.queryParam("port").orElse("9000")
        val address = "$slaveAddr:$slavePort"

        val lastUpdate = urls[address]
        val result = if (lastUpdate != null) {
            "update"
        } else {
            "register"
        }
        val newTime = Date().time
        urls[address] = newTime
        val lastUpdateDesc = if (lastUpdate == null) {
            "never"
        } else {
            "${((newTime - lastUpdate) / 1000)} seconds ago"
        }
        return ServerResponse.ok()
                .withDefaultHeader()
                .jsonSuccess(mapOf(
                        "result" to result,
                        "last update" to lastUpdateDesc,
                        "slave address" to slaveAddr,
                        "heartbeat period in millis" to cleaningPeriodInMillis))
    }
}