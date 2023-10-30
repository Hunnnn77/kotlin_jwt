package com.example.util

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.time.Instant

val now = Clock.System.now()
val tz = TimeZone.of("Asia/Seoul")

//kotlin data obj -> java instant
val issuedAt: Instant = now.toJavaInstant()
val atTokenExpiration: Instant =
    now.plus(DateTimePeriod(seconds = 30), tz).toJavaInstant()
val rtTokenExpiration: Instant =
    now.plus(DateTimePeriod(minutes = 1), tz).toJavaInstant()

fun Long.intoLocalDateTime(): LocalDateTime {
    //from java obj -> kotlin instant
    return Instant.ofEpochMilli(this).toKotlinInstant().toLocalDateTime(tz)
}