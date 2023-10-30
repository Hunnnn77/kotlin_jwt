package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.model.ErrResponse
import com.example.routing.JwtConfig
import com.example.model.Status
import com.example.routing.Fields
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity(jwtConfig: JwtConfig) {
    authentication {
        jwt("jwt") {
            realm = jwtConfig.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtConfig.secret))
                    .withAudience(jwtConfig.audience)
                    .withIssuer(jwtConfig.issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim(Fields.Email.value).asString()
                        .isNotEmpty()
                ) JWTPrincipal(credential.payload) else null
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, ErrResponse(status = Status.Unauthorized))
            }
        }
    }
}
