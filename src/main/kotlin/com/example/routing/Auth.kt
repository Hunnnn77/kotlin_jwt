package com.example.routing

import com.example.db.Mongo
import com.example.model.Claim
import com.example.model.ErrResponse
import com.example.model.OkResponse
import com.example.model.Status
import com.example.util.intoLocalDateTime
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Routing.auth(mongo: Mongo) {
    route(Paths.Auth.value) {
        authenticate("jwt") {
            get {
                call.principal<JWTPrincipal>()?.let {
                    call.respond(
                        HttpStatusCode.OK, OkResponse(
                            Claim(
                                userName = it.payload.getClaim(Fields.Email.value).asString() ?: "anonymous",
                                issuedAt = it.issuedAt?.time?.intoLocalDateTime(),
                                expiredAt = it.expiresAt?.time?.intoLocalDateTime(),
                            )
                        )
                    )
                }
            }

            get(Paths.LogOut.value) {
                call.principal<JWTPrincipal>()?.let {
                    val email =
                        it.payload.getClaim(Fields.Email.value).asString() ?: "anonymous"
                    mongo.removeRt(email).onFailure {
                        return@get call.respond(
                            HttpStatusCode.NotImplemented,
                            ErrResponse(status = Status.NotUpdatedRt)
                        )
                    }.onSuccess {
                        call.respond(HttpStatusCode.OK, OkResponse(status = Status.LogOut, data = null))
                    }
                }
            }
        }
    }
}