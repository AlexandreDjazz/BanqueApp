package com.example.banqueapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.banqueapp.data.db.entities.TransactionEntity
import com.example.banqueapp.data.db.dao.TransactionDao
import com.example.banqueapp.data.db.dao.UserDao
import com.example.banqueapp.data.db.entities.UserEntity

@Database(
    entities = [UserEntity::class, TransactionEntity::class],
    version = 7
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
}
