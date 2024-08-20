import java.awt.*;

public class Ball extends GameObject {

    private static final int size = 12;
    private final int speed;
    private int dirX, dirY;

    public Ball(int speed) {
        super(Game.GAME_WIDTH / 2, Game.GAME_HEIGHT / 2, size, size);
        this.speed = speed;
        this.dirX = 0;
        this.dirY = 1;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval(getX(), getY(), size, size);
    }

    public void move() {
        if (!Game.game.isPaused()) {
            return;
        }

        moveBy(speed * dirX, speed * dirY);

        if (getX() + size >= Game.GAME_WIDTH - size) {
            this.dirX *= -1;
        }
        if (getX() <= 0) {
            this.dirX *= -1;
        }
        if (getY() <= 0) {
            this.dirY *= -1;
        }
        if (getY() + size >= Game.GAME_HEIGHT - 20) {
            Game.game.minusLive();
        }

        Paddle paddle = Game.game.getGamePanel().getPaddle();
        if (getX() + size >= paddle.getX() + paddle.getWidth() / 2
                && getX() + size <= paddle.getX() + paddle.getWidth() / 2 + 6) {
            if (getY() + size >= paddle.getY()) {
                this.dirX = 0;
                this.dirY *= -1;
            }
        }

        if (getX() + size >= paddle.getX()
                && getX() + size < paddle.getX()
                + paddle.getWidth() / 2) {
            if (getY() + size >= paddle.getY()) {
                this.dirX = -1;
                this.dirY *= -1;
            }
        }
        if (getX() + size > paddle.getX()
                + paddle.getWidth() / 2 + 6
                && getX() + size <= paddle.getX()
                + paddle.getWidth()) {
            if (getY() + size >= paddle.getY()) {
                this.dirX = 1;
                this.dirY *= -1;
            }
        }

        for (Brick brick : Game.game.getGamePanel().getBricks()) {
            if (!brick.isVisible()) {
                continue;
            }

            boolean removeBrick = false;

            if (getX() >= brick.getX() && getX() <= brick.getX() + brick.getWidth()) {
                if (getY() + size >= brick.getY() && getY() <= brick.getY() + brick.getHeight()) {
                    removeBrick = true;
                    this.dirY *= -1;
                }
            }

            if (!removeBrick && getY() >= brick.getY() && getY() <= brick.getY() + brick.getHeight()) {
                if (getX() + size >= brick.getX()
                        && getX() <= brick.getX() + brick.getWidth()) {
                    removeBrick = true;
                    this.dirX *= -1;
                }
            }
            if (removeBrick) {
                Game.game.getGamePanel().removeBrick(brick, speed);
            }
        }
    }
}
