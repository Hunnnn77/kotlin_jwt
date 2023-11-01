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
            val signIn = call.receive<Registration>()
            if (!signIn.isNotEmpty()) {
                return@post call.respond(
                    HttpStatusCode.NotImplemented, ErrResponse(statusOr = ResponseStatus.ValidationErr)
                )
            }

            mongo.insertOne(signIn).onSuccess {
                return@post call.respond(
                    HttpStatusCode.Created, OkResponse(statusOr = ResponseStatus.Registered, data = null)
                )
            }.onFailure {
                call.respond(
                    HttpStatusCode.NotImplemented, ErrResponse(statusOr = it.message)
                )
            }
        }

        post(Paths.LogIn.value) {
            val logIn = call.receive<Login>()
            if (!logIn.isNotEmpty()) return@post call.respond(
                HttpStatusCode.NotImplemented, ErrResponse(statusOr = ResponseStatus.ValidationErr)
            )

            if (mongo.findUser(logIn.email) == null) return@post call.respond(
                HttpStatusCode.NotFound, ErrResponse(statusOr = ResponseStatus.NotFound)
            )

            if (!logIn.validPassword()) return@post call.respond(
                HttpStatusCode.NotFound, ErrResponse(statusOr = ResponseStatus.ValidationErr)
            )

            val (at, rt) = handler.genToken(logIn, TokenKind.At) to handler.genToken(logIn, TokenKind.Rt)
            if (at.isFailure || rt.isFailure) return@post call.respond(
                HttpStatusCode.NotImplemented, ErrResponse(statusOr = ResponseStatus.NotGeneratedToken)
            )

            at.onSuccess { a ->
                rt.onSuccess { r ->
                    mongo.updateRt(logIn.email, r).onFailure {
                        return@post call.respond(
                            HttpStatusCode.NotImplemented,
                            ErrResponse(statusOr = it.message)
                        )
                    }.onSuccess {
                        call.handleCookie(a)
                        call.respond(
                            HttpStatusCode.OK,
                            OkResponse(statusOr = ResponseStatus.Login, data = AuthToken(at = a, rt = r))
                        )
                    }
                }
            }
        }
    }
}