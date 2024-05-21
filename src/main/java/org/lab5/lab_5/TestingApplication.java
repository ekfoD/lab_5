package org.lab5.lab_5;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.lab5.lab_5.Utilities.MessageCoder;
import org.lab5.lab_5.Utilities.MessageParser;
import org.lab5.lab_5.Utilities.MessageTypes;

import java.util.ArrayList;
import java.util.List;

public class TestingApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ArrayList<String> testing = new ArrayList<>();
        testing.add("Ryklys");
        testing.add("Jonas");
        testing.add("GRygas");

        String codedMessage = MessageCoder.codeMessageDeeper(MessageTypes.GET_PRIVATE_ROOM, "Obuolys", "");
        System.out.println(codedMessage);

        String recipientSlashRoomIDFK = MessageParser.parseMessageFurther(
                MessageParser.parseMessage(codedMessage).getValue()
        ).getKey();

        System.out.println(recipientSlashRoomIDFK);
    }

    public static void main(String[] args) {
        launch();
    }
}
