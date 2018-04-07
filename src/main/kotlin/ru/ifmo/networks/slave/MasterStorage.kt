package ru.ifmo.networks.slave

import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import ru.ifmo.networks.common.StreamInfo
import ru.ifmo.networks.common.storage.Storage

class MasterStorage(val masterURL: String): Storage {
    override fun getFragment(streamInfo: StreamInfo): ByteArray? {
        val restTemplate = RestTemplate()
        return restTemplate.exchange(
                "$masterURL/streams/${streamInfo.name}/${streamInfo.fragment}",
                HttpMethod.GET,
                null,
                ByteArray::class.java
        ).body
    }
}