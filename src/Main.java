import javax.swing.*;
import java.awt.*;

public class Main {
    private static Point blockShapes[][] = {
            // I -block
        { new Point (0,0), new Point (0, 1), new Point (0,2), new Point (0, 3)},
            // L -block
            { new Point (0,0), new Point (0, 1), new Point (0,2), new Point (1, 2)},
            // L reversed -block
            { new Point (0,0), new Point (0, 1), new Point (0,2), new Point (-1, 2)},
            // Cube -block
            { new Point (0,0), new Point (0, 1), new Point (1,0), new Point (1, 1)},
            // T -block
            { new Point (0,0), new Point (-1, 0), new Point (1,0), new Point (0, 1)},
            // z -block
            { new Point (0,0), new Point (0, 1), new Point (1,1), new Point (2, 1)},
    };

    static Color blockColors[] = { new Color (200, 10, 10),
            new Color (10, 200, 10),
            new Color (10, 10, 200),
            new Color (200, 200, 10),
            new Color (200, 10, 200),
            new Color (10, 200, 200) };

    static int tileSize = 26;
    static int marginX = 10;
    static int marginY = 26;

    static int tilesX = 12;
    static int tilesY = 20;

    static Point playerPos;
    static int playerPice;
    static Color[][] pinned = new Color [tilesX][tilesY];
    public static void main(String[] args) {

        JFrame frame = new JFrame("Tetris");
        JPanel panel = new JPanel () {
            @Override
            public void paint (Graphics g) {
                super.paint (g);
                g.setColor (blockColors[playerPice]);
                for (Point p: blockShapes[playerPice]) {
                    g.fillRect ((p.x + playerPos.x)  * tileSize + marginX,
                            (p.y + playerPos.y) * tileSize + marginY, tileSize -1, tileSize - 1);
                }
            }
        };
        frame.setContentPane(panel);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setSize (tilesX * tileSize + marginX * 2, tilesY * tileSize + marginY *2);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();

                if (playerPos == null) {
                    playerPos = new Point (tilesX/2, 1);
                    playerPice = (int)(Math.random () * blockShapes.length);
                } else {
                    // Move down
                    playerPos.y += 1;
                }


                try {
                    sleep (990);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                frame.repaint ();
            }
        };

        thread.start ();
    }
}