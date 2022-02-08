package com.yandex.xplat.common

import android.net.Uri
import java.io.File
import java.net.URLDecoder
import java.net.URLEncoder

class DefaultUri(val uri: Uri) : com.yandex.xplat.common.Uri {
    override fun getScheme(): String? = uri.scheme
    override fun getHost(): String? = uri.host?.takeIf(String::isNotEmpty)
    override fun getPath(): String? = uri.path
    override fun getPathSegments(): YSArray<String> = uri.pathSegments.toMutableList()
    override fun getQuery(): String? = uri.query
    override fun getQueryParameter(name: String): String? = uri.getQueryParameter(name)
    override fun getQueryParameterNames(): YSArray<String> = uri.queryParameterNames.toMutableList()
    override fun getQueryParameters(name: String): YSArray<String> = uri.getQueryParameters(name).toMutableList()
    override fun getAllQueryParameters(): YSArray<UriQueryParameter> = getQueryParameterNames().flatMap { name ->
        getQueryParameters(name).map { UriQueryParameter(name, it) }
    }.toMutableList()
    override fun getFragment(): String? = uri.fragment
    override fun getAbsoluteString(): String = uri.toString()
    override fun isFileUri(): Boolean = uri.scheme == "file"
    override fun builder(): UriBuilder = DefaultUriBuilder(uri.buildUpon())
}

class DefaultUriBuilder(val builder: Uri.Builder) : UriBuilder {
    override fun setScheme(value: String): UriBuilder {
        builder.scheme(value)
        return this
    }
    override fun setHost(value: String): UriBuilder {
        builder.authority(value)
        return this
    }
    override fun setPath(value: String): UriBuilder {
        builder.path(if (!value.startsWith("/")) "/$value" else value)
        return this
    }
    override fun appendQueryParameter(name: String, value: String): UriBuilder {
        builder.appendQueryParameter(name, value)
        return this
    }
    override fun setAllQueryParameters(values: YSArray<UriQueryParameter>): UriBuilder {
        builder.clearQuery()
        values.forEach { builder.appendQueryParameter(it.name, it.value) }
        return this
    }
    override fun clearQuery(): UriBuilder {
        builder.clearQuery()
        return this
    }
    override fun setFragment(value: String): UriBuilder {
        builder.fragment(value)
        return this
    }
    override fun build(): com.yandex.xplat.common.Uri {
        return DefaultUri(builder.build())
    }
}

object Uris {
    fun fromFilePath(filePath: String): com.yandex.xplat.common.Uri {
        return DefaultUri(Uri.fromFile(File(filePath)))
    }

    fun fromString(value: String): com.yandex.xplat.common.Uri? {
        return try {
            val parsedAndValidUri = java.net.URI(value).toString()
            DefaultUri(Uri.parse(parsedAndValidUri))
        } catch (e: Throwable) {
            null
        }
    }
}

fun percentEncode(value: String, plusIsSpace: Boolean): String {
    val encoded = URLEncoder.encode(value, Charsets.UTF_8.name())
    return if (plusIsSpace) encoded else encoded.replace("+", "%20")
}

fun percentDecode(value: String, plusIsSpace: Boolean): String {
    val encoded = if (plusIsSpace) value else value.replace("+", "%2B")
    return URLDecoder.decode(encoded, Charsets.UTF_8.name())
}
