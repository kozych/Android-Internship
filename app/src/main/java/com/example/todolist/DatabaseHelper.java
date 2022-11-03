package com.example.todolist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DatabaseHelper {
    private final String databaseName = "sqlite-todolist.db";
    private final int infoPosition = 0;
    private final int isCheckedPosition = 1;

    public void initializeAndLoadFromDatabase(Context baseContext, ListView listView, ArrayAdapter<String> itemsAdapter) {
        String sql = "CREATE TABLE IF NOT EXISTS \"tasks\" (\n" +
                "\t\"info\"\tTEXT,\n" +
                "\t\"isChecked\"\tINTEGER DEFAULT 0\n" +
                ");";

        SQLiteDatabase sqLiteDatabase = baseContext.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);
        sqLiteDatabase.execSQL(sql);
        Cursor query = sqLiteDatabase.rawQuery("SELECT * FROM tasks;", null);
        if (query.moveToFirst()) {
            do {
                String info = query.getString(infoPosition);
                Log.i("query.getPosition(): ", String.valueOf(query.getPosition()));
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Position: ").append(query.getPosition())
                                .append(" info: ").append(query.getString(0))
                                .append(" isChecked: ").append(query.getInt(1));
                Log.i("data", stringBuilder.toString());
                itemsAdapter.add(info);
                if (query.getInt(isCheckedPosition) == 1) {
                    listView.setItemChecked(query.getPosition(), true);
                }
                itemsAdapter.notifyDataSetChanged();
            } while (query.moveToNext());
        }
        query.close();
        sqLiteDatabase.close();
    }

    public void handleChecked(Context context, ListView listView) {
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);
        String sql = "SELECT * FROM tasks";
        Cursor query = sqLiteDatabase.rawQuery(sql, null);
        if (query.moveToFirst()) {
            do {
                if (query.getInt(isCheckedPosition) == 1) {
                    listView.setItemChecked(query.getPosition(), true);
                }
            } while (query.moveToNext());
        }
    }

    public void saveToDatabase(Context context, String info) {
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);
        String sql = "INSERT INTO tasks ('info', 'isChecked') VALUES('" + info + "', 0);";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
    }

    public void removeFromDatabase(Context context, int id) {
        id++;
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);
        String sql = "DELETE FROM tasks WHERE ROWID = '" + id +"';";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.execSQL("VACUUM;");
        sqLiteDatabase.close();
    }

    public void toggleCheck(Context context, int id, boolean check) {
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE tasks SET isChecked = ");
        if (check) sql.append("1");
        else sql.append("0")
                .append(" WHERE ROWID = ")
                .append(id+1);
        sql.append(";");
        sqLiteDatabase.execSQL(sql.toString());
        sqLiteDatabase.close();
    }
}
