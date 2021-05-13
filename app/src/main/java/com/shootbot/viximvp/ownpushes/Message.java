package com.shootbot.viximvp.ownpushes;

import java.util.List;

public class Message {
    private List<String> to;
    private MessageData data;

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public MessageData getData() {
        return data;
    }

    public void setData(MessageData data) {
        this.data = data;
    }
}
