package com.example.banqueapp.data.mapper

import com.example.banqueapp.data.db.entities.UserEntity
import com.example.banqueapp.domain.models.User

object UserMapper {
    fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            password = entity.password,
            pin = entity.pin
        )
    }

    fun toEntity(user: User): UserEntity {
        return UserEntity(
            id = user.id,
            name = user.name,
            email = user.email,
            password = user.password,
            pin = user.pin
        )
    }
}

