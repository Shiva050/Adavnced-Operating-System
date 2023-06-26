import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.directory.SearchResult;

public class Receiver {
    private String relay_server_ip;
    private int relay_server_port;
    private String dataAck;
    private Socket sender_socket;
    private DataOutputStream out_to_server;
    private DataInputStream in_from_server;
    private String currentInput;

    
    private ServerSocket listenSocket;
    private int receiverPort;
    private Socket senderSocket;
    private Connection conn;


    public Receiver(int receiverPort) {
        this.receiverPort = receiverPort;
        this.listenSocket = null;
        this.conn = null;
    }

    public void createRecieverSocket() {
        try {
            listenSocket = new ServerSocket(receiverPort);
            while(true) {
                senderSocket = listenSocket.accept();
                conn = new Connection(senderSocket);
                System.out.println("Relay Connection Successful");
            }
        }
        catch(IOException e) {
            System.out.println("Receiver Socket IO: " + e.getMessage());
        }
    }

    public void closeSocket() {
        try {
            senderSocket.close();
        } 
        catch (IOException e) {
            System.out.println("Sender Close: "+ e.getMessage());
        }
    }
}

class Connection extends Thread {

    
    private DataInputStream inFromSender;
    private DataOutputStream outToSender;
    private String dataFromSender;
    private Socket senderSocket;
    private String loggedinUser;
    private Map<String, String> userPasswords = new HashMap<>(); 
    private String searchResult = "";

    public Connection(Socket asenderSocket) {
        this.senderSocket = asenderSocket;
        this.dataFromSender = "";
        this.loggedinUser = null;
        this.start();
    }

    public void run() {
        System.out.println("Receiver Thread Started...");
        sendAck("Receiver: Connection Successful");
        receiveData();
        // sendAck("Receiver: Got the usermsgs");
        System.out.println("I got this data from Sender via Relay:\n"+dataFromSender);
        System.out.println("Applying search Alogrithm...");
        searchSubString();
        System.out.println("Search Result:\nLongest common Substring is: " + searchResult + "\nLength of common substring: " + searchResult.length());
        sendAck(searchResult);
        // receiveData();
        // readUsersFile();
        // verifyUser();
        // receiveData();
        // validateUser();
        //closeSenderSocket();
    }

    public void receiveData() {
        try {
            inFromSender = new DataInputStream(senderSocket.getInputStream());
            dataFromSender = inFromSender.readUTF();
        }
        catch(IOException e) {
            System.out.println("Data Received Failed - Server: "+ e.getMessage());
        }
        sendAck("Relay ACK: Data received Sucessfully");
    }

    public void sendAck(String ackvalue) {
        try {
            outToSender = new DataOutputStream(senderSocket.getOutputStream());
            outToSender.writeUTF(ackvalue);
        }
        catch(IOException e) {
            System.out.println("Data Send Failed - Server: "+ e.getMessage());
        }
    }

    public void closeSenderSocket() {
        try {
            senderSocket.close();
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void readUsersFile() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("./userList.txt"));
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

    public void verifyUser() {
        if(userPasswords.containsKey(dataFromSender)) {
            sendAck("Valid User");
            loggedinUser = dataFromSender;
        } else {
            sendAck("Invalid user");
        }
    }

    public void validateUser() {
        if(dataFromSender.equals(userPasswords.get(loggedinUser))){
            sendAck("User authentication Successfull");
        } else {
            sendAck("User authentication Failed");
        }
        
    }

    public void searchSubString() {
        String str = dataFromSender;
        int n = str.length();

        int[][] dp = new int[n + 1][n + 1];
        int maxLength = 0;
        int endIndex = 0;

        for (int i = 1; i <= n; i++) {
            for (int j = i + 1; j <= n; j++) {
                if (str.charAt(j - 1) == str.charAt(i - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    if (dp[i][j] > maxLength) {
                        maxLength = dp[i][j];
                        endIndex = j - 1;
                    }
                } else {
                    dp[i][j] = 0;
                }
            }
        }

        if (maxLength == 0) {
            searchResult =  "";  // No common substring found
        }

        searchResult = str.substring(endIndex - maxLength + 1, endIndex + 1);
    }

    // public String getdataFromSender() {
    //     return dataFromSender;
    // }
}