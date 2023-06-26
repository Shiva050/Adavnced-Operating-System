package src.utils;
import src.application.data;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkUtils {

    public static Socket createClientSocket(String IPAddress, int portNumber) {
        try {
            Socket socket = new Socket(IPAddress, portNumber);
            System.out.println("Client socket created and connected to the server.");
            return socket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ServerSocket createServerSocket(int portNumber) {
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server socket created and listening on port " + portNumber);
            return serverSocket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Socket acceptClientSocket(ServerSocket serverSocket) {
        try {
            Socket clienSocket = serverSocket.accept();
            System.out.println("Client connected");
            return clienSocket;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ObjectInputStream createReader(Socket socket) {
        try {
            return new ObjectInputStream(socket.getInputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ObjectOutputStream createWriter(Socket socket) {
        try {
            return new ObjectOutputStream(socket.getOutputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendData(ObjectOutputStream writer, data data) {
        try {
            writer.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeSocket(Socket socket) {
        try {
            socket.close();
            System.out.println("Socket closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeServerSocket(ServerSocket serverSocket) {
        try {
            serverSocket.close();
            System.out.println("Server socket closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static data receiveData(ObjectInputStream reader) throws ClassNotFoundException {
        try {
            return (data) reader.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}