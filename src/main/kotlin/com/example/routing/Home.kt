package com.example.routing

import com.example.config.Paths
import com.example.db.Mongo
import com.example.model.*
import com.example.util.*
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
                    HttpStatusCode.NotImplemented, ErrResponse(message = Status.NotRegistered.name)
                )
            }

            mongo.insertOne(signIn).onSuccess {
                return@post call.respond(
                    HttpStatusCode.Created, OkResponse(message = Status.Registered.name, data = null)
                )
            }.onFailure {
                call.respond(
                    HttpStatusCode.NotImplemented, ErrResponse(message = it.message)
                )
            }
        }

        post(Paths.LogIn.value) {
            val logIn = call.receive<AuthBody>()
            if (mongo.findUser(logIn.email) == null) return@post call.respond(
                HttpStatusCode.NotFound, ErrResponse(message = Status.NotFound.name)
            )

            val (at, rt) = handler.genToken(logIn, TokenKind.At) to handler.genToken(logIn, TokenKind.Rt)
            if (at.isFailure || rt.isFailure) return@post call.respond(
                HttpStatusCode.NotImplemented, ErrResponse(message = Status.NotGeneratedToken.name)
            )

            at.onSuccess { a ->
                rt.onSuccess { r ->
                    mongo.updateRt(logIn, r).onFailure {
                        return@post call.respond(
                            HttpStatusCode.NotImplemented,
                            ErrResponse(message = it.message)
                        )
                    }.onSuccess {
                        call.handleCookie(a)
                        call.respond(
                            HttpStatusCode.OK, OkResponse(message = Status.Login.name, data = AuthToken(at = a, rt = r))
                        )
                    }
                }
            }
        }
    }
}