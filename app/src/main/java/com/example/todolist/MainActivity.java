package com.example.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addToDoButton;
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView listView;
    private DatabaseHelper database = new DatabaseHelper();

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
        database.initializeAndLoadFromDatabase(getBaseContext(), listView, itemsAdapter);
        listView.setAdapter(itemsAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        database.handleChecked(getBaseContext(), listView);
        setUpListViewListener();
    }

    private void setUpListViewListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Context context = getApplicationContext();
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view;
                if (listView.isItemChecked(i)) {
                    database.toggleCheck(getBaseContext(), i, true);
                } else {
                    database.toggleCheck(getBaseContext(), i, false);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                items.remove(i);
                database.removeFromDatabase(getBaseContext(), i);
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
                database.saveToDatabase(getBaseContext(), itemText);
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
}