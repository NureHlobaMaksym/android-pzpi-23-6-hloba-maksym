package nure.hloba.maksym.lab_task5;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    public static float[] fontSizes = new float[]{0.7f, 0.85f, 1f};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner fontSizeSpinner = view.findViewById(R.id.fontSizeSpinner);
        Switch themeSwitch = view.findViewById(R.id.themeSwitch);
        ImageView backArrowSettingsImageView = view.findViewById(R.id.backArrowSettingsImageView);

        SharedPreferences preferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        boolean isDarkTheme = preferences.getBoolean("isDarkTheme", false);
        themeSwitch.setChecked(isDarkTheme);
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("isDarkTheme", isChecked);
            editor.apply();

            requireActivity().recreate();
        });
        int currentFontSizeIndex = preferences.getInt("fontSizeIndex", 1);
        fontSizeSpinner.setSelection(currentFontSizeIndex);

        fontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != currentFontSizeIndex) {
                    editor.putInt("fontSizeIndex", position);
                    editor.apply();
                    applyFontSize(fontSizes[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        backArrowSettingsImageView.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void applyFontSize(float fontSize) {
        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = fontSize;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        getResources().updateConfiguration(configuration, metrics);
        requireActivity().recreate();
    }
}
