package com.example.sc.transloom;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 101;
    private EditText inputEditText;
    private TextView translatedTextView;
    private Spinner sourceLanguageSpinner;
    private Spinner targetLanguageSpinner;
    private Button translateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputEditText = findViewById(R.id.inputText_to_translate);
        translatedTextView = findViewById(R.id.translatedText);
        sourceLanguageSpinner = findViewById(R.id.source_language_spinner);
        targetLanguageSpinner = findViewById(R.id.target_language_spinner);
        translateButton = findViewById(R.id.translate_button);

          //opening camera activity
        FloatingActionButton fabCamera = findViewById(R.id.floatingActionButton_camera);
        fabCamera.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CameraActivity.class)));

        // Checking and requesting for storage permission if necessary
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Requesting the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_STORAGE_PERMISSION);
        }

        // Populating language spinners
        populateLanguageSpinners();

        translateButton.setOnClickListener(v -> translateText());
    }
    // Method to populate language spinners
    private void populateLanguageSpinners() {
        List<String> languages = new ArrayList<>();
        languages.add("English");
        languages.add("French");
        languages.add("Hindi");
        languages.add("Urdu");
        languages.add("Chinese");
        languages.add("German");
        languages.add("Spanish");
        languages.add("Tamil");
        languages.add("Kannada");
        languages.add("Telugu");
        languages.add("Greek");
        languages.add("Arabic");
        languages.add("Turkish");
        languages.add("Italian");
        languages.add("Marathi");
        languages.add("Bengali");


        // Creating adapter for spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Setting adapters to spinners
        sourceLanguageSpinner.setAdapter(adapter);
        targetLanguageSpinner.setAdapter(adapter);
    }


    //Method to translate text
    private void translateText() {
        String inputText = inputEditText.getText().toString().trim();
        if (inputText.isEmpty()) {
            Toast.makeText(this, "Please enter text to translate", Toast.LENGTH_SHORT).show();
            return;
        }

        String sourceLanguage = sourceLanguageSpinner.getSelectedItem().toString();
        String targetLanguage = targetLanguageSpinner.getSelectedItem().toString();

        // Creating a TranslatorOptions object
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(getLanguageCode(sourceLanguage))
                .setTargetLanguage(getLanguageCode(targetLanguage))
                .build();

        // Creating a Translator object
        final Translator translator = Translation.getClient(options);

        // Downloading the language models if needed
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(aVoid -> {
                    // Translation models are available, perform translation
                    translator.translate(inputText)
                            .addOnSuccessListener(translatedText -> {
                                // Translation successful, display the translated text
                                translatedTextView.setText(translatedText);
                                Log.d("Translation", "Language models downloaded successfully");

                            })
                            .addOnFailureListener(e -> {
                                // Error occurred during translation
                                Toast.makeText(MainActivity.this, "Translation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Error occurred while downloading models
                    Log.e("Translation", "Failed to download language models: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Failed to download language models: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    // Helper method to get language code from language name
    private String getLanguageCode(String languageName) {
        switch (languageName) {
            case "English":
                return TranslateLanguage.ENGLISH;
            case "French":
                return TranslateLanguage.FRENCH;
            case "Hindi":
                return TranslateLanguage.HINDI;
            case "Urdu":
                return TranslateLanguage.URDU;// Urdu language code
            case "Chinese":
                return TranslateLanguage.CHINESE;
            case "German":
                return TranslateLanguage.GERMAN;
            case "Spanish":
                return TranslateLanguage.SPANISH;
            case "Tamil":
                return TranslateLanguage.TAMIL;
            case "Kannada":
                return TranslateLanguage.KANNADA;
            case "Telugu":
                return TranslateLanguage.TELUGU;
            case "Greek":
                return TranslateLanguage.GREEK;
            case "Arabic":
                return TranslateLanguage.ARABIC;
            case "Turkish":
                return TranslateLanguage.TURKISH;
            case "Italian":
                return TranslateLanguage.ITALIAN;
            case "Marathi":
                return TranslateLanguage.MARATHI;
            case "Bengali":
                return TranslateLanguage.BENGALI;
            default:
                return TranslateLanguage.ENGLISH; // Default to English
        }
    }

    // Handling permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceeding with translation
                translateText();
            } else {
                // Permission denied, showing a message
                Toast.makeText(this, "Storage permission denied. Cannot proceed with translation.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
