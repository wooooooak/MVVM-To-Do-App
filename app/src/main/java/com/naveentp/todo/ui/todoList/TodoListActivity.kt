package com.naveentp.todo.ui.todoList

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.naveentp.todo.R
import com.naveentp.todo.data.db.TodoRecord
import com.naveentp.todo.ui.createTodo.CreateTodoActivity
import com.naveentp.todo.utils.Constants
import kotlinx.android.synthetic.main.activity_todo_list.*
import kotlinx.android.synthetic.main.content_main.*

class TodoListActivity : AppCompatActivity(), TodoListAdapter.TodoEvents {

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var searchView: SearchView
    private lateinit var todoAdapter: TodoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)
        Log.d("tag1", "onCreate!!!!!!!!!!!!!!!")
        // Setting up RecyclerView
        // 설정하는 Manager에 따라 RecyclerView에 보여지는 아이템(row)의 배치 방법이 달라진다.
        // 대부분의 경우에 일렬로 나열하면 되므로 LinearLayoutManager를 사용하면 되지만
        // 다른 배열을 원한다면 문서를 참조하면 될 것 같다.
        rv_todo_list.layoutManager = LinearLayoutManager(this)

        // 사실 리사이클러뷰에서 핵심이 되는 부분은 어댑터를 정의하는 코드이다.
        // 생성자에서 todoEvent로 this를 넘겨주었다.
        todoAdapter = TodoListAdapter(this)
        rv_todo_list.adapter = todoAdapter

        // Setting up ViewModel and LiveData
        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)
        todoViewModel.getAllTodoList().observe(this, Observer {
            Log.d("tag1", "observe~!")
            todoAdapter.setAllTodoItems(it)
        })

        // FAB click listener
        fab_new_todo.setOnClickListener {
            resetSearchView()
            val intent = Intent(this@TodoListActivity, CreateTodoActivity::class.java)
            startActivityForResult(intent, Constants.INTENT_CREATE_TODO) // 두 번째 파라미터는 request코드임
        }
    }



    // RecyclerView Item callbacks
    // Callback when user clicks on Delete note
    // 아이템이 삭제될 때의 이벤트 리스너는 TodoListAdapter에 정의되어 있다.
    override fun onDeleteClicked(todoRecord: TodoRecord) {
        // onCreate에서 todoViewModel의 todoList를 구독했기 때문에 아래 코드처럼만 해도 noti된다.
        todoViewModel.deleteTodo(todoRecord)
    }

    //Callback when user clicks on view note
    override fun onViewClicked(todoRecord: TodoRecord) {
        resetSearchView()
        val intent = Intent(this@TodoListActivity, CreateTodoActivity::class.java)
        intent.putExtra(Constants.INTENT_OBJECT, todoRecord)
        startActivityForResult(intent, Constants.INTENT_UPDATE_TODO)
    }


    /**
     * Activity result callback
     * Triggers when Save button clicked from @CreateTodoActivity
     * 글쓰기 화면에서 글 저장을 누르면 응답이 오는데 그걸 처리하는 부분.
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val todoRecord = data?.getParcelableExtra<TodoRecord>(Constants.INTENT_OBJECT)!!
            when (requestCode) {
                Constants.INTENT_CREATE_TODO -> {
                    todoViewModel.saveTodo(todoRecord)
                }
                Constants.INTENT_UPDATE_TODO -> {
                    todoViewModel.updateTodo(todoRecord)
                }
            }
        }
    }


    // 화면 상단 search View 설정
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu) // 화면에 서치뷰가 나타남
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.search_todo)
                ?.actionView as SearchView
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(componentName))
        searchView.maxWidth = Integer.MAX_VALUE

        // 아래 코드에서 todoAdapter가 변경되는데, 이 todoAdapter는 rx_todo_list속에 있으므로 변경시 자동으로 옵저버들에게 noti되는 듯?
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                todoAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                todoAdapter.filter.filter(newText)
                return false
            }

        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.search_todo -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        resetSearchView()
        super.onBackPressed()
    }

    private fun resetSearchView() {
        if (!searchView.isIconified) {
            searchView.isIconified = true
            return
        }
    }
}
