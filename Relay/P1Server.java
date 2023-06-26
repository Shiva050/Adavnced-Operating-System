public class P1Server {
    public static void main(String args[]) {
        System.out.println("In Server Main");
        Server ser = new Server(Integer.parseInt(args[0]));
        ser.createServerSocket();
        // ser.connectReciever("Sapphire.uhcl.edu");
    }
}