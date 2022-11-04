package com.example.todokotlin

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    lateinit var addToDoButton: FloatingActionButton
    lateinit var itemsAdapter: ArrayAdapter<String>
    lateinit var listView: ListView
    var items = ArrayList<String>()
    private val databaseHelper: DatabaseHelper = DatabaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        itemsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, items)
        addToDoButton = findViewById(R.id.addToDoButton)
        listView = findViewById(R.id.listView)

        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listView.adapter = itemsAdapter

        addToDoButton.setOnClickListener{ addItem() }

        databaseHelper.initializeAndLoadFromDatabase(baseContext, listView, itemsAdapter)
        databaseHelper.handleChecked(baseContext, listView)
        setUpListViewListener()
    }

    private fun setUpListViewListener() {
        listView.setOnItemClickListener { adapterView, view, i, l ->
            if (listView.isItemChecked(i)) {
                databaseHelper.toggleCheck(baseContext, i, true)
            }
            else {
                databaseHelper.toggleCheck(baseContext, i, false)
            }
        }
        listView.setOnItemLongClickListener { adapterView, view, i, l ->
            databaseHelper.removeFromDatabase(baseContext, i+1)
            items.removeAt(i)
            itemsAdapter.notifyDataSetChanged()

            return@setOnItemLongClickListener true
        }
    }

    private fun addItem() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Enter activity name")
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
            val itemText: String = input.text.toString()
            databaseHelper.saveToDatabase(baseContext, itemText)
            itemsAdapter.add(itemText)
            input.setText("")

            dialogInterface.cancel()
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
        })

        builder.show()
    }
}