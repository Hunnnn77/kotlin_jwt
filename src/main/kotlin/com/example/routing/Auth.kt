package com.example.routing

import com.example.config.Paths
import com.example.db.Mongo
import com.example.model.Claim
import com.example.model.ErrResponse
import com.example.model.OkResponse
import com.example.model.ResponseStatus
import com.example.util.getEatFromDecoded
import com.example.util.getEmailFromDecoded
import com.example.util.getIatFromDecoded
import com.example.util.parseCookie
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDateTime


fun Routing.auth(mongo: Mongo, toLocalDateTime: (Long?) -> LocalDateTime) {
    route(Paths.Auth.value) {
        authenticate("jwt") {
            get {
                call.parseCookie()?.let { at ->
                    call.respond(
                        HttpStatusCode.OK, OkResponse(
                            statusOr = ResponseStatus.Ok,
                            Claim(
                                userName = getEmailFromDecoded(at),
                                issuedAt = toLocalDateTime(getIatFromDecoded(at)),
                                expiredAt = toLocalDateTime(getEatFromDecoded(at)),
                            )
                        )
                    )
                }
            }

            get(Paths.LogOut.value) {
                // from Bearer header
//                call.principal<JWTPrincipal>()?.let {
//                }

                //[middleware - challenge]
                call.parseCookie()?.let { at ->
                    val email = getEmailFromDecoded(at)
                    mongo.removeRt(email).onFailure {
                        return@get call.respond(
                            HttpStatusCode.NotImplemented, ErrResponse(statusOr = ResponseStatus.NotUpdatedRt)
                        )
                    }.onSuccess {
                        call.handleCookie("")
                        call.respond(HttpStatusCode.OK, OkResponse(statusOr = ResponseStatus.LogOut, data = null))
                    }
                }
            }
        }
    }
}
