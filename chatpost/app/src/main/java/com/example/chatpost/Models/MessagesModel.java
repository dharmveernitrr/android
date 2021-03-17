package com.example.chatpost.Models;

import android.widget.ImageView;

public class MessagesModel {

    String uId , message , messageId;

    String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    Integer android_reaction;

    public Integer getAndroid_reaction() {
        return android_reaction;
    }

    public void setAndroid_reaction(Integer android_reaction) {
        this.android_reaction = android_reaction;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    Long timestamp;

    public MessagesModel(String uId, String message, Long timestamp) {
        this.uId = uId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public MessagesModel(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }

    public MessagesModel() {
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
