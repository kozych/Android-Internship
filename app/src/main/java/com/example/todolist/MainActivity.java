package com.example.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addToDoButton;
    private ArrayList<String> items;
    private ArrayList<String> selected;
    private ArrayAdapter<String> itemsAdapter;
    private ListView listView;
    private final String databaseName = "sqlite-todolist.db";
    private int idMax = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        addToDoButton = findViewById(R.id.addToDoButton);
        addToDoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem(view);
            }
        });

        items = new ArrayList<>();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, items);
        initializeAndLoadFromDatabase(databaseName, MODE_PRIVATE);
        listView.setAdapter(itemsAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        setUpListViewListener();
    }

    private void setUpListViewListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Context context = getApplicationContext();
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view;
                if (listView.isItemChecked(i)) {
                    Toast.makeText(context, "i = "+Integer.toString(i)+" l = "+Long.toString(l), Toast.LENGTH_LONG).show();
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    textView.setPaintFlags(textView.getPaintFlags() &~ Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Context context = getApplicationContext();
                items.remove(i);
                itemsAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void addItem(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter activity name");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String itemText = input.getText().toString();
                itemsAdapter.add(itemText);
                saveToDatabase(databaseName, MODE_PRIVATE, itemText);
                input.setText("");
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void initializeAndLoadFromDatabase(String databaseName, int mode) {
        String sql = "CREATE TABLE IF NOT EXISTS \"tasks\" (\n" +
                "\t\"id\"\tINTEGER NOT NULL,\n" +
                "\t\"info\"\tTEXT,\n" +
                "\t\"isChecked\"\tINTEGER DEFAULT 0,\n" +
                "\tPRIMARY KEY(\"id\")\n" +
                ");";
        SQLiteDatabase sqLiteDatabase = getBaseContext().openOrCreateDatabase(databaseName, mode, null);
        sqLiteDatabase.execSQL(sql);
        Cursor query = sqLiteDatabase.rawQuery("SELECT * FROM tasks;", null);
        if (query.moveToFirst()) {
            do {
                String info = query.getString(1);
                Boolean checked = (query.getInt(2) == 0) ? false: true;
                listView.setItemChecked(idMax, checked);
                itemsAdapter.add(info);
                idMax++;
            } while (query.moveToNext());
        }
        query.close();
        sqLiteDatabase.close();
    }

    private void saveToDatabase(String databaseName, int mode, String info) {
        SQLiteDatabase sqLiteDatabase = getBaseContext().openOrCreateDatabase(databaseName, mode, null);
        String sql = "INSERT INTO tasks ('info', 'isChecked') VALUES('" + info + "', 0);";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
    }

    private void removeFromDatabase(String databaseName, int mode, int id) {
    }

    private void toggleCheck(String databaseName, int mode, int id) {
    }
}