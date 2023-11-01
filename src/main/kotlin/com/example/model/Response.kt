package com.example.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class Status {
    Default,
    Registered,
    NotRegistered,
    Login,
    LogOut,
    NotGeneratedToken,
    NotUpdatedRt,
    NotFound,
    InvalidPayload,
    InvalidToken,
    Wrong,
    Failed
}

@Serializable
data class OkResponse<T>(
    val message: String?,
    val data: T? = null,
)

@Serializable
data class ErrResponse(
    val message: String? = null
)

@Serializable
data class AuthToken(
    val at: String,
    val rt: String,
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
