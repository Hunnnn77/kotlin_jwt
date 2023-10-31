package com.example.plugins

import com.example.db.Mongo
import com.example.routing.auth
import com.example.routing.home
import com.example.util.JwtHandler
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDateTime

fun Application.configureRouting(mongo: Mongo, handler: JwtHandler, toLocalDateTime: (Long?) -> LocalDateTime) {
    install(Resources)
    routing {
        home(mongo = mongo, handler = handler)
        auth(mongo = mongo, handler = handler, toLocalDateTime = toLocalDateTime)
    }
}

