package com.example.util

import com.example.model.BodyModel

fun BodyModel.isNotEmpty() = email.isNotEmpty() && password.isNotEmpty()
fun BodyModel.validPassword(): Boolean {
    if (password.length <= 3) return false
    return true
}