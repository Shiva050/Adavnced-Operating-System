
public class P1Receiver {
    public static void main(String args[]) {
        System.out.println("In Receiver Main");
        Receiver ser = new Receiver(Integer.parseInt(args[0]));
        ser.createRecieverSocket();
    }
}
