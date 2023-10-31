package com.example.util

import com.example.model.AuthBody

fun AuthBody.checkEmpty() = email.isNotEmpty() && password.isNotEmpty()
fun String.toUpperFirst() = split(" ").joinToString(" ") { it[0].uppercase() + it.substring(1) }