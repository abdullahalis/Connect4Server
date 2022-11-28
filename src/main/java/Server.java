import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server {
    int count = 0;
    int portNumber;
    ClientThread p1Thread;
    Boolean p1Exists;
    ClientThread p2Thread;
    Boolean p2Exists;
    TheServer server;
    private Consumer<Serializable> callback;

    Server(Consumer<Serializable> call, int portNumber){
        this.portNumber = portNumber;
        callback = call;
        server = new TheServer();
        server.start();
        p1Exists = false;
        p2Exists = false;
    }

    public class TheServer extends Thread{

        public void run() {
            try(ServerSocket mySocket = new ServerSocket(portNumber);){
                callback.accept("Server socket launched");

                while(count < 2) {
                    ClientThread c = new ClientThread(mySocket.accept(), count);
                    callback.accept("client has connected to server");

                    if (!p1Exists) {
                        p1Thread = c;
                    }
                    else {
                        p2Thread = c;
                    }

                    c.start();
                    count++;
                }
            }//end of try
            catch(Exception e) {
                System.out.println("didn't work");
                callback.accept("Server socket did not launch");
            }
        }//end of run
    }

    class ClientThread extends Thread {
        Socket connection;
        int count;
        ObjectInputStream in;
        ObjectOutputStream out;

        ClientThread(Socket s, int count) {
            this.connection = s;
            this.count = count;
        }

//        public void updateClients(String message) {
//            for(int i = 0; i < clients.size(); i++) {
//                ClientThread t = clients.get(i);
//                try {
//                    t.out.writeObject(message);
//                }
//                catch(Exception e) {}
//            }
//        }

        public void run(){

            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            }
            catch(Exception e) {
                System.out.println("Streams not open");
            }



            while(true) {
                try {
                    String data = in.readObject().toString();
                    callback.accept("client: " + count + " sent: " + data);


                }
                catch(Exception e) {
                    callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
                    break;
                }
            }
        }//end of run


    }//end of client thread
}
