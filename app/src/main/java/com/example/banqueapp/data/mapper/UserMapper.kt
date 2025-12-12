package com.example.banqueapp.data.mapper

import com.example.banqueapp.data.db.entities.UserEntity
import com.example.banqueapp.domain.models.User
import kotlin.Int

object UserMapper {
    fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            balance = entity.balance,
            name = entity.name,
            email = entity.email,
            phone = entity.phone,
            password = entity.password,
            pin = entity.pin,
        )
    }

    fun toEntity(user: User): UserEntity {
        return UserEntity(
            id = user.id,
            balance = user.balance,
            name = user.name,
            email = user.email,
            phone = user.phone,
            password = user.password,
            pin = user.pin
        )
    }
}

