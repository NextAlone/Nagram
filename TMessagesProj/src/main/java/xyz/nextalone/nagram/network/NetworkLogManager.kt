package xyz.nextalone.nagram.network

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.header
import io.ktor.client.request.options
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NetworkLogManager {

    private val scope = CoroutineScope(Dispatchers.IO)

    fun get(
        url: String,
        headers: Map<String, String> = emptyMap(),
        params: Map<String, String> = emptyMap(),
        onComplete: (suspend () -> Unit)? = null
    ) {
        scope.launch {
            NetworkLoggingInterceptor.executeWithLogging(
                method = "GET",
                url = url,
                headers = headers,
                requestParams = params
            ) {
                get(url) {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                    params.forEach { (key, value) ->
                        parameter(key, value)
                    }
                }
            }
            onComplete?.invoke()
        }
    }

    fun post(
        url: String,
        body: String,
        headers: Map<String, String> = emptyMap(),
        params: Map<String, String> = emptyMap(),
        onComplete: (suspend () -> Unit)? = null
    ) {
        scope.launch {
            NetworkLoggingInterceptor.executeWithLogging(
                method = "POST",
                url = url,
                headers = headers,
                requestBody = body,
                requestParams = params
            ) {
                post(url) {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                    setBody(body)
                }
            }
            onComplete?.invoke()
        }
    }

    fun put(
        url: String,
        body: String,
        headers: Map<String, String> = emptyMap(),
        params: Map<String, String> = emptyMap(),
        onComplete: (suspend () -> Unit)? = null
    ) {
        scope.launch {
            NetworkLoggingInterceptor.executeWithLogging(
                method = "PUT",
                url = url,
                headers = headers,
                requestBody = body,
                requestParams = params
            ) {
                put(url) {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                    setBody(body)
                }
            }
            onComplete?.invoke()
        }
    }

    fun delete(
        url: String,
        headers: Map<String, String> = emptyMap(),
        params: Map<String, String> = emptyMap(),
        onComplete: (suspend () -> Unit)? = null
    ) {
        scope.launch {
            NetworkLoggingInterceptor.executeWithLogging(
                method = "DELETE",
                url = url,
                headers = headers,
                requestParams = params
            ) {
                delete(url) {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                    params.forEach { (key, value) ->
                        parameter(key, value)
                    }
                }
            }
            onComplete?.invoke()
        }
    }
}

