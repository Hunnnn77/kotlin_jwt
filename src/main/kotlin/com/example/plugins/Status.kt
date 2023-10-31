package com.example.plugins

import com.example.model.ErrResponse
import com.example.model.Status
import com.example.routing.AuthorizationException
import com.example.routing.InvalidPayloadException
import com.example.routing.InvalidTokenException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*


fun Application.configurePages() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(HttpStatusCode.NotFound, ErrResponse(status = Status.NotFound, message = status.description))
        }

        exception<Throwable> { call, cause ->
            when (cause) {
                is AuthorizationException -> call.respond(
                    HttpStatusCode.Unauthorized, ErrResponse(Status.Unauthorized, message = cause.message)
                )

                is InvalidTokenException -> call.respond(
                    HttpStatusCode.Unauthorized, ErrResponse(Status.InvalidToken, message = cause.message)
                )

                is InvalidPayloadException -> call.respond(
                    HttpStatusCode.Unauthorized, ErrResponse(Status.InvalidPayload, message = cause.message)
                )
            }
        }
    }
}