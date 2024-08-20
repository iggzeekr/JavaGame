import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Paddle extends GameObject implements KeyListener {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 20;

    public Paddle() {
        super(Game.GAME_WIDTH / 2 - WIDTH / 2, Game.GAME_HEIGHT - HEIGHT * 4, WIDTH, HEIGHT);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawRect(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                this.moveBy(20, 0);
                break;
            case KeyEvent.VK_LEFT:
                this.moveBy(-20, 0);
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
