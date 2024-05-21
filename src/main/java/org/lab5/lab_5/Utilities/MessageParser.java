package org.lab5.lab_5.Utilities;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MessageParser {
    public static Pair<MessageTypes, String> parseMessage(String message) {
        StringTokenizer tokenizer = new StringTokenizer(message, "}}}");

        MessageTypes type;

        switch (tokenizer.nextToken()) {
            // client
            case "GET_CLIENT_NAMES":
                type = MessageTypes.GET_CLIENT_NAMES;
                break;
            case "GET_ROOM_NAMES":
                type = MessageTypes.GET_ROOM_NAMES;
                break;
            case "ADD_ROOM":
                type = MessageTypes.ADD_ROOM;
                break;
            case "ASSIGN_ROOM":
                type = MessageTypes.ASSIGN_ROOM;
                break;
            case "SEND_MESSAGE":
                type = MessageTypes.SEND_MESSAGE;
                break;
            case "SEND_PRIVATE_MESSAGE":
                type = MessageTypes.SEND_PRIVATE_MESSAGE;
                break;
            case "GET_PRIVATE_ROOM":
                type = MessageTypes.GET_PRIVATE_ROOM;
                break;
            case "GET_ROOM_MESSAGES":
                type = MessageTypes.GET_ROOM_MESSAGES;
                break;

                // servo
            case "CLIENT_NAMES":
                type = MessageTypes.CLIENT_NAMES;
                break;
            case "ROOM_NAMES":
                type = MessageTypes.ROOM_NAMES;
                break;
            case "RECIEVE_MESSAGE":
                type = MessageTypes.RECIEVE_MESSAGE;
                break;

                // universalus
            default:
                type = MessageTypes.ERROR;
                break;
        }

        if (tokenizer.countTokens() == 0)
            return new Pair<>(type, "");
        return new Pair<>(type, tokenizer.nextToken());
    }

    public static Pair<String, String> parseMessageFurther(String message) {
        StringTokenizer tokenizer = new StringTokenizer(message, "{{{");

        if (tokenizer.countTokens() == 0)
            return new Pair<>("error", "error");
        if (tokenizer.countTokens() == 1)
            return new Pair<>(tokenizer.nextToken(), "");
        return new Pair<>(tokenizer.nextToken(), tokenizer.nextToken());
    }

    public static List<String> getListFromMessageWithSymbols(String message) {
        List<String> objectNames = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(message, "#$%");

        while (tokenizer.hasMoreElements())
            objectNames.add(tokenizer.nextToken());
        return objectNames;
    }
}
