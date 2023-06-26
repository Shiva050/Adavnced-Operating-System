package src.application;
import src.utils.NetworkUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Sender {
    
    private Socket relaySocket;
    private ObjectInputStream relayReader;
    private ObjectOutputStream relayWriter;

    public Sender(String serverAddress, int relayPort) {
        try {
            relaySocket = NetworkUtils.createClientSocket(serverAddress, relayPort);
            relayReader = NetworkUtils.createReader(relaySocket);
            relayWriter = NetworkUtils.createWriter(relaySocket);
            data ack = NetworkUtils.receiveData(relayReader);
            System.out.println("Relay acknowledgement received: " + ack.data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startCommunication(String type) {
        try {
            while (true) {              
                String input = takeInput(type);
                data data = new data(input, type, input.length());
                NetworkUtils.sendData(relayWriter, data);
                data relayAck = NetworkUtils.receiveData(relayReader);
                System.out.println("Relay acknowledgement received: " + relayAck.data);
                if (input.equalsIgnoreCase("Close")) {
                    break;
                }
            }
            NetworkUtils.closeSocket(relaySocket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String takeInput(String placeholder) {
        try {
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter your " + inFromUser + ": ");
            return inFromUser.readLine();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}