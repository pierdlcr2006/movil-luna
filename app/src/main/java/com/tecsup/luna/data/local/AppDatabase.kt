package com.tecsup.luna.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tecsup.luna.data.local.Curso
import com.tecsup.luna.data.local.CursoDao

@Database(entities = [Curso::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cursoDao(): CursoDao
}
