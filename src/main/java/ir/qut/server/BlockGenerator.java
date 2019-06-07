package main.java.ir.qut.server;

import main.java.ir.qut.client.PlayerPanel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class BlockGenerator extends Thread {
    private DataOutputStream dataOutputStream;
    private Random random;

    BlockGenerator(OutputStream dataOutputStream) {
        random = new Random();
        this.dataOutputStream = new DataOutputStream(dataOutputStream);
    }

    @Override
    public void run() {
        long speed = 5000;
        while (true) {
            try {
                dataOutputStream.writeBoolean(random.nextBoolean());
                Thread.sleep(speed);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                PlayerPanel panel = new PlayerPanel(10);
                ServerSocket socket = new ServerSocket(8888);
                Socket accept = socket.accept();
                ObjectOutputStream outputStream = new ObjectOutputStream(accept.getOutputStream());
                outputStream.writeObject(panel);
                panel.setPlayerNumber(11);
                outputStream.writeObject(panel);
                panel.setPlayerNumber(12);
                Thread.sleep(5000);
                outputStream.writeObject(panel);
            } catch (IOException e) {

            } catch (InterruptedException e) {
            }
        }).start();


        new Thread(() -> {
            try {
                PlayerPanel panel = new PlayerPanel(10);
                Socket socket = new Socket("localhost", 8888);
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                System.out.println(((PlayerPanel) inputStream.readObject()).getPlayerNumber());
                System.out.println(((PlayerPanel) inputStream.readObject()).getPlayerNumber());
                System.out.println(((PlayerPanel) inputStream.readObject()).getPlayerNumber());
            } catch (IOException e) {
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
