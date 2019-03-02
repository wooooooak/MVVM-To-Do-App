package com.naveentp.todo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


// Entity 모델을 기반으로 하고, DAO의 메소드를 가지고 있는 데이터베이스
// 메인 액티비티에서 호출하여 database객체를 반환하거나 삭제할 수 있도록
// 싱글톤으로 getInstance()와 destroyInstance() 메소드를 생성해준다.
@Database(entities = [TodoRecord::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    companion object {
        private var INSTANCE: TodoDatabase? = null

        fun getInstance(context: Context): TodoDatabase? {
            if (INSTANCE == null) {
                synchronized(TodoDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context,
                            TodoDatabase::class.java,
                            "todo_db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyIstance() {
            INSTANCE = null
        }
    }
}