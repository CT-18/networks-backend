package ru.ifmo.networks

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NetworksApplication

/**
 * Network Main server starter
 *
 * @author Danil Kolikov
 */
fun main(args: Array<String>) {
    runApplication<NetworksApplication>(*args)
}