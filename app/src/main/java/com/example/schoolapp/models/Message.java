package com.example.schoolapp.models;

import java.time.LocalDate;

public class Message {
    private Integer message_id;
    private Integer from_user_id;
    private Integer to_user_id;
    private String title;
    private String content;
    private LocalDate sentDate;

    public Message() {

    }

    public Message(Integer message_id, Integer from_user_id, Integer to_user_id, String title, String content, LocalDate sentDate) {
        this.message_id = message_id;
        this.from_user_id = from_user_id;
        this.to_user_id = to_user_id;
        this.title = title;
        this.content = content;
        this.sentDate = sentDate;
    }

    public Integer getMessage_id() {
        return message_id;
    }

    public void setMessage_id(Integer message_id) {
        this.message_id = message_id;
    }

    public Integer getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(Integer from_user_id) {
        this.from_user_id = from_user_id;
    }

    public Integer getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(Integer to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDate sentDate) {
        this.sentDate = sentDate;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + message_id +
                ", fromId=" + from_user_id +
                ", toId=" + to_user_id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", sentDate=" + sentDate +
                '}';
    }
}
