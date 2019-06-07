package main.java.ir.qut.client;

import main.java.ir.qut.server.BlockMovementResponsible;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.List;

public class PlayerPanel extends JPanel implements Serializable, Cloneable {
    private String carImagePath = "C:\\Users\\Hamed Abbaszadeh\\programming\\projects\\carsRace\\src\\main\\java\\ir\\qut\\client\\car.png";
    private String blockImagePath = "C:\\Users\\Hamed Abbaszadeh\\programming\\projects\\carsRace\\src\\main\\java\\ir\\qut\\client\\carBlock.png";
    private int carX = 100;
    private int carY = 22;
    private int x = 20;
    private int x2 = x + 900;
    private int y1 = 20;
    private int y2 = y1 + 40;
    private int y3 = y1 + 80;
    private int mouseX;
    private int mouseY;
    private boolean isCrashed = false;
    private boolean isUp = true;
    private Label label;
    private Label scoreLabel;
    private Integer score = 0;
    private int speed = 50;
    private java.util.List<Integer> upRoadPositions = new ArrayList<>();
    private java.util.List<Integer> dowRoadPositions = new ArrayList<>();
    private transient BufferedImage image;
    private transient BlockMovementResponsible blockMovementResponsible;
    private transient Socket objectTransferSocket;
    private transient ObjectOutputStream objectOutputStream;
    public int playerNumber;

