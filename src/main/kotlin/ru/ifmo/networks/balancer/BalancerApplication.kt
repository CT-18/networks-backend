package ru.ifmo.networks.balancer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class BalancerApplication

object BalancerRunner {
    fun run(args: Array<String>) {
        if (args.size > 1) {
            BalancerHandler.cleaningPeriodInMillis = args[1].toLong()
        }
        runApplication<BalancerApplication>(*args)
    }
}