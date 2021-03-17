package com.example.chatpost.Models;

public class Users {

    String profilePicture , userName ,  mail , password , userId , lastMessage , status,user_offline_online_status;

    public String getUser_offline_online_status() {
        return user_offline_online_status;
    }

    public void setUser_offline_online_status(String user_offline_online_status) {
        this.user_offline_online_status = user_offline_online_status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Users(String profilePicture, String userName, String mail, String password, String userId, String lastMessage) {
        this.profilePicture = profilePicture;
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
        this.lastMessage = lastMessage;
    }

    public Users() {
    }

    // signup constructor..

    public Users( String userName, String mail, String password) {
        this.userName = userName;
        this.mail = mail;
        this.password = password;
    }


    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
