package org.lab5.lab_5.Models;

public class Client {
    private String name;
    private Room room;
    public Client(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
