package org.lab5.lab_5;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.lab5.lab_5.Models.Client;
import org.lab5.lab_5.Models.PrivateRoom;
import org.lab5.lab_5.Models.Room;
import org.lab5.lab_5.Utilities.MessageCoder;
import org.lab5.lab_5.Utilities.MessageParser;
import org.lab5.lab_5.Utilities.MessageTypes;
import org.lab5.lab_5.ViewControllers.ServerViewController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class ServerApplication extends Application {
    private class ClientHandler implements Runnable{
        private DataInputStream in;
        private DataOutputStream out;
        public Socket socket;
        private Client client;

        public ClientHandler(Socket socket){
            this.socket = socket;

            try {
                this.in = new DataInputStream(socket.getInputStream());
                this.out = new DataOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                System.out.println("Error here " + e.getMessage());
            }
        }

        public void run() {
            try {
                //get username from client
                String userName;
                String unparsedInput;
                out.writeUTF(MessageCoder.codeMessage(MessageTypes.RECIEVE_MESSAGE, "Prisijungėte prie serverio"));

                while (true) {
                    unparsedInput = in.readUTF();
                    Pair<MessageTypes, String> parsedMessage = MessageParser.parseMessage(unparsedInput);

                    if (parsedMessage.getKey() == MessageTypes.GET_CLIENT_NAMES) {
                        this.out.writeUTF(MessageCoder.codeMessage(MessageTypes.CLIENT_NAMES,
                                MessageCoder.getMessageWithSymbolsFromList(clientList.stream().map(Client::getName).collect(Collectors.toList()))
                        ));
                        continue;
                    } else if (parsedMessage.getKey() == MessageTypes.GET_ROOM_NAMES) {
                        this.out.writeUTF(MessageCoder.codeMessage(MessageTypes.ROOM_NAMES,
                                MessageCoder.getMessageWithSymbolsFromList(roomList.stream().map(Room::getName).collect(Collectors.toList()))
                        ));
                        continue;
                    }

                    userName = parsedMessage.getValue();
                    client = new Client(userName);
                    client.setRoom(defaultRoom);
                    clientList.add(client);
                    break;
                }
                updateAllClientRoomAndClientList();
                out.writeUTF(MessageCoder.codeMessage(MessageTypes.RECIEVE_MESSAGE, "Vardas tinkamas. Sveiki " + userName));

                //welcome message to server
                String message = "Prisijungė vartotojas vardu: " + client.getName();
                serverViewController.addMessageToServerLog(message);

                // dabar acceptinsim visas zinutes ir desim i MESSAGES ta type
                String line = "";
                while (!line.equals("bye")) {

                    try {
                        line = this.in.readUTF();
                        Pair<MessageTypes, String> parsedClientInput = MessageParser.parseMessage(line);

                        switch (parsedClientInput.getKey()) {
                            case GET_ROOM_NAMES:
                                this.out.writeUTF(MessageCoder.codeMessage(MessageTypes.ROOM_NAMES,
                                        MessageCoder.getMessageWithSymbolsFromList(roomList.stream().map(Room::getName).collect(Collectors.toList()))
                                ));
                                break;
                            case GET_CLIENT_NAMES:
                                this.out.writeUTF(MessageCoder.codeMessage(MessageTypes.CLIENT_NAMES,
                                        MessageCoder.getMessageWithSymbolsFromList(clientList.stream().map(Client::getName).collect(Collectors.toList()))
                                ));
                                break;
                            case SEND_MESSAGE:
                                Pair<String, String> furtherParsedMessage = MessageParser.parseMessageFurther(parsedClientInput.getValue());

                                String line_formatted = furtherParsedMessage.getKey() + ": " + client.getName() + ": " + furtherParsedMessage.getValue();
                                MESSAGES.put(line_formatted);
                                //print to server
                                serverViewController.addMessageToServerLog(line_formatted);
                                break;
                            case GET_ROOM_MESSAGES:
                                assignClientToRoom(MessageParser.parseMessageFurther(parsedClientInput.getValue()).getKey());
                                for (String roomMessage : client.getRoom().getMessages())
                                    this.out.writeUTF(MessageCoder.codeMessage(MessageTypes.RECIEVE_MESSAGE, roomMessage));
                                break;
                            case ADD_ROOM:
                                if (!checkIfRoomAlreadyExists(parsedClientInput.getValue())) {
                                    roomList.add(new Room(parsedClientInput.getValue()));
                                    updateAllClientRoomAndClientList();
                                    serverViewController.addMessageToServerLog("Sukurtas kambarys pavadinimu \"" + parsedClientInput.getValue() + "\"");
                                }
                                break;
                            case GET_PRIVATE_ROOM:
                                String anotherPersonName = MessageParser.parseMessageFurther(parsedClientInput.getValue()).getKey();
                                client.setRoom(null);
                                if (checkIfPrivateRoomAlreadyExists(userName, anotherPersonName) &&
                                        checkIfPrivateRoomAlreadyExists(anotherPersonName, userName)) {
                                    privateRoomList.add(new PrivateRoom(userName, anotherPersonName));
                                    this.out.writeUTF(MessageCoder.codeMessage(MessageTypes.RECIEVE_MESSAGE, "privatus kambarys"));
                                }
                                else {
                                    PrivateRoom privateRoom = findPrivateRoom(userName, anotherPersonName);
                                    for (String privateMessage : privateRoom.getMessages())
                                        this.out.writeUTF(MessageCoder.codeMessage(MessageTypes.RECIEVE_MESSAGE, privateMessage));
                                }

                                break;
                            case SEND_PRIVATE_MESSAGE:
                                furtherParsedMessage = MessageParser.parseMessageFurther(parsedClientInput.getValue());

                                line_formatted = "PRIVATE MESSAGE: (" + furtherParsedMessage.getKey() + ", " + client.getName() + "): " + furtherParsedMessage.getValue();
                                findPrivateRoom(userName, furtherParsedMessage.getKey()).getMessages().add(client.getName() + ": " + furtherParsedMessage.getValue());
                                sendPrivateMessage(userName, furtherParsedMessage.getKey(), client.getName() + ": " + furtherParsedMessage.getValue());
                                //print to server
                                serverViewController.addMessageToServerLog(line_formatted);
                                break;
                            case ERROR:
                                System.out.println("something is very very wrong boi. nepasiduok");
                                break;
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        System.out.println(client.getName());
                    }
                }

                // user disconnected
                try {
                    String goodbyeMessage = "Server: Goodbye " + client.getName() + ", see ya soon!";
                    MESSAGES.put(goodbyeMessage);
                    serverViewController.addMessageToServerLog(goodbyeMessage);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // galbut turesim list klientu is servo puses vizualiai
                //removeClientToUI(client.getName());
                clientList.remove(client);
                this.in.close();
                this.out.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Error :" + e.getMessage());
            }
        }

        private void assignClientToRoom(String roomName) {
            for (Room room : roomList) {
                if (Objects.equals(room.getName(), roomName)) {
                    client.setRoom(room);
                    selectedRoom = roomName;
                }
            }
        }

        private boolean checkIfRoomAlreadyExists(String roomName) {
            for (Room room : roomList) {
                if (Objects.equals(room.getName(), roomName))
                    return true;
            }
            return false;
        }

        private PrivateRoom findPrivateRoom(String userName, String anotherPersonName) {
            for (PrivateRoom privateRoom : privateRoomList) {
                if ((Objects.equals(privateRoom.getClient1(), userName) && Objects.equals(privateRoom.getClient2(), anotherPersonName)) ||
                        (Objects.equals(privateRoom.getClient2(), userName) && Objects.equals(privateRoom.getClient1(), anotherPersonName)))
                    return privateRoom;
            }
            // cia nenueisim
            return privateRoomList.get(0);
        }

        private boolean checkIfPrivateRoomAlreadyExists(String userName, String anotherPersonName) {
            for (PrivateRoom privateRoom : privateRoomList) {
                if (Objects.equals(privateRoom.getClient1(), userName) &&
                Objects.equals(privateRoom.getClient2(), anotherPersonName))
                    return false;
            }
            return true;
        }

        public void write(String message){
            try{
                this.out.writeUTF(message);
            }catch (IOException e){
                System.out.println("Error here " + e.getMessage());
            }
        }

        public Client getClient() {
            return client;
        }
    }
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ServerViewController serverViewController;
    private List<Client> clientList;
    private List<ClientHandler> clientHandlerList;
    private List<Room> roomList;
    private List<PrivateRoom> privateRoomList;
    public String selectedRoom;
    public Room defaultRoom;
    private final LinkedBlockingQueue<String> MESSAGES;

    public ServerApplication() throws IOException {
        clientList = new ArrayList<>();
        roomList = new ArrayList<>();
        privateRoomList = new ArrayList<>();
        clientHandlerList = new ArrayList<>();
        serverSocket = new ServerSocket(8886);
        MESSAGES = new LinkedBlockingQueue<>();
        defaultRoom = new Room("-");
    }
    @Override
    public void start(Stage stage) throws IOException {
        // load UI
        loadFXML(stage);

        Thread acceptClients = new Thread(() -> {
            try {
                while (true) {
                    clientSocket = serverSocket.accept();

                    //handle multithreading for clients
                    ClientHandler client = new ClientHandler(clientSocket);
                    clientHandlerList.add(client);
                    new Thread(client).start();
                }

            } catch (Exception e) {
                System.out.println("Error here " + e.getMessage());
            }
        });
        acceptClients.start();

        Thread writeMessages = new Thread(() -> {
            while (true) {
                try {
                    String message = MESSAGES.take();

                    for (ClientHandler clientHandler : clientHandlerList) {
                        Client client = clientHandler.getClient();
                        // getRoomName()
                        if (Objects.equals(client.getRoom().getName(), selectedRoom)) {
                            clientHandler.write(MessageCoder.codeMessage(MessageTypes.RECIEVE_MESSAGE, message));
                            // prideti prie kambario ta zinute
                            for (Room room : roomList) {
                                if (Objects.equals(room.getName(), selectedRoom)) {
                                    roomList.get(roomList.indexOf(room)).getMessages().add(message);
                                    break;
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Error is it here " + e.getMessage());
                }
            }
        });
        writeMessages.start();
    }
    public void updateAllClientRoomAndClientList() {
        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.write(
                    MessageCoder.codeMessage(MessageTypes.ROOM_NAMES,
                            MessageCoder.getMessageWithSymbolsFromList(roomList.stream().map(Room::getName).collect(Collectors.toList()))
                    ));
            clientHandler.write(
                    MessageCoder.codeMessage(MessageTypes.CLIENT_NAMES,
                            MessageCoder.getMessageWithSymbolsFromList(clientList.stream().map(Client::getName).collect(Collectors.toList()))
                    ));
        }
    }
    protected void sendPrivateMessage(String userName, String anotherUserName, String message) {
        for (ClientHandler clientHandler : clientHandlerList) {
            if (Objects.equals(clientHandler.getClient().getName(), userName) || Objects.equals(clientHandler.getClient().getName(), anotherUserName)) {
                if (clientHandler.getClient().getRoom() == null)
                    clientHandler.write(MessageCoder.codeMessage(MessageTypes.RECIEVE_MESSAGE, message));
            }
        }
    }
    private void loadFXML(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("server-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 600);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();
        serverViewController = fxmlLoader.getController();
    }

    public static void main(String[] args) {
        launch();
    }
}