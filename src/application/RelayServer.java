package src.application;
import src.utils.NetworkOperations;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelayServer {
    private NetworkOperations networkOperations;
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private String loggedInUser;
    private Map<String, String> userPasswords = new HashMap<>(); 
    private Map<String, String> receiverIPMapping = new HashMap<>(); 

    public RelayServer(NetworkOperations networkOperations) {
        this.networkOperations = networkOperations;
        this.loggedInUser = null;
    }

    // public RelayServer(Socket clientSocket) {
    //     this.networkOperations = new NetworkOperations(clientSocket);
    // }

    public void startRelayCommunication(int port) {

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Relay server started. Waiting for clients...");

            clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket);

            // networkOperations.connect(receiverAddress, receiverPort);
            // System.out.println("Connected to receiver server.");

            // Send acknowledgement to client
            networkOperations.sendObject(new DataObject("Connection established with the relay server.", "Connection ACK"), clientSocket);

            System.out.println("Send");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveData() {
        try {
            while (true) {
                // Receive data from client
                Object dataObject = networkOperations.receiveObject(clientSocket);
                if (dataObject instanceof DataObject) {
                    DataObject data = (DataObject) dataObject;
                    System.out.println(data);
                    String message = data.getData();
                    String type = data.getType();

                    if ("close".equalsIgnoreCase(message))
                        break;

                    System.out.println("Received data from client: " + message);
                    processData(message, type);
                    //networkOperations.sendObject(new DataObject("Data sent successfully."), clientSocket);

                    



                    // Send data to receiver server
                    // networkOperations.sendObject(new DataObject(message));

                    // Receive acknowledgement from receiver server
                    // Object ackObject = networkOperations.receiveObject();
                    // if (ackObject instanceof DataObject) {
                    //     DataObject ackData = (DataObject) ackObject;
                    //     System.out.println("Received acknowledgement from receiver server: " + ackData.getData());

                    //     // Send acknowledgement back to client
                    //     networkOperations.sendObject(new DataObject(ackData.getData()));
                    // }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void processData(String data, String type) {
        if (type.equals("username")) {
            readUsersFile();
            verifyUser(data);
        } else if (type.equals("password")) {
            validateUser(data);
        } else if (type.equals("receiverServer")) {
            readReceiverFile();
            verifyReceiver(data);
        }
    }

    public void readUsersFile() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("../src/static/userList.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(" ");
                if (columns.length == 3) {
                    userPasswords.put(columns[0], columns[1]);
                } else {
                    System.out.println("Invaild line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.print("Relay File Reader: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch(IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public void verifyUser(String userName) {
        try {
            if(userPasswords.containsKey(userName)) {
                System.out.println("Valid");
                this.loggedInUser = userName;
                networkOperations.sendObject(new DataObject("Valid Username.", "Username"), clientSocket);
            } else {
                networkOperations.sendObject(new DataObject("InValid Username.", "Username"), clientSocket);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void validateUser(String data) {
        try {
            if(data.equals(userPasswords.get(this.loggedInUser))){
                networkOperations.sendObject(new DataObject("Authenticated.", "Password"), clientSocket);
            } else {
                System.out.println("Not Authenticated");
                networkOperations.sendObject(new DataObject("Not Authenticated.", "Password"), clientSocket);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }   
    }

    public void readReceiverFile() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("../src/static/receiverList.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(" ");
                if (columns.length == 3) {
                    receiverIPMapping.put(columns[0], columns[1]);
                } else {
                    System.out.println("Invaild line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.print("Relay File Reader: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch(IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public void verifyReceiver(String address) {
        if(receiverIPMapping.containsKey(address)){
            System.out.println("Receiver Found");
            System.out.println(receiverIPMapping.get(address));
            
        } else {
            System.out.println("Reciever Not found! " + address + " , act:" + receiverIPMapping.get(address));
        }
    }
}
