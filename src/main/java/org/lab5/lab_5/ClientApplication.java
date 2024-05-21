package org.lab5.lab_5;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.lab5.lab_5.Singletons.ClientMessagesSingleton;
import org.lab5.lab_5.Utilities.MessageCoder;
import org.lab5.lab_5.Utilities.MessageParser;
import org.lab5.lab_5.Utilities.MessageTypes;
import org.lab5.lab_5.ViewControllers.ClientViewController;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientApplication extends Application {
    private class ReadMessagesFromServer implements Runnable{
        DataInputStream in = null;
        DataOutputStream out = null;
        Socket socket;

        ReadMessagesFromServer(Socket socket){
            this.socket = socket;
        }

        public void run(){
            try{
                in = new DataInputStream(
                        new BufferedInputStream(socket.getInputStream()));
                out = new DataOutputStream(socket.getOutputStream());

                while(true){
                    try{
                        String line = in.readUTF();
                        Pair<MessageTypes, String> parsedServerOutput = MessageParser.parseMessage(line);
                        switch (parsedServerOutput.getKey()) {
                            case ROOM_NAMES:
                                currentRoomNames = MessageParser.getListFromMessageWithSymbols(parsedServerOutput.getValue());
                                areRoomNamesLoaded = true;
                                clientViewController.updateRoomSelection(currentRoomNames);
                                break;
                            case CLIENT_NAMES:
                                currentClientNames = MessageParser.getListFromMessageWithSymbols(parsedServerOutput.getValue());
                                areClientNamesLoaded = true;
                                clientViewController.updateClientSelection(currentClientNames);
                                break;
                            case RECIEVE_MESSAGE:
                                ClientMessagesSingleton.getInstance().getMESSAGES().put(parsedServerOutput.getValue());
                                break;
                            case ERROR:
                                System.out.println("Kazkas blogai is kliento puses. hmmm");
                                break;
                        }
                    }catch (IOException | InterruptedException e){
                            e.printStackTrace();
                    }
                }
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        }
    }
    private Socket socket;
    private DataInputStream in;    // readinti ka atsiuncia klientui
    private DataOutputStream out;  // issiusti
    private ClientViewController clientViewController;
    private String name;
    protected List<String> currentClientNames;
    protected List<String> currentRoomNames;
    protected Boolean areClientNamesLoaded;
    protected Boolean areRoomNamesLoaded;

    public ClientApplication() throws IOException {
        socket = new Socket("0.0.0.0", 8886);
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(socket.getOutputStream());
        currentClientNames = new ArrayList<>();
        currentRoomNames = new ArrayList<>();
        areClientNamesLoaded = false;
        areRoomNamesLoaded = false;
    }

    @Override
    public void start(Stage stage) throws Exception {
        // UI
        loadFXML(stage);

        // listena MESSAGES_BY_CLIENT. jei zinute "disconnect" atsijungia, jei ne tai issiuncia serveriui
        Thread userInput = new Thread(() -> {
            try {
                String message = "";
                while (!message.equals("atsijungti")) {
                    try {
                        message = ClientMessagesSingleton.getInstance().getMESSAGES_BY_CLIENT().take();
                        out.writeUTF(message);

                    } catch (IOException | InterruptedException e) {
                        System.out.println(e);
                    }
                }

                Thread.sleep(2000);
                in.close();
                out.close();
                socket.close();
                System.exit(0);
            }
            catch (IOException | InterruptedException e){
                System.out.println("Error here " + e.getMessage());
            }
        });
        userInput.start();

        // is MESSAGES i chato UI
        Thread readMessagesToClient = new Thread(() -> {
            String message = "";
            while(true){
                try{
                    message = ClientMessagesSingleton.getInstance().getMESSAGES().take();
                    clientViewController.addMessageToChat(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        readMessagesToClient.start();

        // is servo i MESSAGES
        ReadMessagesFromServer server = new ReadMessagesFromServer(socket);
        new Thread(server).start();

        // veda varda tol kol atitiks salyga
        do {
            name = promptUsernameAlert();
        } while (name.isEmpty() || !isThisTheOnlyName(name));

        ClientMessagesSingleton.getInstance().getMESSAGES_BY_CLIENT().put(
                MessageCoder.codeMessage(MessageTypes.SEND_MESSAGE, name));
        clientViewController.setClientName(name);
    }
    private boolean isThisTheOnlyName(String name) throws IOException {
        out.writeUTF(MessageCoder.codeMessage(MessageTypes.GET_CLIENT_NAMES, ""));
        while (!areClientNamesLoaded) {}
        return !currentClientNames.contains(name);
    }
    private String promptUsernameAlert() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Įveskite savo vardą");
        dialog.setHeaderText(null);
        dialog.setContentText("Prašau įveskite savo vardą:");

        // Show the dialog and wait for the user's response
        Optional<String> result = dialog.showAndWait();
        // functional return type! opa
        return result.orElse("");
    }
    private void loadFXML(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("client-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 600);
        stage.setTitle("Client");
        stage.setScene(scene);
        stage.show();
        clientViewController = fxmlLoader.getController();
    }

    public static void main(String[] args) {
        launch();
    }
}

