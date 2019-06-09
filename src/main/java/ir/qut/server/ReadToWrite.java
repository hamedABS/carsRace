package main.java.ir.qut.server;

import main.java.ir.qut.client.PlayerPanel;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ReadToWrite extends Thread {

    private List<Socket> players;
    private Socket socket;

    public ReadToWrite(Socket socket, List<Socket> players) throws IOException {
        this.players = players;
        this.socket = socket;
        this.objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    ObjectInputStream objectInputStream;

    @Override
    public void run() {
        while (true) {
            try {
                PlayerPanel panel = (PlayerPanel) objectInputStream.readObject();

                ObjectOutputStream outputStream;
                for (Socket objectTransferSocket : players) {
                    if (!socket.equals(objectTransferSocket)) {
                        outputStream = new ObjectOutputStream(objectTransferSocket.getOutputStream());
                        outputStream.writeObject(panel);
                        outputStream.flush();
                    }
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }
}
