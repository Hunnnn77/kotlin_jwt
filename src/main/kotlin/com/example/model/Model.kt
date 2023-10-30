package com.example.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class Status {
    Default, Registered, NotRegistered, Login, LogOut, Unauthorized, NotGeneratedToken, NotUpdatedRt
}

@Serializable
data class OkResponse<T>(
    val data: T? = null,
    val status: Status = Status.Default,
)

@Serializable
data class ErrResponse(
    val status: Status,
    val message: String? = null
)

@Serializable
data class AuthToken(
    val at: String,
    val rt: String,
)

@Serializable
data class AuthBody(
    val email: String,
    val password: String,
    val rt: String? = null,
)

@Serializable
data class Claim(
    @SerialName("user_name")
    val userName: String,
    @SerialName("issued_at")
    val issuedAt: LocalDateTime?,
    @SerialName("expired_at")
    val expiredAt: LocalDateTime?,
)
