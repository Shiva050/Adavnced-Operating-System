package src.application;

public class P1Client {
    public static void main(String[] args) {
        System.out.println("In Client Main...");
        Sender clnt = new Sender(args[0], Integer.parseInt(args[1]));

        //start the client relay communincation for username
        clnt.takeInput("username");
        clnt.startCommunication("username"); 

        //start the client relay communincation for username
        clnt.takeInput("password");
        clnt.startCommunication("password"); 
    }
}