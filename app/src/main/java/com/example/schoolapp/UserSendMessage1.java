package com.example.schoolapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.UserSendMessageAdapter;
import com.example.schoolapp.data_access.IUserDA;
import com.example.schoolapp.data_access.UserDAFactory;
import com.example.schoolapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserSendMessage1 extends AppCompatActivity {

    private EditText etUserSearch;
    private Button btnSearch;
    private RecyclerView rvUsers;
    private Button btnCancel;

    private UserSendMessageAdapter userAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_send_message1);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setupViews();
        setupRecyclerView();
        handleBtnSearch();
    }

    private void setupViews() {
        etUserSearch = findViewById(R.id.etUserSearch);
        btnSearch    = findViewById(R.id.btnSearch);
        rvUsers      = findViewById(R.id.rvUsers);
        btnCancel    = findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        IUserDA userDA = UserDAFactory.getUserDA(this);
        userDA.getAllUsers(new IUserDA.UserListCallback() {
            @Override
            public void onSuccess(List<User> list) {
                userList    = list;
                userAdapter = new UserSendMessageAdapter(userList);
                rvUsers.setAdapter(userAdapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(UserSendMessage1.this,
                                "Could not load users: " + error,
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void handleBtnSearch() {
        btnSearch.setOnClickListener(e -> {
            String nameOrId = etUserSearch.getText().toString().trim();
            if (nameOrId.isEmpty()) {
                // no query → reload full list
                setupRecyclerView();
                return;
            }

            IUserDA userDA = UserDAFactory.getUserDA(this);
            userDA.getAllUsers(new IUserDA.UserListCallback() {
                @Override
                public void onSuccess(List<User> list) {
                    List<User> filtered = new ArrayList<>();
                    String q = nameOrId.toLowerCase();

                    for (User u : list) {
                        if (String.valueOf(u.getUser_id()).contains(q)
                                || u.getFirstName().toLowerCase().contains(q)
                                || u.getLastName().toLowerCase().contains(q)) {
                            filtered.add(u);
                        }
                    }

                    // update adapter with filtered results
                    userAdapter.updateData(filtered);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(UserSendMessage1.this,
                                    "Search failed: " + error,
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            });
        });
    }
}
