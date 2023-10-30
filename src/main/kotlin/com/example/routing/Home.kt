package com.example.routing

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.db.Mongo
import com.example.model.*
import com.example.util.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.home(mongo: Mongo, jwtConfig: JwtConfig) {
    route(Paths.Home.value) {
        post(Paths.SignUp.value) {
            val signIn = call.receive<AuthBody>()
            if (!signIn.checkEmpty()) {
                return@post call.respond(
                    HttpStatusCode.NotImplemented, ErrResponse(status = Status.NotRegistered)
                )
            }

            mongo.insertOne(signIn).onSuccess {
                return@post call.respond(
                    HttpStatusCode.Created, OkResponse(status = Status.Registered, data = null)
                )
            }.onFailure {
                call.respond(
                    HttpStatusCode.NotImplemented, ErrResponse(status = Status.NotRegistered, message = it.message)
                )
            }
        }

        post(Paths.LogIn.value) {
            val logIn = call.receive<AuthBody>()
            val tokens = genToken(jwtConfig, logIn, TokenKind.At) to genToken(jwtConfig, logIn, TokenKind.Rt)
            if (tokens.first.isFailure || tokens.second.isFailure) return@post call.respond(
                HttpStatusCode.NotImplemented, ErrResponse(Status.NotGeneratedToken)
            )

            tokens.first.onSuccess { a ->
                tokens.second.onSuccess { r ->
                    mongo.updateRt(logIn, r).onFailure {
                        return@post call.respond(
                            HttpStatusCode.NotImplemented, ErrResponse(status = Status.NotUpdatedRt, message = it.message)
                        )
                    }.onSuccess {
                        call.respond(
                            HttpStatusCode.OK, OkResponse(status = Status.Login, data = AuthToken(at = a, rt = r))
                        )
                    }
                }
            }
        }
    }
}

private enum class TokenKind {
    At, Rt
}

private fun AuthBody.checkEmpty() = email.isNotEmpty() && password.isNotEmpty()

private fun genToken(
    jwtConfig: JwtConfig, signIn: AuthBody, tokenKind: TokenKind
): Result<String> {
    return try {
        Result.success(
            JWT.create().withAudience(jwtConfig.audience).withIssuer(jwtConfig.issuer)
                .withClaim(Fields.Email.value, signIn.email).withIssuedAt(issuedAt)
                .withExpiresAt(if (tokenKind == TokenKind.At) atTokenExpiration else rtTokenExpiration)
                .sign(Algorithm.HMAC256(jwtConfig.secret))
        )
    } catch (e: Exception) {
        Result.failure(Throwable(e.message))
    }
}
