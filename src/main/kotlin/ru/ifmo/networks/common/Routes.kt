package ru.ifmo.networks.common

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.router
import ru.ifmo.networks.common.handlers.Handler

/**
 * Routes of server
 *
 * @author Danil Kolikov
 */
@Component
class Routes {

    @Bean
    fun mainRouter(
            handler: Handler
    ) = router {
        "/streams".nest {
            GET("/", handler::getStreams)

            GET("/{name}/{fragment}", handler::getFragment)
        }

        "/heartbeat".nest {
            POST("/", handler::malinkaHeartbeat)
        }
    }
}