package com.example.schoolapp.models;

import java.time.LocalDate;

public class Message {
    private Integer id;
    private Integer fromId;
    private Integer toId;
    private String title;
    private String content;
    private LocalDate sentDate;

    public Message() {

    }

    public Message(Integer id, Integer fromId, Integer toId, String title, String content, LocalDate sentDate) {
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.title = title;
        this.content = content;
        this.sentDate = sentDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
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
                "id=" + id +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", sentDate=" + sentDate +
                '}';
    }
}
