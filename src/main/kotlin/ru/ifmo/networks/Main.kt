package ru.ifmo.networks

import ru.ifmo.networks.balancer.BalancerRunner
import ru.ifmo.networks.master.MasterRunner
import ru.ifmo.networks.slave.SlaveRunner

fun main(args: Array<String>) {
    when (args.getOrNull(0)?.toLowerCase()) {
        "master" -> MasterRunner.run(args)
        "slave" -> SlaveRunner.run(args)
        "balancer" -> BalancerRunner.run(args)
        else -> showUsage()
    }
}

fun showUsage() {
    System.err.println("args[0] must be in { master, slave, balancer }.")
}
