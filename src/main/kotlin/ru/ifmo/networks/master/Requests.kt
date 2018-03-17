package ru.ifmo.networks.master

data class HeartbeatRequest(val baseUrl: String, val name: String, val fragment: String)