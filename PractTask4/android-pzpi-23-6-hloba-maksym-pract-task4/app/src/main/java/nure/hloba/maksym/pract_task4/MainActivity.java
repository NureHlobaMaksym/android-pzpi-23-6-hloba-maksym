package nure.hloba.maksym.pract_task4;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText nameEditText, ageEditText;
    private Button addToSharedPreferencesButton, addToDataBaseButton, addAgeToTextFileButton, downloadAgeFromTextFileButton;
    private RecyclerView userRecyclerView;
    private DBHelper dBHelper;
    private UserAdapter adapter;
    private List<User> users;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeUI();
        initializeDatabase();
        initializeSharedPreferences();
        setupRecyclerView();
        loadUsersFromDatabase();
        loadSharedPreferencesData();
        setupEventListeners();
    }

    private void initializeUI() {
        nameEditText = findViewById(R.id.nameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        addToSharedPreferencesButton = findViewById(R.id.addToSharedPreferencesButton);
        addToDataBaseButton = findViewById(R.id.addToDataBaseButton);
        addAgeToTextFileButton = findViewById(R.id.addAgeToTextFileButton);
        downloadAgeFromTextFileButton = findViewById(R.id.downloadAgeFromTextFileButton);
        userRecyclerView = findViewById(R.id.userRecyclerView);
    }

    private void initializeDatabase() {
        dBHelper = new DBHelper(this);
    }

    private void initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void setupRecyclerView() {
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        users = new ArrayList<>();
        adapter = new UserAdapter(users);
        userRecyclerView.setAdapter(adapter);
    }

    private void loadUsersFromDatabase() {
        Cursor cursor = dBHelper.getReadableDatabase().query("users", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int age = cursor.getInt(cursor.getColumnIndex("age"));
            users.add(new User(name, age));
        }
        cursor.close();
    }

    private void loadSharedPreferencesData() {
        String name = sharedPreferences.getString("name", "");
        int age = sharedPreferences.getInt("age", -1);
        nameEditText.setText(name);
        ageEditText.setText(age != -1 ? Integer.toString(age) : "");
    }

    private void setupEventListeners() {
        addToSharedPreferencesButton.setOnClickListener(view -> saveToSharedPreferences());
        addToDataBaseButton.setOnClickListener(view -> saveToDatabase());
        addAgeToTextFileButton.setOnClickListener(view -> saveAgeToTextFile());
        downloadAgeFromTextFileButton.setOnClickListener(view -> loadAgeFromTextFile());
    }

    private void saveToSharedPreferences() {
        editor.putString("name", nameEditText.getText().toString());
        if (!ageEditText.getText().toString().isEmpty()) {
            editor.putInt("age", Integer.parseInt(ageEditText.getText().toString()));
        }
        editor.apply();
    }

    private void saveToDatabase() {
        String name = nameEditText.getText().toString();
        String ageText = ageEditText.getText().toString();
        int age = Integer.parseInt(ageText);

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("age", age);
        dBHelper.getWritableDatabase().insert("users", null, values);

        users.add(new User(name, age));
        adapter.setUsers(users);
    }

    private void saveAgeToTextFile() {
        try (FileOutputStream fos = openFileOutput("myfile.txt", Context.MODE_PRIVATE)) {
            fos.write(ageEditText.getText().toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAgeFromTextFile() {
        try (FileInputStream fis = openFileInput("myfile.txt")) {
            StringBuilder age = new StringBuilder();
            int c;
            while ((c = fis.read()) != -1) {
                age.append((char) c);
            }
            ageEditText.setText(age.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
