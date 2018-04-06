package ru.ifmo.networks.common.storage

class LruMap<K, V>(private val capacity: Int): LinkedHashMap<K, V>() {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return size > capacity
    }
}