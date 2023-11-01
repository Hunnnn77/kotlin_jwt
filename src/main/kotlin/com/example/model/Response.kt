package com.example.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ResponseStatus {
    Ok,
    NotFound,
    Registered,
    Login,
    LogOut,
    ValidationErr,
    NotGeneratedToken,
    NotUpdatedRt,
}

interface ResponseModel<T> {
    val statusOr: T?
}

@Serializable
data class OkResponse<T, U>(
    override val statusOr: T?,
    val data: U? = null,
) : ResponseModel<T>

@Serializable
data class ErrResponse<T>(
    override val statusOr: T? = null
) : ResponseModel<T>

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
