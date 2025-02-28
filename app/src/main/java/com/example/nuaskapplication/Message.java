package com.example.nuaskapplication;

public class Message {
    private String message;
    private String sentBy;
    private int imageResId;
    private long timestamp;

    public static final String SENT_BY_ME = "me";
    public static final String SENT_BY_BOT = "bot";

    public Message() {}

    public Message(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
        this.timestamp = System.currentTimeMillis();
        this.imageResId = 0;
    }

    public Message(int imageResId, String sentBy) {
        this.message = null;
        this.sentBy = sentBy;
        this.timestamp = System.currentTimeMillis();
        this.imageResId = imageResId;
    }

    public String getMessage() {
        return message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public int getImageResId() {
        return imageResId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
