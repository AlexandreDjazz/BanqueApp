package com.example.banqueapp.domain.models

data class User(
    val id: Int,
    val balance: Double = 0.0,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val pin: String? = null,
)
