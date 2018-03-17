package ru.ifmo.networks.slave

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import ru.ifmo.networks.common.*
import ru.ifmo.networks.common.handlers.HandlerWorker
import org.springframework.web.client.RestTemplate
import org.springframework.http.ResponseEntity
import java.net.URL
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import javax.ws.rs.GET






class SlaveHandlerWorker : HandlerWorker {

    private val masterURL = "http://127.0.0.1:8080"

    override fun getStreams(serverRequest: ServerRequest): Mono<ServerResponse>  {
        val restTemplate = RestTemplate()
        val result = restTemplate.getForObject("${masterURL}/streams", SomeResponse::class.java)
        return ServerResponse.ok().jsonSuccess(result.result)
    }


    override fun getFragment(serverRequest: ServerRequest): Mono<ServerResponse> {
        val name = serverRequest.pathVariable("name") ?: return ServerResponse.badRequest().build()
        val fragment = serverRequest.pathVariable("fragment") ?: return ServerResponse.badRequest().build()

        val restTemplate = RestTemplate()
        val response = restTemplate.exchange("${masterURL}/streams/${name}/${fragment}", HttpMethod.GET, null, ByteArray::class.java)

        if (response.statusCode != HttpStatus.OK) {
            return ServerResponse.badRequest().build()
        } else {
            return ServerResponse.ok().accessControlAllowOrigin()
                    .contentType(MediaType.parseMediaType("video/mp2t"))
                    .writeByteContent(response.body)
        }
    }


    class SomeResponse(result : StreamsResponse) : Response<StreamsResponse>(result)

    data class StreamsResponse (
        val streams : List<StreamInfo>
    )
}