class NetworkRequestBuilder(
    private var url: String,
    private var method: String = "GET",
    private val headersMap: MutableMap<String, String> = mutableMapOf(),
    private val paramsMap: MutableMap<String, String> = mutableMapOf(),
    private var contentBody: String? = null,
    private var binaryBody: ByteArray? = null,
    private var contentType: ContentType? = null
) {
    private var onCompleteCallback: (suspend () -> Unit)? = null

    fun url(url: String): NetworkRequestBuilder {
        this.url = url
        return this
    }

    fun method(method: String): NetworkRequestBuilder {
        this.method = method
        return this
    }

    fun header(key: String, value: String): NetworkRequestBuilder {
        headersMap[key] = value
        return this
    }

    fun headers(block: MutableMap<String, String>.() -> Unit): NetworkRequestBuilder {
        headersMap.block()
        return this
    }

    fun parameter(key: String, value: String): NetworkRequestBuilder {
        paramsMap[key] = value
        return this
    }

    fun parameters(block: MutableMap<String, String>.() -> Unit): NetworkRequestBuilder {
        paramsMap.block()
        return this
    }

    fun setBody(body: String): NetworkRequestBuilder {
        this.contentBody = body
        this.binaryBody = null
        return this
    }

    fun body(body: String): NetworkRequestBuilder {
        this.contentBody = body
        this.binaryBody = null
        return this
    }

    fun setBinaryBody(body: ByteArray): NetworkRequestBuilder {
        this.binaryBody = body
        this.contentBody = null
        return this
    }

    fun binaryBody(body: ByteArray): NetworkRequestBuilder {
        this.binaryBody = body
        this.contentBody = null
        return this
    }

    fun contentType(contentType: ContentType): NetworkRequestBuilder {
        this.contentType = contentType
        headersMap["Content-Type"] = contentType.toString()
        return this
    }

    fun onComplete(block: suspend () -> Unit): NetworkRequestBuilder {
        onCompleteCallback = block
        return this
    }

    fun execute(): NetworkResponse {
        var responseCode = 0
        var responseBody = ""
        var responseBodyBytes: ByteArray? = null
        var responseTime = 0L
        var errorMsg: String? = null

        val startTime = System.currentTimeMillis()

        try {
            val response: HttpResponse = kotlinx.coroutines.runBlocking {
                NetworkLoggingInterceptor.executeWithLogging(
                    method = method.uppercase(),
                    url = url,
                    headers = headersMap.toMap(),
                    requestBody = contentBody,
                    requestParams = paramsMap.toMap()
                ) {
                    when (method.uppercase()) {
                        "GET" -> get(url) {
                            applyHeadersAndParams()
                        }
                        "POST" -> post(url) {
                            applyHeadersAndParams()
                            when {
                                binaryBody != null -> setBody(binaryBody!!)
                                contentBody != null -> setBody(contentBody!!)
                            }
                        }
                        "PUT" -> put(url) {
                            applyHeadersAndParams()
                            when {
                                binaryBody != null -> setBody(binaryBody!!)
                                contentBody != null -> setBody(contentBody!!)
                            }
                        }
                        "DELETE" -> delete(url) {
                            applyHeadersAndParams()
                        }
                        "PATCH" -> patch(url) {
                            applyHeadersAndParams()
                            when {
                                binaryBody != null -> setBody(binaryBody!!)
                                contentBody != null -> setBody(contentBody!!)
                            }
                        }
                        "HEAD" -> head(url) {
                            applyHeadersAndParams()
                        }
                        "OPTIONS" -> options(url) {
                            applyHeadersAndParams()
                        }
                        else -> get(url) {
                            applyHeadersAndParams()
                        }
                    }
                }
            }

            responseCode = response.status.value
            responseTime = System.currentTimeMillis() - startTime

            kotlinx.coroutines.runBlocking {
                try {
                    if (binaryBody != null || contentType?.toString()?.contains("application/dns-message") == true) {
                        val channel = response.bodyAsChannel()
                        val buffer = ByteArray(8192)
                        val output = java.io.ByteArrayOutputStream()
                        while (true) {
                            val read = channel.readAvailable(buffer)
                            if (read <= 0) break
                            output.write(buffer, 0, read)
                        }
                        responseBodyBytes = output.toByteArray()
                        responseBody = responseBodyBytes!!.toString(Charsets.UTF_8)
                    } else {
                        responseBody = response.bodyAsText()
                    }
                } catch (e: Exception) {
                    responseBody = "Unable to read response body: ${e.message}"
                }
            }

            kotlinx.coroutines.runBlocking {
                onCompleteCallback?.invoke()
            }

            return NetworkResponse(
                statusCode = responseCode,
                body = responseBody,
                bodyBytes = responseBodyBytes,
                headers = response.headers.entries().joinToString("\n") { "${it.key}: ${it.value.joinToString(", ")}" },
                responseTime = responseTime,
                isSuccess = response.status.isSuccess()
            )

        } catch (e: Exception) {
            errorMsg = e.message ?: "Unknown error"
            responseTime = System.currentTimeMillis() - startTime

            return NetworkResponse(
                statusCode = 0,
                body = "",
                bodyBytes = null,
                headers = "",
                responseTime = responseTime,
                isSuccess = false,
                error = errorMsg
            )
        }
    }

    private fun io.ktor.client.request.HttpRequestBuilder.applyHeadersAndParams() {
        headersMap.forEach { (key, value) ->
            header(key, value)
        }
        paramsMap.forEach { (key, value) ->
            parameter(key, value)
        }
    }

    suspend fun executeAsync(): NetworkResponse {
        return execute()
    }

    fun launch() {
        CoroutineScope(Dispatchers.IO).launch {
            execute()
        }
    }

    companion object {
        fun get(url: String, block: NetworkRequestBuilder.() -> Unit): NetworkRequestBuilder {
            return NetworkRequestBuilder(url, "GET").apply(block)
        }

        fun post(url: String, block: NetworkRequestBuilder.() -> Unit): NetworkRequestBuilder {
            return NetworkRequestBuilder(url, "POST").apply(block)
        }

        fun put(url: String, block: NetworkRequestBuilder.() -> Unit): NetworkRequestBuilder {
            return NetworkRequestBuilder(url, "PUT").apply(block)
        }

        fun delete(url: String, block: NetworkRequestBuilder.() -> Unit): NetworkRequestBuilder {
            return NetworkRequestBuilder(url, "DELETE").apply(block)
        }

        fun patch(url: String, block: NetworkRequestBuilder.() -> Unit): NetworkRequestBuilder {
            return NetworkRequestBuilder(url, "PATCH").apply(block)
        }

        fun head(url: String, block: NetworkRequestBuilder.() -> Unit): NetworkRequestBuilder {
            return NetworkRequestBuilder(url, "HEAD").apply(block)
        }

        fun options(url: String, block: NetworkRequestBuilder.() -> Unit): NetworkRequestBuilder {
            return NetworkRequestBuilder(url, "OPTIONS").apply(block)
        }
    }
}

