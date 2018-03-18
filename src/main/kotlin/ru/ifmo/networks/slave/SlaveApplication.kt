package ru.ifmo.networks.slave

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.ifmo.networks.common.configuration.AppConfig
import ru.ifmo.networks.common.handlers.HandlerWorker
import ru.ifmo.networks.master.MasterHandlerWorker


@SpringBootApplication(scanBasePackages = ["ru.ifmo.networks.common"])
class SlaveApplication

object SlaveRunner {
    fun run(args: Array<String>) {
        AppConfig.isSlave = true
        SlaveHandlerWorker.masterURL = args[1]
        runApplication<SlaveApplication>(*args)
    }
}