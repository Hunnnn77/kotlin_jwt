package com.example

import com.example.db.Mongo
import com.example.plugins.*
import com.example.config.Config
import com.example.config.JwtConfig
import com.example.util.JwtHandler
import com.example.util.TimeHandler
import configureHeader
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
    val mongo = Mongo.getInstance(c)
    val timeHandler = TimeHandler()
    val handler = JwtHandler.getInstance(jwtC)

    configureSecurity(mongo = mongo, jwtConfig = jwtC, handler = handler)
    configureHeader()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configurePages()
    configureRouting(
        mongo = mongo,
        handler = handler,
        toLocalDateTime = { timeHandler.intoLocalDateTime(it) }
    )
}
