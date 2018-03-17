package ru.ifmo.networks.slave

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import ru.ifmo.networks.common.*
import ru.ifmo.networks.common.handlers.HandlerWorker
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpMethod
import org.springframework.web.client.ResourceAccessException
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


class SlaveHandlerWorker : HandlerWorker {

    private val masterURL = InputStreamReader(Thread.currentThread().contextClassLoader.getResourceAsStream("master.txt")).readText()

    override fun getStreams(serverRequest: ServerRequest): Mono<ServerResponse> {
        try {
            val restTemplate = RestTemplate()
            val result = restTemplate.getForObject("${masterURL}/streams", SomeResponse::class.java)
            return ServerResponse.ok()
                    .withDefaultHeader()
                    .jsonSuccess(result.result)
        } catch (e: ResourceAccessException) {
            return ServerResponse.badRequest()
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
            if (!fragment.endsWith(".m3u8")) {
                val dirName = createStorage(name)
                if (Files.exists(dirName.resolve(fragment))) {
                    return fragmentResponse(Files.readAllBytes(dirName.resolve(fragment)))
                }
            }

            return queryDataFromMaster(name, fragment)

        } catch (e: ResourceAccessException) {
            return ServerResponse.badRequest()
                    .withDefaultHeader()
                    .build()
        }

    }

    private fun queryDataFromMaster(name: String, fragment: String): Mono<ServerResponse> {
        val restTemplate = RestTemplate()
        val response = restTemplate.exchange("${masterURL}/streams/${name}/${fragment}", HttpMethod.GET, null, ByteArray::class.java)

        if (response.statusCode != HttpStatus.OK) {
            return ServerResponse.badRequest()
                    .withDefaultHeader()
                    .build()
        } else {

            if (!fragment.endsWith(".m3u8")) {
                val dirName = createStorage(name)
                Files.newOutputStream(dirName.resolve(fragment)).use {
                    it.write(response.body)
                }
                return fragmentResponse(response.body)

            } else {
                return ServerResponse.ok().withDefaultHeader()
                        .contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
                        .writeByteContent(response.body)
            }
        }
    }

    private fun fragmentResponse(data: ByteArray): Mono<ServerResponse> {
        return ServerResponse.ok().withDefaultHeader()
                .contentType(MediaType.parseMediaType("video/mp2t"))
                .writeByteContent(data)
    }

    private fun createStorage(name: String): Path {
        var path = Paths.get(name)
        if (Files.isDirectory(path)) return path
        if (!Files.exists(path)) return Files.createDirectory(path)

        var i = 0
        while (i < 255) {
            path = Paths.get("${name}${i++}")
            if (Files.isDirectory(path)) return path
            if (!Files.exists(path)) return Files.createDirectory(path)
        }

        throw IllegalStateException("Can't create directory for ${name}")
    }

    class SomeResponse(result: StreamsResponse) : Response<StreamsResponse>(result)

    data class StreamsResponse(
            val streams: List<StreamInfo>
    )
}
