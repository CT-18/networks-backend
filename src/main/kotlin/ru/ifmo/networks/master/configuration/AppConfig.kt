package ru.ifmo.networks.master.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.ifmo.networks.master.handlers.HandlerWorker
import ru.ifmo.networks.master.handlers.MasterHandlerWorker
import ru.ifmo.networks.master.handlers.SlaveHandlerWorker

@Configuration
class AppConfig {

    val handlerWorker: HandlerWorker
        @Bean
        get() = if (isSlave)
            SlaveHandlerWorker()
        else
            MasterHandlerWorker()

    companion object {
        var isSlave = false
    }
}
