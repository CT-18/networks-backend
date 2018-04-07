package ru.ifmo.networks.slave

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import ru.ifmo.networks.balancer.BalancerHandler
import ru.ifmo.networks.common.configuration.AppConfig
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate


@SpringBootApplication(scanBasePackages = ["ru.ifmo.networks.common"])
class SlaveApplication

object SlaveRunner {
    fun run(args: Array<String>) {
        AppConfig.isSlave = true
        SlaveHandlerWorker.masterURL = args[1]
        runApplication<SlaveApplication>(*args)

        val heartbeatPeriodInMillis = if (args.size > 3) {
            args[3].toLong()
        } else BalancerHandler.cleaningPeriodInMillis / 2

        val timer = Timer()
        timer.scheduleAtFixedRate(heartbeatPeriodInMillis, heartbeatPeriodInMillis) {
            try {
                val port = System.getProperty("server.port", "8080") // 8080 -- default Spring server.port
                val balancerUrl = args[2] + "/balancer/heartbeat?port=$port"
                val response = RestTemplate().exchange(balancerUrl, HttpMethod.GET, null, String::class.java)
                println(response.toString())
            } catch (e: Exception) {
                println("WTF: $e")
                e.printStackTrace()
            }
        }
    }
}