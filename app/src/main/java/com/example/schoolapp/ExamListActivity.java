package com.example.schoolapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.adapters.ExamAdapter;
import com.example.schoolapp.data_access.ExamDA;
import com.example.schoolapp.data_access.IExamDA;
import com.example.schoolapp.json_helpers.LocalDateAdapter;
import com.example.schoolapp.models.Exam;
import com.example.schoolapp.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.*;

public class ExamListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExamAdapter adapter;
    private List<Exam> examList = new ArrayList<>();
    private List<Exam> fullExamList = new ArrayList<>();
    private Map<Exam, String> subjectTitleMap = new HashMap<>();
    private Map<Exam, String> classTitleMap = new HashMap<>();

    private boolean ascending = true;
    private Spinner spinnerSortBy;
    private RadioGroup radioGroupSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        EditText editSearch = findViewById(R.id.editSearch);
        spinnerSortBy = findViewById(R.id.spinnerSortBy);
        radioGroupSortOrder = findViewById(R.id.radioGroupSortOrder);
        recyclerView = findViewById(R.id.recyclerExams);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayAdapter<String> sortOptionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Date", "Subject", "Title"});
        sortOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortBy.setAdapter(sortOptionsAdapter);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                filterAndSortExams(s.toString());
            }
        });

        spinnerSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAndSortExams(editSearch.getText().toString());
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        radioGroupSortOrder.setOnCheckedChangeListener((group, checkedId) -> {
            ascending = (checkedId == R.id.radioAscending);
            filterAndSortExams(editSearch.getText().toString());
        });

        adapter = new ExamAdapter(examList, subjectTitleMap, classTitleMap, exam -> {
            String classTitle = classTitleMap.getOrDefault(exam, "Unknown");
            String subjectTitle = subjectTitleMap.getOrDefault(exam, "Unknown");
            Intent intent = new Intent(this, ExamDetailsActivity.class);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
            intent.putExtra("EXAM_JSON", gson.toJson(exam));
            intent.putExtra("SUBJECT_TITLE", subjectTitle);
            intent.putExtra("CLASS_TITLE", classTitle);
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

        new ExamDA(this).getAllExams(studentId, new IExamDA.ExamListWithTitleCallback() {
            @Override
            public void onSuccess(List<Exam> data) {
            }

            @Override
            public void onSuccessWithTitles(List<Exam> exams, Map<Exam, String> subjectMap, Map<Exam, String> classMap) {
                examList.clear();
                fullExamList.clear();
                subjectTitleMap.clear();
                classTitleMap.clear();

                examList.addAll(exams);
                fullExamList.addAll(exams);
                subjectTitleMap.putAll(subjectMap);
                classTitleMap.putAll(classMap);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ExamListActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterAndSortExams(String query) {
        List<Exam> filtered = new ArrayList<>();
        for (Exam e : fullExamList) {
            String title = e.getTitle().toLowerCase();
            String subject = subjectTitleMap.getOrDefault(e, "").toLowerCase();
            if (title.contains(query.toLowerCase()) || subject.contains(query.toLowerCase())) {
                filtered.add(e);
            }
        }

        int selectedSort = spinnerSortBy.getSelectedItemPosition();
        Comparator<Exam> comparator = null;
        switch (selectedSort) {
            case 0:
                comparator = Comparator.comparing(Exam::getDate);
                break;
            case 1:
                comparator = Comparator.comparing(e -> subjectTitleMap.getOrDefault(e, "").toLowerCase());
                break;
            case 2:
                comparator = Comparator.comparing(e -> e.getTitle().toLowerCase());
                break;
        }

        if (comparator != null) {
            filtered.sort(ascending ? comparator : comparator.reversed());
        }

        adapter.updateData(filtered);
    }
}
