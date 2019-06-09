package main.java.ir.qut.server;

import java.io.DataOutputStream;
import java.io.OutputStream;
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
}
