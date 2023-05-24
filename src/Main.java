import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main {
    private static Point blockShapes[][] = {
            // I -block
        { new Point (0,0), new Point (0, 1), new Point (0,2), new Point (0, -1)},
            // L -block
            { new Point (0,0), new Point (0, -1), new Point (0,1), new Point (1, 1)},
            // L reversed -block
            { new Point (0,0), new Point (0, -1), new Point (0,1), new Point (-1, 1)},
            // Cube -block
            { new Point (0,0), new Point (0, 1), new Point (1,0), new Point (1, 1)},
            // T -block
            { new Point (0,0), new Point (-1, 0), new Point (1,0), new Point (0, 1)},
            // z -block
            { new Point (0,0), new Point (0, 1), new Point (-1,1), new Point (-1, 2)},
            // reversed z -block
            { new Point (0,0), new Point (0, 1), new Point (1,1), new Point (1, 2)}
    };

    static Color blockColors[] = { new Color (190, 40, 40),
            new Color (10, 200, 10),
            new Color (30, 30, 180),
            new Color (240, 210, 30),
            new Color (160, 10, 160),
            new Color (10, 190, 200),
            new Color (180, 100, 20) };

    static int[][] rotationMatrix = {
            {1, 0, 1, 0 },
            {0, -1, 0, 1 },
            {-1, 0, -1, 0 },
            {0, 1, 0, -1 }    };
    static int tileSize = 26;
    static int marginX = 0;
    static int marginY = 36;

    static int tilesX = 12;
    static int tilesY = 20;

    static Point playerPos;
    static int playerPice;
    static int rotation = 0;

    static boolean gameRunning = true;
    static int speed = 240;
    static int score = 0;
    static Color[][] pinned = new Color [tilesX][tilesY];
    public static void main(String[] args) {

        JFrame frame = new JFrame("Tetris");
        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);

                for (int y = 0; y < tilesY; y++) {
                    int count = 0;
                    for (int x = 0; x < tilesX; x++) {
                        if (pinned[x][y] == null) continue;
                        count ++;
                        g.setColor (pinned[x][y]);
                        g.fillRect(x  * tileSize + marginX, y  * tileSize + marginY, tileSize - 1, tileSize - 1);
                    }
                }

                if (playerPos == null) return;

                g.setColor(blockColors[playerPice]);
                for (Point p : blockShapes[playerPice]) {
                    int x1 = getRotatedX (p);
                    int y1 = getRotatedY(p);
                    g.fillRect(x1 * tileSize + marginX,
                            y1 * tileSize + marginY, tileSize - 1, tileSize - 1);
                }
                g.setColor (Color.WHITE);
                g.drawString("Score: " + score, 10, 26);
            }
        };
        frame.setContentPane(panel);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setSize(tilesX * tileSize + frame.FRAMEBITS + marginX * 2, tilesY * tileSize + marginY * 2);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyPressed(KeyEvent e) {
                if (playerPos == null) return;
                if (!gameRunning) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        // restart game
                        newGame ();
                        }
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (posAllowed(-1, 0))
                        playerPos.x--;
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (posAllowed(1, 0))
                        playerPos.x++;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (posAllowed(0, 1)) {
                        playerPos.y++;
                        frame.repaint();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    if (playerPice == 3) return; // Cube cant be rotated
                    int oldRotation = rotation;
                    rotation ++;
                    rotation %= 4;
                    if (!posAllowed(0, 0))
                        rotation = oldRotation;
                    frame.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();

                while (true) {
                    if (playerPos == null) {
                        playerPos = new Point(tilesX / 2, 2);
                        playerPice = (int) (Math.random() * blockShapes.length);
                    }
                    if(gameRunning) {
                        if (!posAllowed(0, 1)) {
                            pinn();
                        } else {
                            playerPos.y += 1;
                        }

                        removeFullLines();
                    }
                    frame.repaint();
                    try {
                        sleep(speed);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                 }
            }
        };

        thread.start();

    }

    private static void newGame () {
        gameRunning = true;
        playerPos = null;
        speed = 240;

        // Clear Pinned
        for (int y = 0; y < tilesY; y ++ ) {
            for (int x = 0; x < tilesX; x ++) {

                pinned[x][y] = null;
            }
        }
    }

    private static boolean posAllowed (int x, int y) {
        for (Point p:blockShapes[playerPice]) {
            int x1 = getRotatedX (p);
            int y1 = getRotatedY(p);
            if (x1 + x < 0 || x1 + x >= tilesX)
                return false;
            if (y1 + y < 0 || y1 + y >= tilesY)
                return false;
            // Check pinned
            if (pinned[x1 + x][y1 + y] != null)
                return false;
        }
        return true;
    }

    private static int getRotatedX (Point p) {
        return playerPos.x + p.x * rotationMatrix[rotation][0] + p.y * rotationMatrix[rotation][1];
    }
    private static int getRotatedY (Point p) {
        return playerPos.y + p.y * rotationMatrix[rotation][2] + p.x * rotationMatrix[rotation][3];
    }
    private static void pinn () {
        for (Point p:blockShapes[playerPice]) {
            int x1 = getRotatedX (p);
            int y1 = getRotatedY(p);
            pinned[x1][y1] = blockColors[playerPice];
        }
        playerPos = new Point(tilesX / 2, 1);
        playerPice = (int) (Math.random() * blockShapes.length);

        if (!posAllowed(0, 0)) {
            // Game over
            gameRunning = false;
        }

        // Check if full line
        for (int y = 0; y < tilesY; y ++) {
            int count = 0;
            for (int x = 0; x < tilesX; x ++) {
                if (pinned[x][y] != null)
                    count++;
            }
            if (count >= tilesX) {
                for (int x = 0; x < tilesX; x ++) {
                    pinned[x][y] = Color.WHITE;
                }
            }
        }
    }

    private static void removeFullLines () {
        int lineCount = 0;
        for (int y = 0; y < tilesY; y++) {
            if (pinned[0][y] == Color.WHITE) {
                removeLine(y);
                lineCount++;
                speed -= 1;
                if (speed < 60)
                    speed = 60;
            }
        }
        score += lineCount * lineCount;
    }

    private static void removeLine (int line) {
        for (int y = line; y > 0; y--) {
            for (int x = 0; x < tilesX; x ++)
            {
                pinned[x][y] = pinned[x][y-1];
            }
        }
        for (int x = 0; x < tilesX; x ++)
            pinned[x][0] = null;
    }
}