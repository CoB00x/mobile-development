package com.example.pr10_zubarev;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        EditText nameInput = findViewById(R.id.name_input);
        EditText phoneInput = findViewById(R.id.phone_input);
        Button saveButton = findViewById(R.id.save_button);
        Button deleteButton = findViewById(R.id.delete_button);
        Button findButton = findViewById(R.id.find_button);
        RecyclerView contactsList = findViewById(R.id.contacts_list);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<Contact> contacts = dbHelper.getAllContacts();
        ContactAdapter adapter = new ContactAdapter((ArrayList<Contact>) contacts);
        contactsList.setLayoutManager(new LinearLayoutManager(this));
        contactsList.setAdapter(adapter);

        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String phone = phoneInput.getText().toString();
            if (dbHelper.addContact(new Contact(0, name, phone)))
            {
                contacts.add(new Contact(0, name, phone));
                adapter.notifyItemInserted(contacts.size() - 1);
                Toast.makeText(this, "Contact saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save contact",
                        Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v -> {
            String phone = phoneInput.getText().toString();
            if (dbHelper.deleteContact(phone)) {
                int position = -1;
                for (int i = 0; i < contacts.size(); i++) {
                    if (contacts.get(i).getPhone().equals(phone))
                    {
                        position = i;
                        contacts.remove(i);
                        break;
                    }
                }
                if (position != -1) {
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(this, "Contact deleted successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Contact not found",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to delete contact",
                        Toast.LENGTH_SHORT).show();
            }
        });

        findButton.setOnClickListener(v -> {
            if (!phoneInput.getText().toString().isEmpty()) {
                String phone = phoneInput.getText().toString();
                Contact foundContact = dbHelper.findByPhone(phone);
                if (foundContact != null) {
                    nameInput.setText(foundContact.getName());
                    phoneInput.setText(foundContact.getPhone());
                    Toast.makeText(this, "Contact found: " +
                            foundContact.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Contact not found",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                String name = nameInput.getText().toString();
                Contact foundContact = dbHelper.findByName(name);
                if (foundContact != null) {
                    nameInput.setText(foundContact.getName());
                    phoneInput.setText(foundContact.getPhone());
                    Toast.makeText(this, "Contact found: " +
                            foundContact.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Contact not found",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(v -> {
            String oldPhone = phoneInput.getText().toString();
            String newName = nameInput.getText().toString();
            String newPhone = phoneInput.getText().toString();
            if (dbHelper.updateContact(oldPhone, newName,
                    newPhone)) {
                Toast.makeText(this, "Contact updated successfully!", Toast.LENGTH_SHORT).show();
                refreshContactsList(dbHelper, contacts, adapter,
                        contactsList);
            } else {
                Toast.makeText(this, "Failed to update contact",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshContactsList(DatabaseHelper dbHelper,
                                     List<Contact> contacts, ContactAdapter adapter, RecyclerView
                                             contactsList) {
        contacts = dbHelper.getAllContacts();
        adapter = new ContactAdapter((ArrayList<Contact>) contacts);
        contactsList.setAdapter(adapter);
    }
}

// Shared preferences
/*public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        sharedPref = getPreferences(MODE_PRIVATE);

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
               editor.putString("user_name", "Имя_пользователя");
                editor.apply();
                Toast.makeText(MainActivity.this, "Имя сохранено", Toast.LENGTH_SHORT).show();
            }
        });

        Button getButton = findViewById(R.id.getButton);
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = sharedPref.getString("user_name", "Имя_по_умолчанию");
                textView.setText("Имя пользователя: " + userName);
                Toast.makeText(MainActivity.this, "Имя получено", Toast.LENGTH_SHORT).show();
            }
        });

        Button changeButton = findViewById(R.id.changeButton);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создаем диалоговое окно для ввода нового имени
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Изменение имени");

                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", (dialog, which) -> {
                    String newName = input.getText().toString();
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("user_name", newName);
                    editor.apply();
                    Toast.makeText(MainActivity.this, "Имя изменено", Toast.LENGTH_SHORT).show();
               });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
               builder.show();
            }
        });

        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove("user_name");
                editor.apply();
                Toast.makeText(MainActivity.this, "Имя удалено", Toast.LENGTH_SHORT).show();
            }
        });
    }
}*/
