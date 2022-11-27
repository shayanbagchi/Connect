package com.app.chatapp.ModleClasses;

public class Users {
    public String uid;
    public String name;
    public String contact;
    public String status;
    public String imageUri;

    public Users() {
    }

    public Users(String uid, String name, String contact, String status, String imageUri) {
        this.uid = uid;
        this.name = name;
        this.contact = contact;
        this.status = status;
        this.imageUri = imageUri;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
