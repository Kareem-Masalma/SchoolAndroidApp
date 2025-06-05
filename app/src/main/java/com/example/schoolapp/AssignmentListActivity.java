package com.example.schoolapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.schoolapp.adapters.AssignmentAdapter;
import com.example.schoolapp.data_access.AssignmentDA;
import com.example.schoolapp.data_access.IAssignmentDA;
import com.example.schoolapp.models.Assignment;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.*;

public class AssignmentListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AssignmentAdapter adapter;
    private List<Assignment> assignmentList = new ArrayList<>();
    private Map<Assignment, String> classTitleMap = new HashMap<>(); // NEW

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_list);

        recyclerView = findViewById(R.id.recyclerAssignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AssignmentAdapter(assignmentList, assignment -> {
            // Get class title from map
            String classTitle = classTitleMap.get(assignment);
            Intent intent = new Intent(this, AssignmentDetailsActivity.class); // or SubmitAssignmentActivity.class
            intent.putExtra("ASSIGNMENT_JSON", new Gson().toJson(assignment));
            intent.putExtra("CLASS_TITLE", classTitle); // send it separately
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        new AssignmentDA(this).getAllAssignments(new IAssignmentDA.AssignmentListCallback() {
            @Override
            public void onSuccess(List<JSONObject> data) {
                for (JSONObject obj : data) {
                    try {
                        Assignment assignment = new Gson().fromJson(obj.toString(), Assignment.class);
                        assignmentList.add(assignment);
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
