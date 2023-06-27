package src.application;
import src.utils.NetworkOperations;

public class P1RelayServer {
    public static void main(String[] args) {
        NetworkOperations networkOperations = new NetworkOperations();
        RelayServer relayServer = new RelayServer(networkOperations);
        // ReceiverServer receiverServer = new ReceiverServer(networkOperations);

        int relayPort = Integer.parseInt(args[0]); 

        Thread relayServerThread = new Thread(
            () -> {
                relayServer.startRelayCommunication(relayPort);
                relayServer.receiveData();
                //relayServer.receiveData();//for username
            }
        );

        // Thread receiverServerThread = new Thread(() -> receiverServer.start(5678));

        relayServerThread.start();
        // receiverServerThread.start();\
    }
}
