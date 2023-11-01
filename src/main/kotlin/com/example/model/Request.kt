package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthBody(
    val email: String,
    val password: String,
    val rt: String? = null,
)
