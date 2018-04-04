package ru.ifmo.networks.master

data class HeartbeatRequest(val name: String, val fragment: String)
data class WithIP<T>(val ip : String, val data : T)