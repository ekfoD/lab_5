package org.lab5.lab_5.ViewControllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.lab5.lab_5.Singletons.ClientMessagesSingleton;
import org.lab5.lab_5.Utilities.MessageCoder;
import org.lab5.lab_5.Utilities.MessageParser;
import org.lab5.lab_5.Utilities.MessageTypes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ClientViewController {

    @FXML
    private TextArea allMessagesField;

    @FXML
    private Label locationName;

    @FXML
    private TextField messageInput;

    @FXML
    private TextField roomName;

    @FXML
    private ChoiceBox<String> roomSelect;

    @FXML
    private Label clientName;

    @FXML
    private ChoiceBox<String> clientSelect;
    public String selectedRoom;
    public String selectedClient;

    @FXML
    void initialize() {
        selectedRoom = "-";
        selectedClient = "-";
    }

    @FXML
    void createRoom(ActionEvent event) throws InterruptedException {
        if (!Objects.equals(roomName.getText(), "")) {
            String codedMessage = MessageCoder.codeMessage(MessageTypes.ADD_ROOM, roomName.getText());
            roomName.setText("");
            ClientMessagesSingleton.getInstance().getMESSAGES_BY_CLIENT().put(codedMessage);
        }
    }

    @FXML
    void openSelectedRoomChat(ActionEvent event) throws InterruptedException {
        if (roomSelect.getValue() != null) {
            selectedClient = "-";
            selectedRoom = roomSelect.getValue();
            locationName.setText(selectedRoom);
            roomSelect.setValue("");
            String codedMessage = MessageCoder.codeMessageDeeper(MessageTypes.GET_ROOM_MESSAGES, selectedRoom, "");
            allMessagesField.clear();
            ClientMessagesSingleton.getInstance().getMESSAGES_BY_CLIENT().put(codedMessage);
        }
    }

    @FXML
    void openSelectedClientChat(ActionEvent event) throws InterruptedException {
        if (clientSelect.getValue() != null) {
            selectedRoom = "-";
            selectedClient = clientSelect.getValue();
            locationName.setText("PM su "+ selectedClient);
            clientSelect.setValue("");
            String codedMessage = MessageCoder.codeMessageDeeper(MessageTypes.GET_PRIVATE_ROOM, selectedClient, "");
            allMessagesField.clear();
            ClientMessagesSingleton.getInstance().getMESSAGES_BY_CLIENT().put(codedMessage);
        }
    }

    @FXML
    void sendMessage(ActionEvent event) throws InterruptedException {
        String message = messageInput.getText();
        String codedMessage;
        if (!message.isEmpty()) {
            if (Objects.equals(selectedClient, "-") && !Objects.equals(selectedRoom, "-")) {
                codedMessage = MessageCoder.codeMessageDeeper(MessageTypes.SEND_MESSAGE, selectedRoom, message);
                System.out.println("siunciam is kambario: " + selectedRoom);
            }

            else if (Objects.equals(selectedRoom, "-") && !Objects.equals(selectedClient, "-")){
                codedMessage = MessageCoder.codeMessageDeeper(MessageTypes.SEND_PRIVATE_MESSAGE, selectedClient, message);
                System.out.println("siunciam klientui: " + selectedClient);
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Pasirinkite pokalbių kambarį arba privatų pokalbį!");
                alert.show();
                return;
            }
            ClientMessagesSingleton.getInstance().getMESSAGES_BY_CLIENT().put(codedMessage);
        }
    }

    public void addMessageToChat(String message) {
        Platform.runLater(() -> {
            Date date = new Date(System.currentTimeMillis());
            String dateFormatted = new SimpleDateFormat().format(date);
            allMessagesField.appendText("\n" + dateFormatted + " " + message);
        });
    }

    public void setClientName(String name) {
        clientName.setText(name);
    }

    public void updateRoomSelection(List<String> updatedRooms) {
        Platform.runLater(() -> {
            roomSelect.getItems().clear();
            roomSelect.setItems(FXCollections.observableList(updatedRooms));
        });
    }

    public void updateClientSelection(List<String> updatedClients) {
        Platform.runLater(() -> {
            clientSelect.getItems().clear();
            updatedClients.remove(clientName.getText());
            clientSelect.setItems(FXCollections.observableList(updatedClients));
        });
    }
}
