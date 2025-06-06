package com.example.schoolapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolapp.data_access.ILoginDA;
import com.example.schoolapp.data_access.IUserDA;
import com.example.schoolapp.data_access.LoginDAFactory;
import com.example.schoolapp.data_access.UserDAFactory;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.List;

public class Login extends AppCompatActivity {

    // view references
    private ImageView logoImage;
    private TextView titleText;
    private TextInputEditText etID;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private TextView tvForgotText;

    public static final String LOGGED_IN_USER = "LOGGED_IN_USER";
    public static final String LOGGED_IN = "LOGGED_IN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        setupViews();
        handleLoginBtn();
    }

    private void handleLoginBtn() {
        btnLogin.setOnClickListener(e -> {
            String user_id = etID.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (user_id.isEmpty() || password.isEmpty()) {
                // TODO display message
                Log.i("input_data", user_id + " " + password);
            } else {
                ILoginDA loginDA = LoginDAFactory.getLoginDA(this);
                loginDA.login(user_id, password, new ILoginDA.LoginCallback() {
                    @Override
                    public void onSuccess(User user) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Login.this);
                        SharedPreferences.Editor editor = prefs.edit();
                        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
                        String json = gson.toJson(user);
                        editor.putString(LOGGED_IN_USER, json);
                        editor.putBoolean(LOGGED_IN, true);
                        editor.apply();
                        Toast.makeText(Login.this, "Welcome " + user.getFirstName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(Login.this, error, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }


    private void setupViews() {
        logoImage = findViewById(R.id.logoImage);
        titleText = findViewById(R.id.titleText);
        etID = findViewById(R.id.etID);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotText = findViewById(R.id.tvForgotText);

    }
}
