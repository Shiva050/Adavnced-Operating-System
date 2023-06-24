package Sender;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Sender {
    
    private String relay_server_ip;
    private int relay_server_port;
    private String dataAck;
    private Socket sender_socket;
    private DataOutputStream out_to_server;
    private DataInputStream in_from_server;
    private String currentInput;

    
    public Sender(String relay_server_ip, int relay_server_port) {
        this.relay_server_ip = relay_server_ip; // relay server IP address
        this.relay_server_port = relay_server_port; // relay server port number
        this.sender_socket = null;
        this.dataAck = null;
        this.currentInput = "";
    }

    public void createSocket() {
        try {
            sender_socket = new Socket(this.relay_server_ip, this.relay_server_port);
            System.out.println("Welcome to Distributed Message Relay system...");
            System.out.println("Sender Initiated...");   
        } 
        catch (UnknownHostException e) {
            System.out.println("Sender Socket:" + e.getMessage());
        } 
        catch (IOException e) {
            System.out.println("Sender Socket IO:" + e.getMessage());
        }
    }

    public void closeSocket() {
        try {
            sender_socket.close();
        } 
        catch (IOException e) {
            System.out.println("Sender Close: "+ e.getMessage());
        }
    }

    public String getCurrentInput() {
        return this.currentInput;
    }

    public void sendData(String data) {
        try {
            out_to_server = new DataOutputStream(sender_socket.getOutputStream());
            out_to_server.writeUTF(data);
        }
        catch(IOException e) {
            System.out.println("Data Send Failed - Sender. "+ e.getMessage());
        }
        System.out.println("Data Sent Successfully!");
    }

    public void receieve_data() {
        try {
            in_from_server = new DataInputStream(sender_socket.getInputStream());
            dataAck = in_from_server.readUTF();
        } 
        catch (Exception e) {
            System.out.println("Data Receive Failed - Sender. "+ e.getMessage());
        }
        System.out.println("Relay ACK: " + dataAck);
    }

    public void takeInput(String placeholder) {
        try {
            // take the input from the user
            BufferedReader in_from_user = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter your " + placeholder + " : ");
            this.currentInput = in_from_user.readLine();
        } 
        catch (IOException e) {
            System.out.println("Data Input Failed - Sender. "+ e.getMessage());
        }
    }
}