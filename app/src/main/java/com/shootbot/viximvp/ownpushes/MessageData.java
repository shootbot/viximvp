package com.shootbot.viximvp.ownpushes;

public class MessageData {
    private String type;
    private String invitationResponse;
    private String meetingType;
    private String first_name;
    private String last_name;
    private String email;
    private String inviterToken;
    private String meetingRoom;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInvitationResponse() {
        return invitationResponse;
    }

    public void setInvitationResponse(String invitationResponse) {
        this.invitationResponse = invitationResponse;
    }

    public String getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInviterToken() {
        return inviterToken;
    }

    public void setInviterToken(String inviterToken) {
        this.inviterToken = inviterToken;
    }

    public String getMeetingRoom() {
        return meetingRoom;
    }

    public void setMeetingRoom(String meetingRoom) {
        this.meetingRoom = meetingRoom;
    }
}
