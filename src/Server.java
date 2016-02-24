import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by martinpettersson on 24/02/16.
 */
public class Server {
    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        try {
            ServerSocket serverSocket = new ServerSocket(1337);
            Socket socket = serverSocket.accept();
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String receivedMessage = "";
            String transferredMessage = "";

            while (!receivedMessage.equals("end")) {
                receivedMessage = dataIn.readUTF();
                System.out.println(receivedMessage);
                transferredMessage = reader.readLine();
                dataOut.writeUTF(transferredMessage);
                dataOut.flush();
            }
            
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
