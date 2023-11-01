package com.example.model

import kotlinx.serialization.Serializable

interface BodyModel {
    val email: String
    val password: String
}

@Serializable
data class Registration(
    override val email: String,
    override val password: String,
    val rt: String? = null,
) : BodyModel

@Serializable
data class Login(
    override val email: String,
    override val password: String,
) : BodyModel
