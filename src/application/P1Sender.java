package src.application;
import src.utils.NetworkOperations;

public class P1Sender {
    public static void main(String[] args) {
        NetworkOperations networkOperations = new NetworkOperations();
        Client client = new Client(networkOperations);
        //RelayServer relayServer = new RelayServer(networkOperations);
        // ReceiverServer receiverServer = new ReceiverServer(networkOperations);

        String relayAddress = args[0];
        int relayPort = Integer.parseInt(args[1]); 


        Thread clientThread = new Thread(
            () -> {
                client.startClientCommunication(relayAddress, relayPort);
                try {
                    client.takeInput("username");
                    client.takeInput("password");
                    client.takeInput("receiverServer");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        );

        // Thread relayServerThread = new Thread(
        //     () -> {
        //         relayServer.startRelayCommunication(relayPort);
        //         relayServer.receiveData();
        //         //relayServer.receiveData();//for username
        //     }
        // );

        // Thread receiverServerThread = new Thread(() -> receiverServer.start(5678));

        clientThread.start();
        // relayServerThread.start();
        // receiverServerThread.start();\
    }
}
