package ru.ifmo.networks.common.configuration

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import ru.ifmo.networks.common.handlers.HandlerWorker
import ru.ifmo.networks.master.MasterHandlerWorker
import ru.ifmo.networks.slave.SlaveApplication
import ru.ifmo.networks.slave.SlaveHandlerWorker

@Component
class AppConfig {
    val handlerWorker: HandlerWorker
        @Bean
        get() = if (isSlave) {
            SlaveHandlerWorker()
        } else {
            MasterHandlerWorker()
        }

    companion object {
        var isSlave = false
    }
}