package ru.ifmo.networks

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono


fun ServerResponse.BodyBuilder.json(): ServerResponse.BodyBuilder = contentType(MediaType.APPLICATION_JSON_UTF8)

fun <T> ServerResponse.BodyBuilder.json(value: T): Mono<ServerResponse> = json().body(BodyInserters.fromObject(value))

fun <T> ServerResponse.BodyBuilder.jsonSuccess(value: T): Mono<ServerResponse> = json(Response(value))

fun ServerResponse.BodyBuilder.jsonFail(errorResponse: ErrorResponse) = json(Response<Unit>(errorResponse))

fun ServerResponse.BodyBuilder.hls(value: String) =
        body(BodyInserters.fromObject(value))