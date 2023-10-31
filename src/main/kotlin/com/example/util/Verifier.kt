package com.example.util

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import com.example.model.AuthBody
import com.example.routing.Fields
import com.example.routing.JwtConfig

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
        signIn: AuthBody, tokenKind: TokenKind
    ): Result<String> {
        val timeHandler = TimeHandler()
        return try {
            val token =
                JWT.create().withAudience(jwtConfig.audience).withIssuer(jwtConfig.issuer)
                    .withClaim(Fields.Email.value, signIn.email).withIssuedAt(timeHandler.issuedAt)
                    .withExpiresAt(if (tokenKind == TokenKind.At) timeHandler.atTokenExpiration else timeHandler.rtTokenExpiration)
                    .sign(Algorithm.HMAC256(jwtConfig.secret))
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(Throwable(e.message))
        }
    }

    fun verify(token: String): Result<Unit> {
        return try {
            builder.verify(token.split(" ").last())
            Result.success(Unit)
        } catch (e: Exception) {
            when (e) {
                is TokenExpiredException -> Result.failure(Throwable("expired token".toUpperFirst()))
                else -> Result.failure(Throwable("failed to verify token".toUpperFirst()))
            }
        }
    }
}
