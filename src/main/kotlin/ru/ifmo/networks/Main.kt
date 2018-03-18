package ru.ifmo.networks

import ru.ifmo.networks.balancer.BalancerRunner
import ru.ifmo.networks.master.MasterRunner
import ru.ifmo.networks.slave.SlaveRunner

fun main(args: Array<String>) {
    when (args[0]) {
        "master" -> MasterRunner.run(args)
        "slave" -> SlaveRunner.run(args)
        "balancer" -> BalancerRunner.run(args)
    }
}