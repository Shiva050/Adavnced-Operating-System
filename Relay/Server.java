import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    
    private ServerSocket listenSocket;
    private int relayServerPort;
    private Socket senderSocket;

    public Server(int relayServerPort) {
        this.relayServerPort = relayServerPort;
        this.listenSocket = null;
    }

    public void createServerSocket() {
        try {
            listenSocket = new ServerSocket(relayServerPort);
            while(true) {
                senderSocket = listenSocket.accept();
                Connection c = new Connection(senderSocket);
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

    public Connection(Socket asenderSocket) {
        this.senderSocket = asenderSocket;
        this.dataFromSender = "";
        this.start();
    }

    public void run() {
        receiveData();
        sendAck();
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
        System.out.println("Relay ACK: Data received Sucessfully" + dataFromSender);
    }

    public void sendAck() {
        try {
            outToSender = new DataOutputStream(senderSocket.getOutputStream());
            outToSender.writeUTF("Data received successfully");
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
}
