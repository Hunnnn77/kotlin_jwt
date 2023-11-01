package com.example.plugins

import com.example.model.*
import com.example.util.toUpperFirst
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*


fun Application.configurePages() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(HttpStatusCode.NotFound, ErrResponse(ResponseStatus.NotFound))
        }

        exception<Throwable> { call, cause ->
            when (cause) {
                is InvalidTokenException -> call.respond(
                    HttpStatusCode.Unauthorized, ErrResponse(statusOr = cause.message)
                )

                is InvalidPayloadException -> call.respond(
                    HttpStatusCode.Unauthorized, ErrResponse(statusOr = cause.message)
                )

                is MongoImplException -> call.respond(
                    HttpStatusCode.BadRequest, ErrResponse(statusOr = cause.message)
                )

                else -> call.respond(
                    HttpStatusCode.NotImplemented,
                    ErrResponse(statusOr = "something went wrong".toUpperFirst())
                )
            }
        }
    }
}