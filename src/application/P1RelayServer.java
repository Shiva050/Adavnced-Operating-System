package src.application;

public class P1RelayServer {
    public static void main(String[] args) {

        System.out.print("In Relay Server....");
        RelayServer relay = new RelayServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));

        relay.startCommunication(); //for username
        relay.startCommunication(); //for password
    }
}
