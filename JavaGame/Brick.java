import java.awt.*;

public class Brick extends GameObject {

    private static final Color[] colorsByLives = {Color.RED, Color.ORANGE, Color.BLACK};
    private int level;
    private boolean isVisible = true;

    public Brick(int x, int y, int lives) {
        super(x, y, 40, 25);
        this.level = lives;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(colorsByLives[this.level - 1]);
        g.fillRect(getX(), getY(), getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.drawRect(getX(), getY(), getWidth(), getHeight());
    }

    public void decreaseLevel() {
        this.level--;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }
}
