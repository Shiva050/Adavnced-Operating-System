public class P1Sender {
    public static void main(String args[]) {
        System.out.println("In Sender Main: ");
        Sender s1 = new Sender(args[0], Integer.parseInt(args[1]));
        // Create output stream
        s1.createSocket();
        s1.takeInput("User-name");
        s1.sendData(s1.getCurrentInput());
        s1.serverAck(); // Acknowledgement for Username verification
        s1.takeInput("Password");
        s1.sendData(s1.getCurrentInput());
        s1.serverAck(); // Acknowledgement for Password verification
        s1.closeSocket();
    }
}