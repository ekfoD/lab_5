package org.lab5.lab_5.ViewControllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import org.lab5.lab_5.Utilities.ExportUtilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerViewController {

    @FXML
    private ChoiceBox<String> exportTypeChoiceBox;

    @FXML
    private TextArea serverLogs;

    @FXML
    void initialize() {
        exportTypeChoiceBox.setItems(FXCollections.observableArrayList("CSV", "TXT", "JSON"));
    }
    @FXML
    void exportServerLogs(ActionEvent event) {
        if (exportTypeChoiceBox.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Pasirinkite failo tipą!");
            alert.show();
            return;
        }

        System.out.println(serverLogs.getText());

        switch (exportTypeChoiceBox.getValue()) {
            case "CSV":
                ExportUtilities.exportCSV(serverLogs.getText());
                break;
            case "TXT":
                ExportUtilities.exportTXT(serverLogs.getText());
                break;
            case "JSON":
                ExportUtilities.exportJSON(serverLogs.getText());
                break;
            default:
                System.out.println("how");
                break;
        }
    }
    public void addMessageToServerLog(String message) {
        Platform.runLater(() -> {
            Date date = new Date(System.currentTimeMillis());
            String dateFormatted = new SimpleDateFormat().format(date);
            serverLogs.appendText(dateFormatted + " :: " + message + "\n");
        });
    }
}

