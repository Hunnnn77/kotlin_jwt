package com.example.plugins

import com.auth0.jwt.exceptions.TokenExpiredException
import com.example.config.PayloadFields
import com.example.config.JwtConfig
import com.example.db.Mongo
import com.example.model.*
import com.example.routing.handleCookie
import com.example.util.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*


fun Application.configureSecurity(mongo: Mongo, jwtConfig: JwtConfig, handler: JwtHandler) {
    authentication {
        jwt("jwt") {
            realm = jwtConfig.realm
            verifier(
                handler.builder
            )
            validate { credential ->
                if (credential.payload.getClaim(PayloadFields.Email.value).asString().isNotEmpty()) JWTPrincipal(
                    credential.payload
                ) else throw InvalidPayloadException("invalid payload".toUpperFirst())
            }
            challenge { _, _ ->
                //for mobile? - ex, from shared pref -> set header at client side
                call.parseCookie()?.let { at ->
                    if (at.isEmpty()) throw InvalidTokenException(message = "invalid token".toUpperFirst())

                    handler.verify(at).onFailure { err ->
                        when (err) {
                            //when at expired -> gen at based on rt(not expired, expired: update rt, and regen at)
                            is TokenExpiredException -> {
                                val email = getEmailFromDecoded(at)

                                mongo.fetchRt(email).onFailure {
                                    throw MongoImplException("failed to fetch rt from db".toUpperFirst())
                                }.onSuccess { body ->
                                    handler.verify(body.rt ?: "").onFailure { err ->
                                        when (err) {
                                            is TokenExpiredException -> refreshTokens(handler, body, mongo)
                                            else -> {
                                                mongo.removeRt(body.email).onFailure {
                                                    throw MongoImplException("failed to remove rt from db".toUpperFirst())
                                                }.onSuccess {
                                                    call.handleCookie("")
                                                    throw err
                                                }
                                            }
                                        }
                                    }.onSuccess {
                                        refreshAtOnly(handler, body)
                                    }
                                }
                            }

                            else -> throw err
                        }
                    }
                }

            }
        }
    }
}

private suspend fun JWTChallengeContext.refreshTokens(
    handler: JwtHandler,
    body: Registration,
    mongo: Mongo
) {
    handler.genToken(body, TokenKind.Rt).onFailure {
        throw InvalidTokenException("failed to generate rt".toUpperFirst())
    }.onSuccess {
        mongo.updateRt(body, it).onFailure {
            throw MongoImplException("failed to update rt from db".toUpperFirst())
        }.onSuccess {
            refreshAtOnly(handler, body)
        }
    }
}

private fun JWTChallengeContext.refreshAtOnly(
    handler: JwtHandler,
    body: Registration
) {
    handler.genToken(body, TokenKind.At).onFailure {
        throw InvalidTokenException(message = "failed to generate at".toUpperFirst())
    }.onSuccess {
        call.handleCookie(it)
    }
}
