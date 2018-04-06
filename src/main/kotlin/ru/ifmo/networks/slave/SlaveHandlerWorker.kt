package ru.ifmo.networks.slave

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import ru.ifmo.networks.common.*
import ru.ifmo.networks.common.handlers.HandlerWorker
import ru.ifmo.networks.common.storage.DiskStorage
import ru.ifmo.networks.common.storage.LruStorage
import ru.ifmo.networks.common.storage.Storage

class SlaveHandlerWorker : HandlerWorker {

    private val storage: Storage

    init {
        storage = LruStorage(60,
                DiskStorage(
                        MasterStorage(masterURL)
                )
        )
    }

    companion object {
        var masterURL = ""
    }

    override fun getStreams(serverRequest: ServerRequest): Mono<ServerResponse> {
        return try {
            val restTemplate = RestTemplate()
            val result = restTemplate.getForObject("$masterURL/streams", SomeResponse::class.java)!!
            ServerResponse.ok()
                    .withDefaultHeader()
                    .jsonSuccess(result.result)
        } catch (e: ResourceAccessException) {
            ServerResponse.badRequest()
                    .withDefaultHeader()
                    .build()
        }
    }

    override fun getFragment(serverRequest: ServerRequest): Mono<ServerResponse> {

        try {
            val name = serverRequest.pathVariable("name") ?:
                    return ServerResponse.badRequest().withDefaultHeader().build()
            val fragment = serverRequest.pathVariable("fragment") ?:
                    return ServerResponse.badRequest().withDefaultHeader().build()
            return if (!fragment.endsWith(".m3u8")) {
                fragmentResponse(storage.getFragment(StreamInfo(name, fragment)), "video/mp2t")
            } else {
                queryM3U8FromMaster(name, fragment)
            }
        } catch (e: ResourceAccessException) {
            return fragmentResponse(null, "")
        }

    }

    private fun queryM3U8FromMaster(name: String, fragment: String): Mono<ServerResponse> {
        val restTemplate = RestTemplate()
        val response = restTemplate.exchange("$masterURL/streams/$name/$fragment", HttpMethod.GET, null, ByteArray::class.java)

        return fragmentResponse(
                if (response.statusCode != HttpStatus.OK) null else response.body,
                "application/vnd.apple.mpegurl"
        )
    }

    private fun fragmentResponse(data: ByteArray?, mediaType: String): Mono<ServerResponse> {
        return if (data == null) {
            ServerResponse.badRequest()
                    .withDefaultHeader()
                    .build()
        } else {
            ServerResponse.ok().withDefaultHeader()
                    .contentType(MediaType.parseMediaType(mediaType))
                    .writeByteContent(data)
        }
    }

    class SomeResponse(result: StreamsResponse) : Response<StreamsResponse>(result)

    data class StreamsResponse(
            val streams: List<StreamInfo>
    )
}
