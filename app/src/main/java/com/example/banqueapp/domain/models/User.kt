package com.example.banqueapp.domain.models

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val pin: String? = null,
)
