package com.example.schoolapp.data_access;

import android.content.Context;

public class LoginDAFactory {
    public static ILoginDA getLoginDA(Context context) {
        return new LoginDA(context);
    }
}
