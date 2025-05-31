package com.example.schoolapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Profile extends AppCompatActivity {

    // 1) Declare member variables for every view you want to initialize
    private ImageView        imageProfile;
    private TextView         textFullName;
    private TextView         textRole;
    private TextView         textBirthDate;
    private TextView         textAddress;
    private TextView         textPhone;
    private CardView         cardInfo;
    private FloatingActionButton fabEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        setupViews();

    }


    private void setupViews() {
        imageProfile    = findViewById(R.id.image_profile);
        textFullName    = findViewById(R.id.text_full_name);
        textRole        = findViewById(R.id.text_role);
        textBirthDate   = findViewById(R.id.text_birth_date);
        textAddress     = findViewById(R.id.text_address);
        textPhone       = findViewById(R.id.text_phone);
        cardInfo        = findViewById(R.id.card_info);
        fabEdit         = findViewById(R.id.fab_edit_profile);

    }
}
