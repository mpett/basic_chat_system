import java.io.*;
import java.net.Socket;

/**
 * Created by martinpettersson on 24/02/16.
 */
public class Client {
    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        try {
            Socket socket = new Socket("127.0.0.1",1337);
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String receivedMessage = ""; String transferredMessage = "";

            while (!receivedMessage.equals("end")) {
                transferredMessage = reader.readLine();
                dataOut.writeUTF(transferredMessage);
                receivedMessage = dataIn.readUTF();
                System.out.println(receivedMessage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
