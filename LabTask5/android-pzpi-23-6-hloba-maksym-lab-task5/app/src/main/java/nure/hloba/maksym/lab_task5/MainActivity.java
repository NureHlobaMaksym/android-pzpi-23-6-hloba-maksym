package nure.hloba.maksym.lab_task5;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        NotesManager.initialize(this);
        applySavedTheme();
        applySavedFontSize();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new NotesListFragment())
                    .commit();
        }
    }

    private void applySavedFontSize() {
        SharedPreferences preferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        int fontSizeIndex = preferences.getInt("fontSizeIndex", 1);
        float scale = SettingsFragment.fontSizes[fontSizeIndex];
        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = scale;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        getResources().updateConfiguration(configuration, metrics);
    }

    private void applySavedTheme() {
        SharedPreferences preferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        boolean isDarkTheme = preferences.getBoolean("isDarkTheme", false);
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}