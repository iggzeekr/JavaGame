import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GamePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private Ball ball;
    private Paddle paddle;
    private BufferedImage image;
    private ArrayList<Brick> bricks;
    private int numOfBricks = 0;

    public GamePanel() {
        bricks = new ArrayList<>();
    }

    @Override
    public void paint(Graphics g) {
        this.image = new BufferedImage(Game.GAME_WIDTH, Game.GAME_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics b = this.image.getGraphics();
        b.setColor(new Color(9, 3, 40));
        b.fillRect(0, 0, image.getWidth(), image.getHeight());
        this.ball.paint(b);
        this.paddle.paint(b);
        for (Brick brick : this.bricks) {
            if (brick.isVisible())
                brick.paint(b);
        }
        g.drawImage(this.image, 0, 0, this);
    }

    public void run() {
        ball.move();
        repaint();
    }

    public ArrayList<Brick> getBricks() {
        return this.bricks;
    }

    public void removeBrick(Brick b, int dScore) {
        b.decreaseLevel();
        if (b.getLevel() <= 0) {
            b.setVisible(false);
            this.numOfBricks--;
            Game.game.setScore(Game.game.getScore() + dScore);
            Game.game.getScoreLabel().setText("Score: " + Game.game.getScore());
        }
        if (this.numOfBricks == 0) {
            Game.game.nextLevel();
        }
    }

    public void createGameObjects(int level, boolean respawn) {
        this.paddle = new Paddle();
        this.ball = new Ball(level + 2);
        int x = 15;
        int y = 40;
        if (respawn) {
            this.bricks = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    this.bricks.add(new Brick(x, y, level));
                    this.numOfBricks++;
                    x += 52;
                }
                x = 15;
                y += 37;
            }
        }
    }

    public Paddle getPaddle() {
        return this.paddle;
    }
}
