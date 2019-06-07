package main.java.ir.qut.server;

import javax.swing.*;
import java.util.List;

public class BlockMovementResponsible extends Thread {
    private JPanel panel;
    private List<Integer> upRoadPositions;
    private List<Integer> dowRoadPositions;
    private Integer speed;
    boolean isCrashed;

    public BlockMovementResponsible(List<Integer> upRoadPositions, List<Integer> dowRoadPositions,
                                     boolean isCrashed,Integer speed,JPanel panel) {
        this.panel = panel;
        this.speed = speed;
        this.isCrashed = isCrashed;
        this.upRoadPositions = upRoadPositions;
        this.dowRoadPositions = dowRoadPositions;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Integer integer;
            if (isCrashed) {
                return;
            }
            synchronized (upRoadPositions) {
                for (int i = 0; i < upRoadPositions.size(); i++) {
                    integer = upRoadPositions.get(i);
                    if (integer >= 10)
                        upRoadPositions.set(i, --integer);
                }
            }
            synchronized (dowRoadPositions) {
                for (int i = 0; i < dowRoadPositions.size(); i++) {
                    integer = dowRoadPositions.get(i);
                    if (integer >= 10)
                        dowRoadPositions.set(i, --integer);
                }
            }
            panel.repaint();
        }
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public boolean isCrashed() {
        return isCrashed;
    }

    public void setCrashed(boolean crashed) {
        isCrashed = crashed;
    }
}
