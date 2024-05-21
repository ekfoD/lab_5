package org.lab5.lab_5.Models;

import java.util.ArrayList;
import java.util.List;

public class Room {
    String name;
    List<String> messages;

    public Room(String name) {
        this.name = name;
        messages = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
