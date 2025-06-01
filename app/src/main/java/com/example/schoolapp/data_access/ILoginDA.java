package com.example.schoolapp.data_access;

import com.example.schoolapp.models.User;

import org.json.JSONObject;

public interface ILoginDA  {

    public void login(String userId, String password, ILoginDA.LoginCallback callback);

    public interface LoginCallback {
        void onSuccess(User user);
        void onError(String error);
    }
}
