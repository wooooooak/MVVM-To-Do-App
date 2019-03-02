package com.naveentp.todo.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.naveentp.todo.data.db.TodoDao
import com.naveentp.todo.data.db.TodoDatabase
import com.naveentp.todo.data.db.TodoRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TodoRepository(application: Application) {

    private val todoDao: TodoDao
    private val allTodos: LiveData<List<TodoRecord>>

    init {
        val todoDb = TodoDatabase.getInstance(application.applicationContext)
        // todoDao타입의 실제 객체가 반환되는 것 같다. Room.databaseBuilder가 알아서 해준 듯?
        todoDao = todoDb!!.todoDao()
        allTodos = todoDao.getAllTodoList()
    }

    // launch나 async같은 coroutine builder는 CoroutineContext값을
    // optional로 지정할 수 있고 이를 통해 dispatcher를 지정할 수 있다.
    fun saveTodo(todo: TodoRecord) = runBlocking {
        this.launch(Dispatchers.IO) {
            // launch는 coroutine의 빌더이며
            // 이를 이용해서 코드를 CoroutineScope안에서 실행시
            // launch{..} 와 thread{..}는 같은 역할이라고 이해
            todoDao.saveTodo(todo)
       }
    }

    fun updateTodo(todo: TodoRecord) = runBlocking {
        this.launch(Dispatchers.IO) {
            todoDao.updateTodo(todo)
        }
    }

    fun deleteTodo(todo: TodoRecord) {
        runBlocking {
            this.launch(Dispatchers.IO) {
                todoDao.deleteTodo(todo)
            }
        }
    }

    fun getAllTodoList(): LiveData<List<TodoRecord>> {
        Log.d("tag1", "getAllTodoList in repository")
        return allTodos
    }
}