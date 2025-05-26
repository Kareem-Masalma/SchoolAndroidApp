package com.example.schoolapp.data_access;

import com.example.schoolapp.models.Message;

import java.util.List;

public interface IMessageDA {

    public void getMessageById(int messageId, IMessageDA.SingleMessageCallback cb);
    public void getAllMessages(IMessageDA.MessageListCallback cb);
    public void sendMessage(Message m, IMessageDA.BaseCallback cb);
    public void deleteMessage(int messageId, IMessageDA.BaseCallback cb);

    public interface SingleMessageCallback {
        void onSuccess(Message m);
        void onError(String error);
    }

    public interface MessageListCallback {
        void onSuccess(List<Message> list);
        void onError(String error);
    }

    public interface BaseCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}
