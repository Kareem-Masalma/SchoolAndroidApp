package com.example.schoolapp.models;

import java.io.Serializable;
import java.util.Objects;


public class AiMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Role {
        USER,
        ASSISTANT,
        SYSTEM,
        TYPING
    }

    private Role role;
    private String content;
    private long timestamp;

    // constructors

    /**
     * Main constructor. Timestamp is set to now.
     * @param role    Role of the message (USER, ASSISTANT, SYSTEM, TYPING).
     * @param content The text content. For TYPING, content may be null or "...".
     */
    public AiMessage(Role role, String content) {
        this.role = role;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructor with explicit timestamp (e.g., if restoring from persistence).
     * @param role      Role of the message.
     * @param content   Text content.
     * @param timestamp Millis since epoch.
     */
    public AiMessage(Role role, String content, long timestamp) {
        this.role = role;
        this.content = content;
        this.timestamp = timestamp;
    }


    public static AiMessage createUserMessage(String content) {
        return new AiMessage(Role.USER, content);
    }

    public static AiMessage createAssistantMessage(String content) {
        return new AiMessage(Role.ASSISTANT, content);
    }


    public static AiMessage createSystemMessage(String content) {
        return new AiMessage(Role.SYSTEM, content);
    }

    public static AiMessage createTypingIndicator() {
        return new AiMessage(Role.TYPING, null);
    }



    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Convenience checks


    public boolean isUser() {
        return this.role == Role.USER;
    }


    public boolean isAssistant() {
        return this.role == Role.ASSISTANT;
    }


    public boolean isSystem() {
        return this.role == Role.SYSTEM;
    }


    public boolean isTypingIndicator() {
        return this.role == Role.TYPING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AiMessage that = (AiMessage) o;
        return timestamp == that.timestamp &&
                role == that.role &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, content, timestamp);
    }

    @Override
    public String toString() {
        return "AiMessage{" +
                "role=" + role +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
