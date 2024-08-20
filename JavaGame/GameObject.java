import java.awt.*;

public abstract class GameObject {

    private int x, y;
    private final int width, height;

    public GameObject(int locX, int locY, int width, int height) {
        this.x = locX;
        this.y = locY;
        this.width = width;
        this.height = height;
    }

    public abstract void paint(Graphics g);

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void moveBy(int dx, int dy) {
        x += dx;
        y += dy;
        if (x + width > Game.GAME_WIDTH)
            x = Game.GAME_WIDTH - width;
        if (x < 0)
            x = 0;
        if (y + height > Game.GAME_HEIGHT)
            y = Game.GAME_HEIGHT - height;
        if (y < 0)
            y = 0;
    }
}
