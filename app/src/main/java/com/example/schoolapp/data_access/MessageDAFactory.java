package com.example.schoolapp.data_access;

import android.content.Context;

public class MessageDAFactory {
    public static IMessageDA getMessageDA(Context context) {
        return new MessageDA(context);
    }
}
