package com.example.routing

import io.ktor.http.*
import io.ktor.server.application.*

fun ApplicationCall.handleCookie(token: String, generate: Boolean = token.isNotEmpty()) {
    val cookie = if (generate) Cookie(
        name = "jwt", value = token, maxAge = 3600, path = "/"
    ) else Cookie(
        name = "jwt", value = "", maxAge = 0, path = "/"
    )
    response.cookies.append(cookie)
}