    public PlayerPanel(int playerNumber) {
        this.playerNumber = playerNumber;
        init();
        label = new Label("(" + mouseX + "," + mouseY + ")");
        Label playerNumberLabel = new Label("p#:" + playerNumber);
        scoreLabel = new Label("score: 0", Label.RIGHT);
        label.setAlignment(Label.CENTER);
        blockMovementResponsible = new BlockMovementResponsible(upRoadPositions, dowRoadPositions, isCrashed, speed, this);
        this.add(label);
        this.add(scoreLabel);
        this.add(playerNumberLabel);

        new Thread(() -> {
            while (true) {
                if (speed > 0)
                    speed -= 1;
                else
                    speed = 50;
                try {
                    blockMovementResponsible.setSpeed(speed);
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            image = ImageIO.read(new File(blockImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToServer(Socket socket) {
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(objectTransferSocket.getOutputStream());
            blockMovementResponsible.start();
            while (true) {
                int read = input.read();
                if (isCrashed) {
                    break;
                }

                if (read == 1) {
                    upRoadPositions.add(x2 - 50);

                } else {
                    dowRoadPositions.add(x2 - 50);
                }
                if (objectOutputStream != null) {
                    PlayerPanel panel = (PlayerPanel) this.clone();
                    objectOutputStream.writeObject(panel);
                    objectOutputStream.reset();
                   /* System.out.println("when serializing...");
                    System.out.println("up blocks: " + panel.getUpRoadPositions());
                    System.out.println("down blocks: " + panel.getDowRoadPositions());*/
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        PlayerPanel panel = this;
        panel.setFocusable(true);
        panel.requestFocusInWindow();
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_DOWN:
                        carY = 62;
                        isUp = false;
                        panel.repaint();
                        break;
                    case KeyEvent.VK_UP:
                        carY = 22;
                        isUp = true;
                        panel.repaint();
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                label.setText("(" + mouseX + "," + mouseY + ")");
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        BufferedImage car;
        try {
            car = ImageIO.read(new File(carImagePath));
            g.drawLine(x, y1, x2, y1);
            Stroke dashed = new BasicStroke(0, BasicStroke.CAP_ROUND, BasicStroke.CAP_SQUARE, 0, new float[]{9}, 0);
            g2d.setStroke(dashed);
            g2d.drawLine(x, y2, x2, y2);
            g.drawLine(x, y3, x2, y3);
            g.drawImage(car, carX, carY, 40, 40, this);

            Integer integer;
            for (int i = 0; i < upRoadPositions.size(); i++) {
                integer = upRoadPositions.get(i);
                if (integer < 10) {
                    upRoadPositions.remove(i);
                    if (!isCrashed) {
                        scoreLabel.setText(String.valueOf(++score));
                    }
                } else {
                    g.drawImage(image, integer, 22, 40, 40, this);
                   /* if (integer >= carX && integer < 140 && isUp) {
                        isCrashed = true;
                        blockMovementResponsible.setCrashed(isCrashed);
                        blockMovementResponsible.interrupt();
                        new Thread(() -> {
                            JOptionPane.showMessageDialog(null, "You Suck Buddy, try again!!");
                            System.exit(1);
                        }).start();
                    }*/

                }
            }

            for (int i = 0; i < dowRoadPositions.size(); i++) {
                integer = dowRoadPositions.get(i);
                if (integer < 10) {
                    dowRoadPositions.remove(i);
                    if (!isCrashed) {
                        scoreLabel.setText(String.valueOf(++score));
                    }
                } else {
                    g.drawImage(image, integer, 62, 40, 40, this);
                    /*if (integer >= carX && integer < 140 && !isUp) {
                        isCrashed = true;
                        blockMovementResponsible.setCrashed(isCrashed);
                        blockMovementResponsible.interrupt();
                        new Thread(() -> {
                            JOptionPane.showMessageDialog(null, "You Suck Buddy, try again!!");
                            System.exit(1);
                        }).start();
                    }*/

                }

            }

        } catch (IOException e) {
        }
    }

    public Socket getObjectTransferSocket() {
        return objectTransferSocket;
    }

    public void setObjectTransferSocket(Socket objectTransferSocket) {
        this.objectTransferSocket = objectTransferSocket;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public static void main(String[] args) {

        int playerNumber = 1;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Create Player? y/n");
        String next = scanner.next();
        while (!next.equals("n")) {
            createPlayer(playerNumber);
            System.out.println("Create Player? y/n");
            playerNumber++;
            next = scanner.next();
        }
    }

    private static void createPlayer(int playerNumber) {
        new Thread(() -> {
            JFrame frame = new JFrame("player");
            frame.setLayout(new GridLayout(5, 0));
            PlayerPanel mainPlayerPanel = new PlayerPanel(playerNumber);
            frame.add(mainPlayerPanel);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(950, 600);
            frame.setVisible(true);
            frame.setResizable(false);
            Socket socket;
            Socket objectTransferSocket;
            try {
                socket = new Socket("localhost", 8080);
                objectTransferSocket = new Socket("localhost", 8080);
                mainPlayerPanel.setObjectTransferSocket(objectTransferSocket);
                new Thread(() -> {
                    while (true) {
                        try {
                            ObjectInputStream objectInputStream = new ObjectInputStream(objectTransferSocket.getInputStream());
                            PlayerPanel serializedPlayerPanel = (PlayerPanel) objectInputStream.readObject();
                            PlayerPanel toAddToFramePlayerPanel = new PlayerPanel(serializedPlayerPanel.getPlayerNumber());
                            toAddToFramePlayerPanel.carX = serializedPlayerPanel.carX;
                            toAddToFramePlayerPanel.carY = serializedPlayerPanel.carY;
                            toAddToFramePlayerPanel.dowRoadPositions = serializedPlayerPanel.dowRoadPositions;
                            toAddToFramePlayerPanel.upRoadPositions = serializedPlayerPanel.upRoadPositions;
                            toAddToFramePlayerPanel.isCrashed = serializedPlayerPanel.isCrashed;
                            List<Component> components = Arrays.asList(frame.getContentPane().getComponents());
                            int index = 0;

                            for (int i = 0; i < frame.getContentPane().getComponentCount(); i++) {
                                if (frame.getContentPane().getComponent(i).equals(toAddToFramePlayerPanel)) {
                                    index = i;
                                }
                            }

                            switch (toAddToFramePlayerPanel.playerNumber) {
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                    if (components.contains(toAddToFramePlayerPanel)) {
                                        frame.getContentPane().remove(index);
                                        frame.getContentPane().add(toAddToFramePlayerPanel, index);
                                    } else {
                                        frame.getContentPane().add(toAddToFramePlayerPanel);
                                    }
                                    break;
                            }


                        /*serializedPlayerPanel.repaint();
                        System.out.println("up blocks: " + serializedPlayerPanel.getUpRoadPositions());
                        System.out.println("down blocks: " + serializedPlayerPanel.getDowRoadPositions());
                        System.out.println("-----");*/
                            frame.revalidate();
                            frame.repaint();
                            Thread.sleep(300);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                // the right place, don't suspect!
                mainPlayerPanel.connectToServer(socket);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerPanel that = (PlayerPanel) o;
        return playerNumber == that.playerNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerNumber);
    }

    public List<Integer> getUpRoadPositions() {
        return upRoadPositions;
    }

    public void setUpRoadPositions(List<Integer> upRoadPositions) {
        this.upRoadPositions = upRoadPositions;
    }

    public List<Integer> getDowRoadPositions() {
        return dowRoadPositions;
    }

    public void setDowRoadPositions(List<Integer> dowRoadPositions) {
        this.dowRoadPositions = dowRoadPositions;
    }

}
