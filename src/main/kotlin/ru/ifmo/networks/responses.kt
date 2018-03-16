package ru.ifmo.networks

data class Response<T>(
        val result: T?,
        val error: ErrorResponse?
) {
    constructor(result: T) : this(result, null)

    constructor(error: ErrorResponse): this(null, error)
}

data class ErrorResponse(
        val name: String,
        val message: String
) {
    constructor(throwable: Throwable) : this(
            throwable::class.simpleName ?: "Throwable",
            throwable.message ?: ""
    )
}

data class StreamInfo(
        val name: String,
        val url: String
)

data class StreamsResponse(
        val streams: List<StreamInfo>
)