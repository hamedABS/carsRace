package main.java.ir.qut.server;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {

    private ServerSocket server;
    boolean running = true;

    public static void main(String[] args) {
        Server server = new Server();
        new Thread(server).start();
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(8080);
            List<Socket> sockets = new ArrayList<>();
            byte[] buf = new byte[256];
            DatagramSocket socket = new DatagramSocket(8086);
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            while (running) {
                socket.receive(packet);
                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                if (receivedMessage.equals("1")) {
                    getCreatePlayerService(sockets);
                } else if (receivedMessage.equals("2")) {
                    getViewGameService(sockets);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCreatePlayerService(List<Socket> sockets) throws IOException {
        System.out.println("Listening to socket in create service:");
        Socket newPlayerSocket = server.accept();
        new BlockGenerator(newPlayerSocket.getOutputStream()).start();
        Socket objectTransferSocket = server.accept();
        ReadToWrite readToWrite = new ReadToWrite(objectTransferSocket, sockets);
        sockets.add(objectTransferSocket);
        readToWrite.start();
        System.out.println("player connected");
    }

    private void getViewGameService(List<Socket> sockets) {
        try {
            System.out.println("Listening to socket in view service:");
            Socket objectTransferSocket = server.accept();
            sockets.add(objectTransferSocket);
            System.out.println("viewer connected.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
