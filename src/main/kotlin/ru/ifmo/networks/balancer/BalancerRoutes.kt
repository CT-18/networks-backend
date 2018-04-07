package ru.ifmo.networks.balancer

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.router
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain

@Component
class BalancerRoutes {
    @Bean
    fun sampleWebFilter(): WebFilter {
        return WebFilter { e: ServerWebExchange, c: WebFilterChain ->
            e.attributes["request-ip"] = e.request.remoteAddress!!
            c.filter(e)
        }
    }

    @Bean
    fun balancerRouter(handler: BalancerHandler) = router {
        "/balancer".nest {
            GET("/", handler::getNodeUrl)

            GET("/heartbeat", handler::heartBeat)
        }
    }
}