data class NetworkResponse(
    val statusCode: Int,
    val body: String,
    val bodyBytes: ByteArray? = null,
    val headers: String,
    val responseTime: Long,
    val isSuccess: Boolean,
    val error: String? = null
) {
    fun status(): HttpStatusCode = HttpStatusCode(statusCode, "")

    fun isSuccessful(): Boolean = isSuccess

    fun isRedirect(): Boolean = statusCode in 300..399

    fun isClientError(): Boolean = statusCode in 400..499

    fun isServerError(): Boolean = statusCode in 500..599

    fun header(name: String): String? {
        return headers.lines()
            .find { it.startsWith("$name:", ignoreCase = true) }
            ?.substringAfter(":")
            ?.trim()
    }

    fun bodyAsBytes(): ByteArray? = bodyBytes

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NetworkResponse

        if (statusCode != other.statusCode) return false
        if (body != other.body) return false
        if (bodyBytes != null) {
            if (other.bodyBytes == null) return false
            if (!bodyBytes.contentEquals(other.bodyBytes)) return false
        } else if (other.bodyBytes != null) return false
        if (headers != other.headers) return false
        if (responseTime != other.responseTime) return false
        if (isSuccess != other.isSuccess) return false
        if (error != other.error) return false

        return true
    }

    override fun hashCode(): Int {
        var result = statusCode
        result = 31 * result + body.hashCode()
        result = 31 * result + (bodyBytes?.contentHashCode() ?: 0)
        result = 31 * result + headers.hashCode()
        result = 31 * result + responseTime.hashCode()
        result = 31 * result + isSuccess.hashCode()
        result = 31 * result + (error?.hashCode() ?: 0)
        return result
    }
}

object NetworkClient {
    private val clientScope = CoroutineScope(Dispatchers.IO)

    val httpClient get() = NetworkLoggingInterceptor.client

    fun get(url: String, block: NetworkRequestBuilder.() -> Unit = {}): NetworkRequestBuilder {
        return NetworkRequestBuilder.get(url, block)
    }

    fun post(url: String, block: NetworkRequestBuilder.() -> Unit = {}): NetworkRequestBuilder {
        return NetworkRequestBuilder.post(url, block)
    }

    fun put(url: String, block: NetworkRequestBuilder.() -> Unit = {}): NetworkRequestBuilder {
        return NetworkRequestBuilder.put(url, block)
    }

    fun delete(url: String, block: NetworkRequestBuilder.() -> Unit = {}): NetworkRequestBuilder {
        return NetworkRequestBuilder.delete(url, block)
    }

    fun patch(url: String, block: NetworkRequestBuilder.() -> Unit = {}): NetworkRequestBuilder {
        return NetworkRequestBuilder.patch(url, block)
    }

    fun head(url: String, block: NetworkRequestBuilder.() -> Unit = {}): NetworkRequestBuilder {
        return NetworkRequestBuilder.head(url, block)
    }

    fun options(url: String, block: NetworkRequestBuilder.() -> Unit = {}): NetworkRequestBuilder {
        return NetworkRequestBuilder.options(url, block)
    }

    suspend fun request(
        method: String,
        url: String,
        headers: Map<String, String> = emptyMap(),
        body: String? = null,
        params: Map<String, String> = emptyMap()
    ): NetworkResponse {
        val response: HttpResponse = NetworkLoggingInterceptor.executeWithLogging(
            method = method.uppercase(),
            url = url,
            headers = headers,
            requestBody = body,
            requestParams = params
        ) {
            when (method.uppercase()) {
                "GET" -> get(url) {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                    params.forEach { (key, value) ->
                        parameter(key, value)
                    }
                }
                "POST" -> post(url) {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                    body?.let { setBody(it) }
                }
                "PUT" -> put(url) {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                    body?.let { setBody(it) }
                }
                "DELETE" -> delete(url) {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                    params.forEach { (key, value) ->
                        parameter(key, value)
                    }
                }
                "PATCH" -> patch(url) {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                    body?.let { setBody(it) }
                }
                "HEAD" -> head(url) {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                    params.forEach { (key, value) ->
                        parameter(key, value)
                    }
                }
                "OPTIONS" -> options(url) {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                    params.forEach { (key, value) ->
                        parameter(key, value)
                    }
                }
                else -> get(url) {
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                    params.forEach { (key, value) ->
                        parameter(key, value)
                    }
                }
            }
        }

        val responseBody: String = try {
            response.bodyAsText()
        } catch (e: Exception) {
            "Unable to read response body: ${e.message}"
        }

        return NetworkResponse(
            statusCode = response.status.value,
            body = responseBody,
            headers = response.headers.entries().joinToString("\n") { "${it.key}: ${it.value.joinToString(", ")}" },
            responseTime = 0,
            isSuccess = response.status.isSuccess()
        )
    }
}

fun io.ktor.client.request.HttpRequestBuilder.jsonBody(json: String): Unit {
    setBody(json)
}

fun String.toQueryParams(): Map<String, String> {
    return if (contains("?")) {
        substringAfter("?").split("&").associate { pair ->
            val parts = pair.split("=")
            val key = parts.getOrElse(0) { "" }
            val value = parts.getOrElse(1) { "" }
            key to value
        }
    } else {
        emptyMap()
    }
}

fun Map<String, String>.toQueryString(): String {
    return entries.joinToString("&") { "${it.key}=${it.value}" }
}

fun String.appendQueryParams(params: Map<String, String>): String {
    val separator = if (contains("?")) "&" else "?"
    return this + separator + params.toQueryString()
}
