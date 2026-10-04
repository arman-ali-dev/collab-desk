package com.example.collab_desk.service;

public interface EmailService {
    void sendInvitation(String to, String fullName, String link);
}
