package ru.ifmo.networks.slave

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import ru.ifmo.networks.balancer.BalancerHandler
import ru.ifmo.networks.common.configuration.AppConfig
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import com.sun.jersey.core.header.LinkHeader.uri
import jdk.nashorn.internal.runtime.ScriptingFunctions.readLine
import java.io.InputStreamReader
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL


@SpringBootApplication(scanBasePackages = ["ru.ifmo.networks.common"])
class SlaveApplication

object SlaveRunner {
    fun run(args: Array<String>) {
        AppConfig.isSlave = true
        SlaveHandlerWorker.masterURL = args[1]
        runApplication<SlaveApplication>(*args)

        val heartbeatPeriodInMillis = if (args.size > 2) {
            args[2].toLong()
        } else BalancerHandler.cleaningPeriodInMillis / 2

        val timer = Timer()
        timer.scheduleAtFixedRate(heartbeatPeriodInMillis, heartbeatPeriodInMillis) {
            //println("sending heartbeat")

            val obj = URL(SlaveHandlerWorker.masterURL + "/balancer/heartbeat")
            //println("sending to url ${obj.host}")
            val connection = obj.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val input = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuffer()

            while (true) {
                val inputLine = input.readLine() ?: break
                response.append(inputLine)
            }
            input.close()

            println(response.toString())
        }
    }
}