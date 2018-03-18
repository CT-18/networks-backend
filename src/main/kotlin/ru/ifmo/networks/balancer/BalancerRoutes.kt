package ru.ifmo.networks.balancer

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.router
import ru.ifmo.networks.common.handlers.Handler

@Component
class BalancerRoutes {

    @Bean
    fun balancerRouter(
            handler: BalancerHandler
    ) = router {
        "/balancer".nest {
            GET("/", handler::getNodeUrl)
        }
    }
}