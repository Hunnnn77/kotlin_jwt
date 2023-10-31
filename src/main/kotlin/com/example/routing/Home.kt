package com.example.routing

import com.example.db.Mongo
import com.example.model.*
import com.example.util.JwtHandler
import com.example.util.TokenKind
import com.example.util.checkEmpty
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.home(mongo: Mongo, handler: JwtHandler) {
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
            val tokens = handler.genToken(logIn, TokenKind.At) to handler.genToken(logIn, TokenKind.Rt)
            if (tokens.first.isFailure || tokens.second.isFailure) return@post call.respond(
                HttpStatusCode.NotImplemented, ErrResponse(Status.NotGeneratedToken)
            )

            tokens.first.onSuccess { a ->
                tokens.second.onSuccess { r ->
                    mongo.updateRt(logIn, r).onFailure {
                        return@post call.respond(
                            HttpStatusCode.NotImplemented,
                            ErrResponse(status = Status.NotUpdatedRt, message = it.message)
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


