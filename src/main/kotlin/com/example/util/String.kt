package com.example.util

fun String.toUpperFirst() = split(" ").joinToString(" ") { it[0].uppercase() + it.substring(1) }