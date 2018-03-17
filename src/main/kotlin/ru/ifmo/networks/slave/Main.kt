package ru.ifmo.networks.slave

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.ifmo.networks.common.handlers.HandlerWorker
import ru.ifmo.networks.master.MasterHandlerWorker


@SpringBootApplication(scanBasePackages = ["ru.ifmo.networks"])
class SlaveApplication

/**
 * Network Main server starter
 *
 * @author Danil Kolikov
 */



fun main(args: Array<String>) {
    runApplication<SlaveApplication>(*args)
}