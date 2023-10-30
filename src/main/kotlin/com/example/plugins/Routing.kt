package com.example.plugins

import com.example.db.Mongo
import com.example.routing.JwtConfig
import com.example.routing.auth
import com.example.routing.home
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

fun Application.configureRouting(mongo: Mongo, jwtConfig: JwtConfig) {
    install(Resources)
    routing {
        home(mongo, jwtConfig)
        auth(mongo)
    }
}

