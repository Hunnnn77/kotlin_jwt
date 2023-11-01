package com.example.config

data class Config(
    val uri: String,
    val db: String,
    val coll: String
)

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val domain: String,
)

