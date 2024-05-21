package org.lab5.lab_5.Singletons;

import java.util.concurrent.LinkedBlockingQueue;

public class ClientMessagesSingleton {
    private final LinkedBlockingQueue<String> MESSAGES = new LinkedBlockingQueue<>(); // i cia gaunam visus msg is servo. turim listeneri on seperate thread
    private final LinkedBlockingQueue<String> MESSAGES_BY_CLIENT = new LinkedBlockingQueue<>(); // cia musu kliento siunciamos zinutes
    private ClientMessagesSingleton() {}

    private static class Wrapper {      // :OOOO it wooorks
        static ClientMessagesSingleton INSTANCE = new ClientMessagesSingleton();
    }
    public static ClientMessagesSingleton getInstance() {
        return Wrapper.INSTANCE;
    }

    public LinkedBlockingQueue<String> getMESSAGES() {
        return MESSAGES;
    }

    public LinkedBlockingQueue<String> getMESSAGES_BY_CLIENT() {
        return MESSAGES_BY_CLIENT;
    }
}
