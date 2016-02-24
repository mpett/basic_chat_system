import java.io.*;
import java.net.Socket;

/**
 * Created by martinpettersson on 24/02/16.
 */
public class Client {
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private BufferedReader reader;

    public static void main(String args[]) {
        if (args.length != 2)
            System.out.println("Usage: java Client host port");
        else
            new Client(args[0], Integer.parseInt(args[1]));
    }

    public Client(String serverName, int portNumber) {
        try {
            socket = new Socket(serverName, portNumber);
            System.out.println("Connected");
            startClient();
            String receivedMessage = ""; String transferredMessage = "";

            while (!receivedMessage.equals("end")) {
                transferredMessage = reader.readLine();
                dataOut.writeUTF(transferredMessage);
                receivedMessage = dataIn.readUTF();
                System.out.println(receivedMessage);
            }
            dataIn.close();
            dataOut.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startClient() throws IOException {
        dataIn = new DataInputStream(socket.getInputStream());
        dataOut = new DataOutputStream(socket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(System.in));
    }
}