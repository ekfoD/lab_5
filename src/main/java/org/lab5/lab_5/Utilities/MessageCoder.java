package org.lab5.lab_5.Utilities;

import java.util.ArrayList;
import java.util.List;

public class MessageCoder {
    public static String codeMessage(MessageTypes type, String message) {
        StringBuilder str = new StringBuilder();
        str.append(type.toString() + "}}}" + message);
        return str.toString();
    }

    public static String codeMessageDeeper(MessageTypes type, String obj, String message) {
        StringBuilder str = new StringBuilder();
        str.append(type.toString() + "}}}" + obj + "{{{" + message);
        return str.toString();
    }

    public static String getMessageWithSymbolsFromList(List<String> data) {
        StringBuilder str = new StringBuilder();
        for (String object : data) {
            str.append(object);
            if (data.indexOf(object) + 1 != data.size())
                str.append("#$%");
        }
        return str.toString();
    }
}
