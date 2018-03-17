package ru.ifmo.networks.common

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

fun ServerResponse.BodyBuilder.withDefaultHeader(): ServerResponse.BodyBuilder =
        accessControlAllowOrigin()

fun ServerResponse.BodyBuilder.accessControlAllowOrigin(): ServerResponse.BodyBuilder =
        header("Access-Control-Allow-Origin", "*")

fun ServerResponse.BodyBuilder.writeByteContent(content: ByteArray): Mono<ServerResponse> =
        header("Accept-Ranges", "bytes")
                .header("Access-Control-Expose-Headers", "Content-Length")
                .contentLength(content.size.toLong())
                .body(BodyInserters.fromObject(content))

fun ServerResponse.BodyBuilder.json(): ServerResponse.BodyBuilder =
        contentType(MediaType.APPLICATION_JSON_UTF8)

fun <T> ServerResponse.BodyBuilder.json(value: T): Mono<ServerResponse> = json().body(BodyInserters.fromObject(value))

fun <T> ServerResponse.BodyBuilder.jsonSuccess(value: T): Mono<ServerResponse> = json(Response(value))

fun ServerResponse.BodyBuilder.jsonFail(errorResponse: ErrorResponse) = json(Response<Unit>(errorResponse))
