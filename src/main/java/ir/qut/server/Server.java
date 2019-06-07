package main.java.ir.qut.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {

    private ServerSocket server;

    public static void main(String[] args) {
        Server server = new Server();
        new Thread(server).start();
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(8080);
            List<Socket> sockets = new ArrayList<>();
            while (true) {
                System.out.println("Listening to socket");
                Socket newPlayer = server.accept();
                new BlockGenerator(newPlayer.getOutputStream()).start();

                Socket objectTransferSocket = server.accept();
                ReadToWrite readToWrite = new ReadToWrite(objectTransferSocket,sockets);
                sockets.add(objectTransferSocket);
                readToWrite.start();
                System.out.println("player connected");
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }


}
