package com.example

import com.example.db.Mongo
import com.example.plugins.*
import com.example.routing.Config
import com.example.routing.JwtConfig
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun getProp(name: String) = Exception("Not Found: $name")

fun Application.module() {
    val c = Config(
        uri = environment.config.propertyOrNull("db.uri")?.getString() ?: throw getProp("uri"),
        db = environment.config.propertyOrNull("db.db")?.getString() ?: throw getProp("db"),
        coll = environment.config.propertyOrNull("db.coll")?.getString() ?: throw getProp("coll")
    )
    val jwtC = JwtConfig(
        secret = environment.config.propertyOrNull("jwt.secret")?.getString() ?: throw getProp("secret"),
        issuer = environment.config.propertyOrNull("jwt.issuer")?.getString() ?: throw getProp("issuer"),
        audience = environment.config.propertyOrNull("jwt.audience")?.getString() ?: throw getProp("audience"),
        realm = environment.config.propertyOrNull("jwt.realm")?.getString() ?: throw getProp("realm"),
        domain = environment.config.propertyOrNull("jwt.domain")?.getString() ?: throw getProp("realm")
    )

    configureSecurity(jwtC)
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureRouting(Mongo.getInstance(c), jwtC)
}
