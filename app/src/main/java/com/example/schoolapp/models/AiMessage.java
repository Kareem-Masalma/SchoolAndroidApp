package com.example.schoolapp.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model for a chat message in the AI assistant UI.
 * Contains:
 * - role: USER, ASSISTANT, SYSTEM, or TYPING (or other if you extend)
 * - content: the text content (for TYPING role, content can be ignored or e.g. "...")
 * - timestamp: milliseconds since epoch when created
 *
 * You can extend this with message IDs, delivery status, etc., if needed.
 */
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

    // Constructors

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

    // Convenience factory methods

    /** Create a user-sent message. */
    public static AiMessage createUserMessage(String content) {
        return new AiMessage(Role.USER, content);
    }

    /** Create an assistant (bot) message. */
    public static AiMessage createAssistantMessage(String content) {
        return new AiMessage(Role.ASSISTANT, content);
    }

    /**
     * Create a system message. You might use this internally (e.g., initial prompt).
     * In UI you may or may not display system messages.
     */
    public static AiMessage createSystemMessage(String content) {
        return new AiMessage(Role.SYSTEM, content);
    }

    /** Create a typing indicator message. content can be ignored or a placeholder. */
    public static AiMessage createTypingIndicator() {
        return new AiMessage(Role.TYPING, null);
    }

    // Getters & setters

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Get content. For TYPING role, this might be null or a placeholder.
     */
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /** Timestamp in milliseconds since epoch. */
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Convenience checks

    /** true if this is a user message. */
    public boolean isUser() {
        return this.role == Role.USER;
    }

    /** true if this is an assistant (bot) message. */
    public boolean isAssistant() {
        return this.role == Role.ASSISTANT;
    }

    /** true if this is a system message. */
    public boolean isSystem() {
        return this.role == Role.SYSTEM;
    }

    /** true if this is a typing indicator. */
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
