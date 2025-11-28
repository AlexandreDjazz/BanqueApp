package com.example.banqueapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.banqueapp.data.db.dao.UserDao
import com.example.banqueapp.data.db.entities.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
