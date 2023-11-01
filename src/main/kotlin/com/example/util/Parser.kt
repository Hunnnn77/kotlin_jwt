package com.example.util

import com.example.config.PayloadFields
import com.auth0.jwt.JWT
import io.ktor.server.application.*

private fun getToken(token: String) = JWT.decode(token)

fun getEmailFromDecoded(token: String, getField: String = PayloadFields.Email.value) =
    getToken(token).getClaim(getField).asString() ?: "anonymous"

fun getIatFromDecoded(token: String) =
    getToken(token).issuedAt.time

fun getEatFromDecoded(token: String) =
    getToken(token).expiresAt.time

fun ApplicationCall.parseCookie(): String? = request.cookies["jwt"]
