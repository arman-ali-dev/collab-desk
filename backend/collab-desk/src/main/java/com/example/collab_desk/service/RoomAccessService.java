package com.example.collab_desk.service;

public interface RoomAccessService {
    public boolean canAccess(String email, Long roomId);
}
