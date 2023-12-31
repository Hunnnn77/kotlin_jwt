package com.example.util

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import com.example.config.PayloadFields
import com.example.config.JwtConfig
import com.example.model.BodyModel
import com.example.model.InvalidTokenException

enum class TokenKind {
    At, Rt
}

class JwtHandler private constructor(private val jwtConfig: JwtConfig) {
    companion object {
        fun getInstance(jwtConfig: JwtConfig): JwtHandler {
            return JwtHandler(jwtConfig)
        }
    }

    val builder: JWTVerifier =
        JWT.require(Algorithm.HMAC256(jwtConfig.secret)).withAudience(jwtConfig.audience).withIssuer(jwtConfig.issuer)
            .build()

    fun genToken(
        body: BodyModel, tokenKind: TokenKind
    ): Result<String> {
        val timeHandler = TimeHandler()
        return try {
            val token =
                JWT.create().withAudience(jwtConfig.audience).withIssuer(jwtConfig.issuer)
                    .withClaim(PayloadFields.Email.value, body.email).withIssuedAt(timeHandler.issuedAt)
                    .withExpiresAt(if (tokenKind == TokenKind.At) timeHandler.atTokenExpiration else timeHandler.rtTokenExpiration)
                    .sign(Algorithm.HMAC256(jwtConfig.secret))
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(InvalidTokenException(message = e.message))
        }
    }

    fun verify(token: String): Result<Unit> {
        return try {
            if (token.contains("Bearer")) {
                builder.verify(token.split(" ").last())
                Result.success(Unit)
            } else {
                builder.verify(token)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            when (e) {
                is TokenExpiredException -> Result.failure(e)
                else -> Result.failure(InvalidTokenException(message = "failed to verify token".toUpperFirst()))
            }
        }
    }
}
