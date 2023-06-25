import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    
    private ServerSocket listenSocket;
    private int relayServerPort;
    private Socket senderSocket;
    private Connection conn;


    public Server(int relayServerPort) {
        this.relayServerPort = relayServerPort;
        this.listenSocket = null;
        this.conn = null;
    }

    public void createServerSocket() {
        try {
            listenSocket = new ServerSocket(relayServerPort);
            while(true) {
                senderSocket = listenSocket.accept();
                conn = new Connection(senderSocket);
                System.out.println("Client Connection Successful");
            }
        }
        catch(IOException e) {
            System.out.println("Server Socket IO: " + e.getMessage());
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

    public Connection(Socket asenderSocket) {
        this.senderSocket = asenderSocket;
        this.dataFromSender = "";
        this.loggedinUser = null;
        this.start();
    }

    public void run() {
        receiveData();
        readUsersFile();
        verifyUser();
        receiveData();
        validateUser();
        closeSenderSocket();
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
            System.out.println("Data Received Failed - Server: "+ e.getMessage());
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

    // public String getdataFromSender() {
    //     return dataFromSender;
    // }
}
