package src.application;
import src.utils.NetworkUtils;
import java.io.BufferedReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class RelayServer {
    private ServerSocket relayServerSocket;
    private Socket relayClientSocket;
    private ObjectInputStream relayClientReader;
    private ObjectOutputStream relayClientWriter;

    private Socket receiverSocket;
    private ObjectInputStream receiverReader;
    private ObjectOutputStream receiverWriter;

    private Map<String, String> userPasswords;
    private String loggedInUser;

    public RelayServer(int relayPort, int receiverPort) {
        try {
            relayServerSocket = NetworkUtils.createServerSocket(relayPort);
            relayClientSocket = NetworkUtils.acceptClientSocket(relayServerSocket);
            relayClientReader = NetworkUtils.createReader(relayClientSocket);
            relayClientWriter = NetworkUtils.createWriter(relayClientSocket);

            receiverSocket = NetworkUtils.createClientSocket("localhost", receiverPort);
            receiverReader = NetworkUtils.createReader(receiverSocket);
            receiverWriter = NetworkUtils.createWriter(receiverSocket);

            this.loggedInUser = null;

            data data = new data("Relay acknowledgement", "Acknowledgement", "Relay acknowledgement".length());
            NetworkUtils.sendData(relayClientWriter, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startCommunication() {
        try {
            while (true) {
                data dataFromClient = NetworkUtils.receiveData(relayClientReader);

                System.out.println("Data received from client: " + dataFromClient.data);

                if (dataFromClient.type == "username") {
                    //Executing readUserFile method to read the users list to verify user
                    readUsersFile();
                    //verify user
                    verifyUser(dataFromClient.data);
                } else {
                    validateUser(dataFromClient.data);
                }
                
                // Send to Receiver
                NetworkUtils.sendData(receiverWriter, dataFromClient);
                data receiverAck = NetworkUtils.receiveData(receiverReader);
                System.out.println("Receiver acknowledgement received: " + receiverAck);
                NetworkUtils.sendData(relayClientWriter, receiverAck);
                if (dataFromClient.data.equalsIgnoreCase("con close")) {
                    break;
                }
            }
            NetworkUtils.closeSocket(receiverSocket);
            NetworkUtils.closeSocket(relayClientSocket);
            NetworkUtils.closeServerSocket(relayServerSocket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void readUsersFile() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src/static/userList.txt"));
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
        if(userPasswords.containsKey(userName)) {
            this.loggedInUser = userName;
            data userAck = new data("Valid User", "username", "Valid User".length());
            NetworkUtils.sendData(relayClientWriter, userAck);
            this.loggedInUser = userName;
        } else {
            data userAck = new data("InValid User", "username", "Valid User".length());
            NetworkUtils.sendData(relayClientWriter, userAck);
        }
    }

    public void validateUser(String password) {
        if(password.equals(userPasswords.get(this.loggedInUser))){
            data authAck = new data("User authentication Successfull", "password", "User authentication Successfull".length());
            NetworkUtils.sendData(relayClientWriter, authAck);
        } else {
            data authAck = new data("User authentication Failed", "password", "User authentication Failed".length());
            NetworkUtils.sendData(relayClientWriter, authAck);
        }
        
    }

}
