package ru.ifmo.networks.master

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import ru.ifmo.networks.common.handlers.HandlerWorker
import ru.ifmo.networks.slave.SlaveHandlerWorker


@SpringBootApplication(scanBasePackages = ["ru.ifmo.networks.common"])
class NetworksApplication


fun main(args: Array<String>) {
    runApplication<NetworksApplication>(*args)
}