import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private DataInputStream inFromReceiver;
    private DataOutputStream outToSender;
    private String dataFromSender;
    private String dataFromReceiver;
    private Socket senderSocket;
    private Socket receiverSocket;
    private String loggedinUser;
    private Map<String, String> userPasswords = new HashMap<>(); 
    private Map<String, List<String>> receiversList = new HashMap<>(); 
    // private List<String> userMsgs = new ArrayList<>();
    String userMsgs = "";
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
        receiveData();
        readReceiverFile();
        verifyReceiver();
        receiveSerializedData();
        sendAckReceiver(userMsgs);
        receiverData();
        // receiverData();//looks like here we are getting result
        System.out.println("\nResult from reciever: "+ dataFromReceiver);
        sendAck(dataFromReceiver);
                
        // closeReceiverSocket();
        // closeSenderSocket();
    }

    public void receiveSerializedData() {
        try {
            // while(true) {
            //     //Deserializing
            //     inFromSender = new DataInputStream(senderSocket.getInputStream());
            //     int total_length = inFromSender.readInt();
            //     String message = inFromSender.readUTF();
            //     if (total_length == -1 && message.equals(null)) {
            //         // Exit the loop if the termination condition is met
            //         sendAck("Relay ACK: Total:"+i+" messages received Sucessfully");
            //         break;
            //     }
            //     userMsgs += message;
            //     userMsgs += "\n";
                
            //     sendAck("Relay ACK: Message:"+ i +" received Sucessfully");
            //     i++;
            // }

            //Deserializing
            inFromSender = new DataInputStream(senderSocket.getInputStream());
            int total_length = inFromSender.readInt();
            String message = inFromSender.readUTF();
            userMsgs += message;
            userMsgs += "\n";
            sendAck("Relay ACK: Message of length "+ total_length +" bytes received Sucessfully");
        }
        catch(IOException e) {
            System.out.println("Data Received Failed - Server: "+ e.getMessage());
        }
        System.out.println("Concatenated Sender Message:\n"+userMsgs);
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

    public void receiverData() {
        try {
            inFromReceiver = new DataInputStream(receiverSocket.getInputStream());
            dataFromReceiver = inFromReceiver.readUTF();
        }
        catch(IOException e) {
            System.out.println("Data Received Failed - Server: "+ e.getMessage());
        }
        // sendAckReceiver("Relay ACK: Data received Sucessfully");
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

    public void sendAckReceiver(String ackvalue) {
        try {
            outToSender = new DataOutputStream(receiverSocket.getOutputStream());
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

    public void closeReceiverSocket() {
        try {
            System.out.println("Relay closing Receiver socket");
            receiverSocket.close();
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

    public void verifyReceiver() {
        if(receiversList.containsKey(dataFromSender)){
            sendAck("Receiver Found");
            List<String> addressAndPort = receiversList.get(dataFromSender);
            String recAck = connectReciever(addressAndPort.get(0),addressAndPort.get(1));
            sendAck(recAck);
        } else {
            sendAck("Reciever Not found! " + dataFromSender + " , act:" + receiversList.get(dataFromSender));
        }
        
    }

    public void readReceiverFile() {
        BufferedReader reader = null;
        try {
            
            // Put the name and address/port list in the map
            //map.put(name, addressAndPort);
            reader = new BufferedReader(new FileReader("./receiverList.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(" ");
                if (columns.length == 3) {
                    List<String> addressAndPort = new ArrayList<>();
                    addressAndPort.add(columns[1]);
                    addressAndPort.add(columns[2]);
                    receiversList.put(columns[0], addressAndPort);
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

    public String connectReciever(String ipAddress, String port) {
        try {
            // receiverSocket = new Socket(ipAddress, Integer.parseInt(port));
            receiverSocket = new Socket("localhost", Integer.parseInt(port));//test purpose
            System.out.println("Receiver Initiated...");   
            receiverData(); 
            System.out.println(dataFromReceiver);
            return "Reciever Connected Successfully";
        } 
        catch (UnknownHostException e) {
            System.out.println("Receiver Socket:" + e.getMessage());
        } 
        catch (IOException e) {
            System.out.println("Receiver Socket IO:" + e.getMessage());
        }
        
        return "Receiver Connection Failed!";
    }
}
