package com.example.todokotlin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.todokotlin.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    lateinit var itemsAdapter: ArrayAdapter<String>
    var items = ArrayList<String>()
    private val databaseHelper: DatabaseHelper = DatabaseHelper()
    private var countCheckedItems: Int = 0
    private var checked = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.mainToolbar)
        binding?.mainToolbar?.subtitle = "List of tasks"

        itemsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, items)

        binding?.listView?.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        binding?.listView?.adapter = itemsAdapter


        databaseHelper.initializeAndLoadFromDatabase(baseContext, binding?.listView!!, itemsAdapter)
        databaseHelper.handleChecked(baseContext, binding?.listView!!)

        countCheckedItems = getCountCheckedItems(binding?.listView!!)
        Log.i("info", "countCheckedItems = $countCheckedItems")
        if ( countCheckedItems <= 0 )
            binding?.removeButton?.visibility = View.GONE

        binding?.addToDoButton?.setOnClickListener{ addItem() }
        setUpRemoveButtonViewListener()
        setUpListViewListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun getCountCheckedItems(listView: ListView): Int {
        var i = 0
        var count = 0
        while (i < listView.adapter.count) {
            if (listView.isItemChecked(i)){
                checked.add(i)
                count++
            }
            i++
        }
        return count
    }

    private fun setUpListViewListener() {
        binding?.listView?.setOnItemClickListener { adapterView, view, i, l ->
            if (binding?.listView!!.isItemChecked(i)) {
                countCheckedItems++
                if (countCheckedItems >= 0 && binding?.removeButton?.visibility == View.GONE)
                    binding?.removeButton?.visibility = View.VISIBLE
                databaseHelper.toggleCheck(baseContext, i, true)
            }
            else {
                countCheckedItems--
                if (countCheckedItems <= 0 && binding?.removeButton?.visibility == View.VISIBLE)
                    binding?.removeButton?.visibility = View.GONE
                databaseHelper.toggleCheck(baseContext, i, false)
            }
        }
        binding?.listView?.setOnItemLongClickListener { adapterView, view, i, l ->
//            databaseHelper.removeFromDatabase(baseContext, i+1)
//            items.removeAt(i)
//            itemsAdapter.notifyDataSetChanged()
//            ItemListDialogFragment.newInstance(1).show(supportFragmentManager, "dialog")
            val intent: Intent = Intent(this@MainActivity, DetailsActivity::class.java)
            val taskItem: TaskItem? = databaseHelper.getData(baseContext, i+1)

            intent.putExtra("ROWID", i)
            intent.putExtra("info", taskItem!!.info)
            intent.putExtra("isChecked", taskItem!!.isChecked)
            intent.putExtra("date", taskItem!!.date)
            startActivity(intent)
            return@setOnItemLongClickListener true
        }
    }

    private fun setUpRemoveButtonViewListener() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Need confirmation")
        builder.setMessage("This will delete all selected items")
        builder.setPositiveButton("Confirm", DialogInterface.OnClickListener { dialogInterface, i ->
            while (checked.isNotEmpty()) {
                databaseHelper.removeFromDatabase(baseContext, checked[0]+1)
                items.removeAt(checked[0])
                checked.removeAt(0)
                itemsAdapter.notifyDataSetChanged()
            }
            Toast.makeText(this, "Removed all checked items", Toast.LENGTH_LONG)
            binding?.removeButton?.visibility = View.GONE
            dialogInterface.cancel()
        });
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
        })
        binding?.removeButton?.setOnClickListener {
            builder.show()
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