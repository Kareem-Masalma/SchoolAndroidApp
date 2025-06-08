package com.example.schoolapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schoolapp.adapters.AssignmentAdapter;
import com.example.schoolapp.data_access.AssignmentDA;
import com.example.schoolapp.data_access.IAssignmentDA;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.Assignment;
import com.example.schoolapp.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.*;

public class AssignmentListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AssignmentAdapter adapter;
    private List<Assignment> assignmentList = new ArrayList<>();

    private Map<Assignment, String> subjectTitleMap = new HashMap<>();
    private Map<Assignment, String> classTitleMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_list);

        recyclerView = findViewById(R.id.recyclerAssignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AssignmentAdapter(assignmentList, subjectTitleMap,assignment -> {
            // Get class title from map
            String classTitle = classTitleMap.get(assignment);
            String subjectTitle = subjectTitleMap.get(assignment);
            Intent intent = new Intent(this, AssignmentDetailsActivity.class);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
            intent.putExtra("ASSIGNMENT_JSON", gson.toJson(assignment));
            intent.putExtra("SUBJECT_TITLE", subjectTitle);
            intent.putExtra("CLASS_TITLE", classTitle); // send it separately
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = prefs.getString(Login.LOGGED_IN_USER, null);
        User user = null;

        if (json != null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
            user = gson.fromJson(json, User.class);
        }
        int studentId = user.getUser_id();

        new AssignmentDA(this).getAllAssignments(studentId, new IAssignmentDA.AssignmentListCallback() {
            @Override
            public void onSuccess(List<JSONObject> data) {
                for (JSONObject obj : data) {
                    try {
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                                .create();
                        Assignment assignment = new Assignment(obj.getInt("assignment_id"), obj.getInt("subject_id"),obj.getString("title"),obj.getString("details"),LocalDate.parse(obj.getString("start_date")),obj.getString("file_path"),LocalDate.parse(obj.getString("end_date")), (float) obj.getDouble("percentage_of_grade"));
                        assignmentList.add(assignment);

                        if (obj.has("subject_title")) {
                            subjectTitleMap.put(assignment, obj.getString("subject_title"));
                        }
                        // Extract and store class title
                        if (obj.has("class_title")) {
                            classTitleMap.put(assignment, obj.getString("class_title"));
                        } else {
                            classTitleMap.put(assignment, "Unknown");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AssignmentListActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
