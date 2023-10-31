    package com.example.plugins

import com.example.routing.*
import com.example.util.JwtHandler
import com.example.util.toUpperFirst
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*


fun Application.configureSecurity(jwtConfig: JwtConfig, handler: JwtHandler) {
    authentication {
        jwt("jwt") {
            realm = jwtConfig.realm
            verifier(
                handler.builder
            )
            validate { credential ->
                if (credential.payload.getClaim(Fields.Email.value).asString()
                        .isNotEmpty()
                ) JWTPrincipal(credential.payload) else throw InvalidPayloadException("invalid payload".toUpperFirst())
            }
            challenge { _, _ ->
                call.request.headers["Authorization"].let {
                    if (it.isNullOrEmpty()) throw InvalidTokenException(message = "invalid token".toUpperFirst())

                    handler.verify(it).onFailure { err ->
                        throw AuthorizationException(message = err.message)
                    }
                }
            }
        }
    }
}
