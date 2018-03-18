package ru.ifmo.networks.balancer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.ifmo.networks.common.configuration.AppConfig
import ru.ifmo.networks.common.handlers.HandlerWorker
import ru.ifmo.networks.master.MasterHandlerWorker


@SpringBootApplication
class BalancerApplication

object BalancerRunner {
    fun run(args: Array<String>) {
        runApplication<BalancerApplication>(*args)
    }
}