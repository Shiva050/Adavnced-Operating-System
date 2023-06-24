package Relay;
import java.net.*;
import java.io.*;

public class P1Server {
    public static void main(String args[]) {

        try {
            // relay server port number
            int relay_server_port = Integer.parseInt(args[0]);

            ServerSocket listenSocket = new ServerSocket(relay_server_port);

            while (true) {
                Socket sendersocket = listenSocket.accept();
                Connection c = new Connection(sendersocket);
                // System.out.println(c.getUser());
                // listenSocket.close();
            }

        } catch (IOException e) {
            System.out.println("Listen: " + e.getMessage());
        }
    }
}

class Connection extends Thread {
    DataInputStream in_from_sender;
    String user_from_client;
    DataOutputStream out_to_sender;
    Socket senderSocket;

    public Connection(Socket asenderSocket) {
        try {
            senderSocket = asenderSocket;
            in_from_sender = new DataInputStream(senderSocket.getInputStream());
            out_to_sender = new DataOutputStream(senderSocket.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection: " + e.getMessage());
        }
    }

    public void run() {
        try {
            user_from_client = in_from_sender.readUTF();
            System.out.println(user_from_client);
            out_to_sender.writeUTF("User Acknowledged");
        } catch (EOFException e) {
            System.out.println("EOF: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            try {
                System.out.println("Closing the socket");
                senderSocket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public String getUser() {
        return user_from_client;
    }
}