import java.net.*;
import java.io.*;

public class P1Sender {
    public static void main(String args[]) {

        // Initializing and declaring the required variables
        String relay_server_ip = args[0]; // relay server IP address
        int relay_server_port = Integer.parseInt(args[1]); // relay server port number
        Socket sender_socket = null;
        String user, user_ack = null;

        // Create output stream
        try {
            // Create the sender socket
            // Connet to the server
            sender_socket = new Socket(relay_server_ip, relay_server_port);

            // Send the success message
            System.out.println("Welcome to the Distributed Message Relay System");

            // take the username from the user
            BufferedReader in_from_user = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Enter your username: ");

            user = in_from_user.readLine();

            DataOutputStream out_to_server = new DataOutputStream(sender_socket.getOutputStream());
            out_to_server.writeUTF(user);
            DataInputStream in_from_server = new DataInputStream(sender_socket.getInputStream());

            user_ack = in_from_server.readUTF();
            System.out.println(user_ack);

        } catch (UnknownHostException e) {
            System.out.println("Sock:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        } finally {
            try {
                sender_socket.close();
            } catch (IOException e) {
                System.out.println("Close: " + e.getMessage());
            }
        }
    }
}