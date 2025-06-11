package com.example.schoolapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import com.example.schoolapp.data_access.IStudentDA;
import com.example.schoolapp.data_access.ITeacherDA;
import com.example.schoolapp.data_access.IUserDA;
import com.example.schoolapp.data_access.LoginDAFactory;
import com.example.schoolapp.data_access.StudentDA;
import com.example.schoolapp.data_access.StudentDAFactory;
import com.example.schoolapp.data_access.TeacherDA;
import com.example.schoolapp.data_access.TeacherDAFactory;
import com.example.schoolapp.data_access.UserDAFactory;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.Role;
import com.example.schoolapp.models.Student;
import com.example.schoolapp.models.Teacher;
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

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setupViews();
        handleLoginBtn();
    }

    private void handleLoginBtn() {
        btnLogin.setOnClickListener(e->{
            String user_id = etID.getText().toString().trim();
//            Log.i("login_user", user_id);
            String password = etPassword.getText().toString().trim();
            if(user_id.isEmpty() || password.isEmpty()){
                // TODO display message
                Log.i("input_data", user_id + " " + password);
            }
            else{
                SharedPreferences   prefs = PreferenceManager.getDefaultSharedPreferences(Login.this);
                SharedPreferences.Editor editor =  prefs.edit();
                editor.remove(Login.LOGGED_IN_USER);
                editor.remove(Login.LOGGED_IN);
                editor.commit();
                ILoginDA loginDA = LoginDAFactory.getLoginDA(this);
                loginDA.login(user_id, password, new ILoginDA.LoginCallback() {
                    @Override
                    public void onSuccess(User user) {

                        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
//                        Log.i("birth_date" , user.getBirthDate().toString());

                        // WRITE THE SUBTYPE SPECIFIC ATTRIBUTES
                        Log.i("Role" , user.getRole().toString());
                        if(user.getRole() == Role.TEACHER){
                            ITeacherDA teacherDA = TeacherDAFactory.getTeacherDA(Login.this);
                            teacherDA.findTeacherById(user.getUser_id(), new TeacherDA.SingleTeacherCallback() {
                                @Override
                                public void onSuccess(Teacher teacher) {
                                    String json = gson.toJson(teacher);
                                    editor.putString(LOGGED_IN_USER,json);
                                    editor.putBoolean(LOGGED_IN, true);
                                    editor.commit();
                                    // TODO -- DELETE THESE LINES, THEY ARE FOR TESTING ONLY
//                                    Log.i("loggedin_teacher", teacher.getFirstName() + " " + teacher.getLastName());
                                    Intent intent = new Intent(Login.this, Profile.class);
                                    startActivity(intent);

                                }
                                @Override
                                public void onError(String error) {
                                    Toast.makeText(Login.this, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else if(user.getRole() == Role.STUDENT){
                            IStudentDA studentDA = StudentDAFactory.getStudentDA(Login.this);
                            studentDA.getStudentById(user.getUser_id(), new StudentDA.SingleStudentCallback() {
                                @Override
                                public void onSuccess(Student s) {
                                    String json = gson.toJson(s);
                                    editor.putString(LOGGED_IN_USER,json);
                                    editor.putBoolean(LOGGED_IN, true);
                                    editor.commit();
                                    // TODO -- DELETE THESE LINES, THEY ARE FOR TESTING ONLY
                                    Intent intent = new Intent(Login.this, Profile.class);
                                    startActivity(intent);

                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(Login.this, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            String json = gson.toJson(user);
                            editor.putString(LOGGED_IN_USER,json);
                            editor.putBoolean(LOGGED_IN, true);
                            editor.commit();
                            // TODO -- DELETE THESE LINES, THEY ARE FOR TESTING ONLY
                            Intent intent = new Intent(Login.this, Profile.class);
                            startActivity(intent);

                        }
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


