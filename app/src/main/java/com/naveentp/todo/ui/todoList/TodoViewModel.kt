package com.naveentp.todo.ui.todoList

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.naveentp.todo.data.TodoRepository
import com.naveentp.todo.data.db.TodoRecord

// AndroidViewModel 과 ViewModel은 같다. 단 한 가지 차이점은 AndroidViewModel은 application context를 포함하고 있다는 것이다.
// You can use use ViewModel and to pass context to ViewModel to function that loading data from MediaStore,
//  or use AndroidViewModel with application context

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    // 정확하게는 모르겠지만 아마 맞는 것 같은데...
    // allTodoList는 repository의 allTodos이다. 즉 Acitivity가 구독한 것은
    // 본질적으로 repository의 allTodos인 것!!
    // 따라서 repository의 allTodos가 바뀌면 viewModel의 allTodoList가 바뀌는 것이니까
    // 변경을 감지한다! 게다가 LiveData는 전역스럽게? 주소값처럼? 계속 사용가능하다고
    // 어디선가 본것 같은데 결국 값만 복사해서 넘기는 게 아니기 때문에
    // repository가 바뀐것을 viewModel이 알 수 있는 것은 viewModel이 그것을 참조하고
    // 있기 때문인 것 같다!(?)
    private val repository: TodoRepository = TodoRepository(application)
    private val allTodoList: LiveData<List<TodoRecord>> = repository.getAllTodoList()

    fun saveTodo(todo: TodoRecord) {
        repository.saveTodo(todo)
    }

    fun updateTodo(todo: TodoRecord) {
        repository.updateTodo(todo)
    }

    fun deleteTodo(todo: TodoRecord) {
        repository.deleteTodo(todo)
    }

    fun getAllTodoList(): LiveData<List<TodoRecord>> {
        Log.d("tag1", "getAllTodoList in viewModel")
        return allTodoList
    }

}
