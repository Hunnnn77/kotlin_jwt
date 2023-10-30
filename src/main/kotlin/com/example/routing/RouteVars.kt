package com.example.routing

sealed class Paths(val value: String) {
    data object Home : Paths("/")
    data object Auth : Paths("/auth")
    data object SignUp : Paths("/signup")
    data object LogIn : Paths("/login")
    data object LogOut : Paths("/logout")
}

enum class Fields(val value: String) {
    Email(value = "email")
}