import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server {
    private int count;
    private int portNumber;
    private ClientThread p1Thread;
    private Boolean p1Turn;
    private ClientThread p2Thread;
    private Boolean p2Turn;
    private TheServer server;
    private Consumer<Serializable> callback;
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    CFourInfo info;


    Server(Consumer<Serializable> call, int portNumber) {
        this.portNumber = portNumber;
        callback = call;
        server = new TheServer();
        server.start();
        count = 1;
        info = new CFourInfo();
    }

    // object telling client to wait for other players turn
    public CFourInfo waitInfo() {
        CFourInfo info = new CFourInfo();
        info.twoPlayers = true;
        info.turn = false;
        info.moveRow = 10;
        return info;
    }

    public class TheServer extends Thread {
        // create a server
        public void run() {
            try(ServerSocket mySocket = new ServerSocket(portNumber);){
                callback.accept("Server socket launched");

                while(true) {
                    ClientThread c = new ClientThread(mySocket.accept(), count);
                    callback.accept("client #" + count + " has connected to server");
                    clients.add(c);

                    if (clients.size() == 1) {
                        callback.accept("Waiting for second player");
                        p1Thread = c;
                        info.twoPlayers = false;
                    }
                    else if (clients.size() == 2) {
                        callback.accept("Both players are here! Game will now begin");
                        info.twoPlayers = true;
                        p2Thread = c;
                        p1Turn = true;
                        p2Turn = false;
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


        // handle new clients joining
        public void run(){

            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            }
            catch(Exception e) {
                System.out.println("Streams not open");
            }

            // if there's only one client send a message to wait
            if (count == 1) {
                System.out.println("one player");
                CFourInfo wait = new CFourInfo();
                wait.twoPlayers = false;
                wait.playerNum = 1;
                wait.turn = false;
                wait.gameStarted = false;
                try {
                    p1Thread.out.writeObject(wait);
                }
                catch (Exception e) {
                    System.out.println("couldn't write");
                    e.printStackTrace();
                }
            }

            // if two players have joined
            if (count == 2) {
                CFourInfo begin1 = new CFourInfo();
                begin1.twoPlayers = true;
                begin1.playerNum = 1;
                begin1.turn = true;
                begin1.gameStarted = false;

                CFourInfo begin2 = new CFourInfo();
                begin2.twoPlayers = true;
                begin2.playerNum = 1;
                begin2.turn = false;
                begin2.gameStarted = false;
                try {
                    p1Thread.out.writeObject(begin1);
                    p2Thread.out.writeObject(begin2);
                }
                catch (Exception e) {
                    System.out.println("couldn't write");
                }
            }


            // handle objects that get sent back
            while(true) {
                try {
                    CFourInfo cInfo = (CFourInfo) in.readObject();
                    // if move came from player 1
                    if (cInfo.playerNum == 1) {
                        callback.accept("Player 1 made a move: row " + cInfo.moveRow + ", col " + cInfo.moveCol);

                        // send p1 move to p2
                        CFourInfo p1Move = new CFourInfo(true, 2, true, false, cInfo.moveRow, cInfo.moveCol, true);
                        p2Thread.out.writeObject(p1Move);

                        // tell p1 to wait
                        p1Thread.out.writeObject(waitInfo());

                    }
                    // if move came from player 2
                    else {
                        callback.accept("Player 2 made a move: row " + cInfo.moveRow + ", col " + cInfo.moveCol);
                        // send p2 move to p1
                        CFourInfo p2Move = new CFourInfo(true, 1, true, false, cInfo.moveRow, cInfo.moveCol, true);
                        p1Thread.out.writeObject(p2Move);

                        // tell p2 to wait
                        p2Thread.out.writeObject(waitInfo());
                        }

                    // if a player won
                    if (cInfo.won) {
                        CFourInfo youWon = new CFourInfo();
                        youWon.gameOver = true;
                        youWon.won = true;
                        youWon.twoPlayers = true;
                        CFourInfo youLose = new CFourInfo();
                        youLose.gameOver = true;
                        youLose.won = false;
                        youWon.twoPlayers = true;

                        if (cInfo.playerNum == 1) {
                            callback.accept("Player 1 won!");
                            p1Thread.out.writeObject(youWon);
                            p2Thread.out.writeObject(youLose);
                        }
                        else {
                            callback.accept("Player 2 won!");
                            p1Thread.out.writeObject(youLose);
                            p2Thread.out.writeObject(youWon);
                        }
                    }
                }

                catch(Exception e) {
                    try {
                        p1Thread.out.writeObject("Other player left");
                        clients.remove(p1Thread);
                        callback.accept("Player 2 has left the game");
                    }
                    catch (Exception d) {
                        try {
                            p2Thread.out.writeObject("Other player left");
                            clients.remove(p2Thread);
                            callback.accept("Player 1 has left the game");
                        }
                        catch(Exception f) {}
                    }
                    //callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
                    count--;
                    break;
                }
            }
        }//end of run

    }//end of client thread

    public int getCount() {
        return count;
    }
}
