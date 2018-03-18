package ru.ifmo.networks.master

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["ru.ifmo.networks.common"])
class MasterApplication

object MasterRunner {
    fun run(args: Array<String>) {
        runApplication<MasterApplication>(*args)
    }
}