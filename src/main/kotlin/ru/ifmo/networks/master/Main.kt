package ru.ifmo.networks.master

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import com.sun.java.swing.plaf.windows.resources.windows
import org.springframework.context.annotation.Bean
import ru.ifmo.networks.master.configuration.AppConfig


@SpringBootApplication
class NetworksApplication

/**
 * Network Main server starter
 *
 * @author Danil Kolikov
 */
fun main(args: Array<String>) {
    AppConfig.isSlave = false
    runApplication<NetworksApplication>(*args)
}