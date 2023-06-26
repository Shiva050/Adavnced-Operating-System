import java.net.Socket;
import java.net.UnknownHostException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class DistributedSystemSocket {
	private String serverIP;
    private int serverPort;
    private String dataAck;
    private Socket socket;
    private DataOutputStream outStream;
    private DataInputStream inStream;

    
    public DistributedSystemSocket(String serverIP, int serverPort) {
        this.serverIP = serverIP; // relay server IP address
        this.serverPort = serverPort; // relay server port number
        this.socket = null;
        this.dataAck = null;
    }

    public void createSocket() {
        try {
            socket = new Socket(this.serverIP, this.serverPort);
            System.out.println("Socket Created...");   
        } 
        catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } 
        catch (IOException e) {
            System.out.println("Socket IO:" + e.getMessage());
        }
    }

    public void closeSocket() {
        try {
            socket.close();
        } 
        catch (IOException e) {
            System.out.println("Close: "+ e.getMessage());
        }
    }

    public void sendData(String data) {
        try {
            outStream = new DataOutputStream(socket.getOutputStream());
            outStream.writeUTF(data);
            receiveData(); //this is ack after giving any input
        }
        catch(IOException e) {
            System.out.println("Data Send Failed. "+ e.getMessage());
        }
    }

    public void receiveData() {
        try {
            inStream = new DataInputStream(socket.getInputStream());
            dataAck = inStream.readUTF();
        } 
        catch (Exception e) {
            System.out.println("Data Receive Failed. "+ e.getMessage());
        }
        System.out.println(dataAck);
    }
}