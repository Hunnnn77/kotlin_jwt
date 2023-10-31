package com.example.routing

data class AuthorizationException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Throwable(message, cause)

data class InvalidTokenException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Throwable(message, cause)

data class InvalidPayloadException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Throwable(message, cause)
