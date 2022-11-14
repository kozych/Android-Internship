package com.example.todokotlin

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView

class DatabaseHelper {
    private val databaseName = "todolist.db"
    private val infoPosition = 0
    private val isCheckedPosition = 1
    private val datePosition = 2
    private val isAlarmOnPosition = 3

    fun initializeAndLoadFromDatabase(baseContext: Context, listView: ListView, itemsAdapter: ArrayAdapter<String>) {
        val sqLiteDatabase: SQLiteDatabase = baseContext.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null)
        val sql = """CREATE TABLE IF NOT EXISTS "tasks" (
	                "info"	TEXT,
	                "isChecked"	INTEGER DEFAULT 0,
                    "date"	TEXT,
                    "isAlarmOn"	INTEGER DEFAULT 0);"""

        sqLiteDatabase.execSQL(sql)
        val query: Cursor = sqLiteDatabase.rawQuery("SELECT * FROM tasks;", null)

        if (query.moveToFirst()) {
            do {
                var info = query.getString(infoPosition)
                itemsAdapter.add(info)
                if (query.getInt(isCheckedPosition) == 1) {
                    listView.setItemChecked(query.position, true)
                }
                itemsAdapter.notifyDataSetChanged();
            } while (query.moveToNext())
        }

        query.close()
        sqLiteDatabase.close()
    }

    fun handleChecked(context: Context, listView: ListView) {
        val sqLiteDatabase: SQLiteDatabase = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null)
        val sql = "SELECT * FROM tasks"
        val query: Cursor = sqLiteDatabase.rawQuery(sql, null)

        if (query.moveToFirst()) {
            do {
                if (query.getInt(isCheckedPosition) == 1)
                    listView.setItemChecked(query.position, true)
            } while (query.moveToNext())
        }
    }

    fun saveToDatabase(context: Context, info: String) {
        val sqLiteDatabase: SQLiteDatabase = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null)
        val sql = "INSERT INTO tasks ('info', 'isChecked') VALUES('$info', 0);"

        sqLiteDatabase.execSQL(sql)
        sqLiteDatabase.close()
    }

    fun removeFromDatabase(context: Context, id: Int) {
        val sqLiteDatabase: SQLiteDatabase = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null)
        val sql = "DELETE FROM tasks WHERE ROWID = '$id';"

        sqLiteDatabase.execSQL(sql)
        sqLiteDatabase.execSQL("VACUUM")
        sqLiteDatabase.close()
    }

    fun toggleCheck(context: Context, id: Int, check: Boolean) {
        val sqLiteDatabase: SQLiteDatabase = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null)

        val sql = buildString {
            append("UPDATE tasks SET isChecked = ")
            if (check)
                append("1 ")
            else {
                append("0 ")
            }
            append("WHERE ROWID = ")
            append(id+1)
            append(";")
        }

        sqLiteDatabase.execSQL(sql)
        sqLiteDatabase.close()
    }

    fun setDate(context: Context, id: Int, date: String) {
        val sqLiteDatabase: SQLiteDatabase = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null)
        val sql = buildString {
            append("UPDATE tasks SET date = '$date'")
            append("WHERE ROWID = ")
            append(id+1)
            append(";")
        }

        Log.i("database TAG", sql)

        sqLiteDatabase.execSQL(sql)
        sqLiteDatabase.close()
    }

    fun toggleAlarm(context: Context, id: Int, alarm: Boolean) {
        val sqLiteDatabase: SQLiteDatabase = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null)
        val sql = buildString {
            append("UPDATE tasks SET isAlarmOn = ")
            if (alarm)
                append("1 ")
            else {
                append("0 ")
            }
            append("WHERE ROWID = ")
            append(id+1)
            append(";")
        }

        sqLiteDatabase.execSQL(sql)
        sqLiteDatabase.close()
    }

    fun getData(context: Context, id: Int): TaskItem? {
        val sqLiteDatabase: SQLiteDatabase = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null)

        val sql = "SELECT * FROM tasks WHERE ROWID = '$id';"
        val query: Cursor = sqLiteDatabase.rawQuery(sql, null)
        val retItem: TaskItem = TaskItem()
        if (query.moveToFirst()){
            retItem.ROWID = id
            retItem.info = query.getString(infoPosition)
            retItem.isChecked = query.getInt(isCheckedPosition) == 1
            retItem.date = query.getString(datePosition)
            retItem.isAlarmOn = query.getInt(isAlarmOnPosition) == 1
            return retItem
        }
        return null
    }
}