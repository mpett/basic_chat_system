import java.io.*;
import java.net.Socket;

/**
 * Created by martinpettersson on 24/02/16.
 */
public class Client implements Runnable{
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private BufferedReader reader;
    private ClientThread clientThread    = null;
    private Thread thread              = null;

    public static void main(String args[]) {
        if (args.length != 2)
            System.out.println("Usage: java Client host port");
        else
            new Client(args[0], Integer.parseInt(args[1]));
    }

    public Client(String serverName, int portNumber) {

        System.out.println("Establishing connection. Please wait ...");
            try {
            socket = new Socket(serverName, portNumber);
            System.out.println("Connected");
            startClient();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run()
    {  while (thread != null)
        {  try
            {
                String transferredMessage = reader.readLine();
                dataOut.writeUTF(transferredMessage);
                dataOut.flush();
            }
            catch(IOException ioe)
            {
        }
    }

    }

    public void handle(String msg)
    {  if (msg.equals(".bye"))
    {  System.out.println("Good bye. Press RETURN to exit ...");
    }
    else
        System.out.println(msg);
    }



    public void startClient() throws IOException {
        dataIn = new DataInputStream(socket.getInputStream());
        dataOut = new DataOutputStream(socket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(System.in));

        if (thread == null)
        {  clientThread = new ClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }

    class ClientThread extends Thread
    {  private Socket           socket   = null;
        private Client       client   = null;
        private DataInputStream  streamIn = null;

        public ClientThread(Client _client, Socket _socket)
        {  client   = _client;
            socket   = _socket;
            open();
            start();
        }
        public void open()
        {  try
        {  streamIn  = new DataInputStream(socket.getInputStream());
        }
        catch(IOException ioe)
        {  System.out.println("Error getting input stream: " + ioe);
        }
        }
        public void close()
        {  try
        {  if (streamIn != null) streamIn.close();
        }
        catch(IOException ioe)
        {  System.out.println("Error closing input stream: " + ioe);
        }
        }
        public void run()
        {  while (true)
        {  try
        {  client.handle(streamIn.readUTF());
        }
        catch(IOException ioe)
        {  System.out.println("Listening error: " + ioe.getMessage());
        }
        }
        }
    }
}