package com.naveentp.todo.ui.todoList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.naveentp.todo.R
import com.naveentp.todo.data.db.TodoRecord
import kotlinx.android.synthetic.main.todo_item.view.*

/**
 * @author Naveen T P
 * @since 08/11/18
 */

// RecyclerView.Adapter<RecyclerView.ViewHolder> 를 상속받아서 정의하면 된다
// 생성자로 TodoEvent를 받는데, 액티비티에서 TodoListAdapter(this)와 같이 생성한다.
// TodoEvent는 맨 아래에 정의했다.
class TodoListAdapter(todoEvents: TodoEvents) : RecyclerView.Adapter<TodoListAdapter.ViewHolder>(), Filterable {

    private var todoList: List<TodoRecord> = arrayListOf()
    private var filteredTodoList: List<TodoRecord> = arrayListOf()
    private val listener: TodoEvents = todoEvents

    // 이 함수는 RecyclerView의 행을 표시하는데 사용되는 레이아웃 xml을 가져오는 역할을 한다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return ViewHolder(view)
    }

    // 이 함수에서 마침내 RecyclerView의 행에 보여질 데이터를 설정한다.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredTodoList[position], listener)
    }

    override fun getItemCount(): Int = filteredTodoList.size


    // 어댑터 내부에 RecyclerView.ViewHolder를 상속받는 클래스를 정의해야 한다.
    // 이 클래스가 RecyclerView의 행(row)를 표시하는 클래스이다.
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(todo: TodoRecord, listener: TodoEvents) {
            itemView.tv_item_title.text = todo.title
            itemView.tv_item_content.text = todo.content

            itemView.iv_item_delete.setOnClickListener {
                listener.onDeleteClicked(todo)
            }

            itemView.setOnClickListener {
                listener.onViewClicked(todo)
            }
        }
    }


    /**
     * Search Filter implementation
     * Filterable 인터페이스의 메서드
     * */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charString = p0.toString()
                filteredTodoList = if (charString.isEmpty()) {
                    todoList
                } else {
                    val filteredList = arrayListOf<TodoRecord>()
                    for (row in todoList) {
                        if (row.title.toLowerCase().contains(charString.toLowerCase())
                                || row.content.contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = filteredTodoList
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                filteredTodoList = p1?.values as List<TodoRecord>
                notifyDataSetChanged()
            }

        }
    }

    /**
     * Activity uses this method to update todoList with the help of LiveData    * */
    fun setAllTodoItems(todoItems: List<TodoRecord>) {
        this.todoList = todoItems
        this.filteredTodoList = todoItems
        notifyDataSetChanged()
    }

    /**
     * RecycleView touch event callbacks
     * */
    interface TodoEvents {
        fun onDeleteClicked(todoRecord: TodoRecord)
        fun onViewClicked(todoRecord: TodoRecord)
    }
}
