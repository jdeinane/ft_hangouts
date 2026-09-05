package com.jubaldo.fthangouts.model;

public class Message {

    public static final int TYPE_SENT = 0;
    public static final int TYPE_RECEIVED = 1;

    private int id;
    private int contactId;
    private String body;
    private long timestamp;
    private int type;

    public Message(int contactId, String body, long timestamp, int type) {
        this.contactId = contactId;
        this.body = body;
        this.timestamp = timestamp;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
