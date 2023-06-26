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
        s1.takeInput("Reciever-Name");
        s1.sendData(s1.getCurrentInput());
        s1.serverAck(); //Receiver name verification
        s1.serverAck(); //Receiver connection verification
        
        
        // s1.takeInput("messages, type -1 to exit");
        // int i = 1;
        // while(true) {
        //     s1.takeInput("message:"+ i +", type -1 to exit");
        //     String msg = s1.getCurrentInput();
        //     if (msg == "-1") {
        //         // DATA data = new DATA();
        //         // data.message = null;
        //         // data.total_length = -1;
        //         s1.sendSerializedData(null,-1);
        //         break;
        //     }
        //     else 
        //     {
        //         System.out.println("Server: Sending message " + i);
        //         // DATA data = new DATA();
        //         // data.message = s1.getCurrentInput();
        //         // data.total_length = s1.getCurrentInput().length();
        //         s1.sendSerializedData(s1.getCurrentInput(), s1.getCurrentInput().length());
        //         s1.serverAck();
        //     }
        //     i++;
        // }
        
        
        s1.takeInput("messages");
        System.out.println("Server: Sending message");
        s1.sendSerializedData(s1.getCurrentInput(), s1.getCurrentInput().length());
        System.out.println("Final Result from receiver via relay:");
        s1.serverAck();
        //s1.serverAck();
        // s1.serverAck();//Final display of total messages
        s1.closeSocket();
    }
}