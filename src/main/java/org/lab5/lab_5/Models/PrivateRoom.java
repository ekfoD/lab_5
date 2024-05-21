package org.lab5.lab_5.Models;

import java.util.ArrayList;
import java.util.List;

public class PrivateRoom {
    private String client1;
    private String client2;
    List<String> messages;

    public PrivateRoom(String client1, String client2) {
        this.client1 = client1;
        this.client2 = client2;
        messages = new ArrayList<>();
    }

    public String getClient1() {
        return client1;
    }

    public void setClient1(String client1) {
        this.client1 = client1;
    }

    public String getClient2() {
        return client2;
    }

    public void setClient2(String client2) {
        this.client2 = client2;
    }

    public List<String> getMessages() {
        return messages;
    }
}
