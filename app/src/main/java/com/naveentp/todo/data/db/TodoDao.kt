package com.naveentp.todo.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TodoDao {

    // 어노테이션으로 이렇게 표현을 하면,
    // Room.databasBuilder가 알아서 saveToDo에 맞는
    // 메서드를 만들어 준다.
    @Insert
    suspend fun saveTodo(todoRecord: TodoRecord)

    @Delete
    suspend fun deleteTodo(todoRecord: TodoRecord)

    @Update
    suspend fun updateTodo(todoRecord: TodoRecord)

    @Query("SELECT * FROM todo ORDER BY id DESC")
    fun getAllTodoList(): LiveData<List<TodoRecord>>
}