package com.example.util

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.time.Instant

class TimeHandler {
    private val tz = TimeZone.of("Asia/Seoul")
    private val now = Clock.System.now()

    //kotlin data obj -> java instant
    val issuedAt: Instant = now.toJavaInstant()
    val atTokenExpiration: Instant =
        now.plus(DateTimePeriod(minutes = 1), tz).toJavaInstant()
    val rtTokenExpiration: Instant =
        now.plus(DateTimePeriod(minutes = 2), tz).toJavaInstant()

    fun intoLocalDateTime(time: Long?): LocalDateTime {
        //from java obj -> kotlin instant
        return Instant.ofEpochMilli(time ?: 0).toKotlinInstant().toLocalDateTime(tz)
    }
}
