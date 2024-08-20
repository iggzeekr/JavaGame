import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Game {

    public static final int GAME_WIDTH = 500;
    public static final int GAME_HEIGHT = 600;

    public static Game game;
    private int score = 0;
    private int lives;
    private int level = 1;
    private JLabel scoreLabel;
    private JLabel livesLabel;
    private JLabel levelLabel;
    private GamePanel gamePanel;
    private JFrame frame;
    private boolean paused = true;

    private Game() {
        JFrame menuFrame = new JFrame("Menu");
        menuFrame.setSize(GAME_WIDTH, GAME_HEIGHT);
        menuFrame.setLocationRelativeTo(null);
        menuFrame.setLayout(new BorderLayout());
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setResizable(false);
        menuFrame.setLayout(new GridLayout(6, 1));

        JButton button = new JButton("New Game");
        button.setMaximumSize(new Dimension(100, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.setFocusable(false);
        button.addActionListener(e -> {
            startGameFrame();
        });
        menuFrame.add(button);

        button = new JButton("Scores");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(100, 40));
        button.addActionListener(e -> {
            showBestScore();
        });
        button.setFocusable(false);
        menuFrame.add(button);

        button = new JButton("Help");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(100, 40));
        button.addActionListener(e -> {
            help();
        });
        button.setFocusable(false);
        menuFrame.add(button);

        button = new JButton("About");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(100, 40));
        button.addActionListener(e -> {
            about();
        });
        button.setFocusable(false);
        menuFrame.add(button);

        button = new JButton("Exit");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(100, 40));
        button.addActionListener(a -> {
            System.exit(0);
        });
        button.setPreferredSize(new Dimension(100, 30));
        button.setFocusable(false);
        menuFrame.add(button);

        menuFrame.setVisible(true);
    }

    private void startGameFrame() {
        frame = new JFrame("Arkanoid Game");
        frame.setSize(GAME_WIDTH, GAME_HEIGHT + 50);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        gamePanel = new GamePanel();

        JPanel scorePanel = new JPanel();

        scorePanel.setBackground(Color.DARK_GRAY);
        scorePanel.setPreferredSize(new Dimension(GAME_WIDTH, 50));
        scorePanel.setLayout(new GridLayout(1, 3));

        scoreLabel = new JLabel("Score: " + this.score);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scorePanel.add(scoreLabel);

        levelLabel = new JLabel("Level: " + this.level);
        levelLabel.setForeground(Color.WHITE);
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scorePanel.add(levelLabel);

        livesLabel = new JLabel("Lives: " + this.lives);
        livesLabel.setForeground(Color.WHITE);
        livesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scorePanel.add(livesLabel);


        frame.add(gamePanel);
        frame.add(scorePanel, BorderLayout.NORTH);

        frame.setVisible(true);
        newGame();

        new Timer(5, e -> game.getGamePanel().run()).start();
    }

    public void newGame() {
        this.score = 0;
        this.lives = 3;
        this.level = 1;
        this.scoreLabel.setText("Score: " + this.score);
        this.livesLabel.setText("Lives: " + this.lives);
        this.levelLabel.setText("Level: " + this.level);
        this.gamePanel.createGameObjects(this.level, true);
        if (isPaused()) {
            gamePause();
            gamePause();
        }
    }

    public void nextLevel() {
        this.lives++;
        this.livesLabel.setText("Lives: " + this.lives);
        this.level++;
        this.gamePanel.createGameObjects(this.level, true);
        this.frame.addKeyListener(this.gamePanel.getPaddle());
    }

    public void gamePause() {
        if (this.paused) {
            this.frame.removeKeyListener(this.gamePanel.getPaddle());
            this.paused = false;
        } else {
            this.frame.addKeyListener(this.gamePanel.getPaddle());
            this.paused = true;
        }
    }

    public void gameOver() {
        String str = "GAME OVER!!!\nYour Score: " + this.score + "\n Insert your name: ";
        String name = JOptionPane.showInputDialog(str);

        if (name != null) {
            String write = name + " " + this.score + "\n";
            try {
                Files.write(Paths.get("scores.txt"), write.getBytes(),
                        StandardOpenOption.APPEND);
            } catch (IOException e) {
                try {
                    FileOutputStream f = new FileOutputStream(new File("scores.txt"));
                    f.write(write.getBytes());
                    f.close();
                } catch (IOException e1) {
                }

            }
        }

        String ObjButtons[] = {"Continue", "Exit"};
        int PromptResult = JOptionPane.showOptionDialog(null,
                "Do you want to play again?", "Play again?", JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
        if (PromptResult == JOptionPane.YES_OPTION) {
            newGame();
        } else {
            System.exit(0);
        }

    }

    public void showBestScore() {
        BufferedReader inputStream = null;
        String score = "scores:\n";
        try {
            inputStream = new BufferedReader(new FileReader("scores.txt"));
            String l;
            while ((l = inputStream.readLine()) != null) {
                score = score + "\n" + l;
            }
        } catch (FileNotFoundException e) {
            score = score + "No one is here.";
        } catch (IOException e) {
            score = score + "No one is here.";
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        JOptionPane.showMessageDialog(this.frame, score, "scores",
                JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    public void about() {
        JOptionPane.showMessageDialog(this.frame, "Developed By EZGİ KARA", "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void help() {
        JOptionPane.showMessageDialog(this.frame, "Use Left & Right key to move the paddle. ", "Help",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void minusLive() {
        this.lives -= 1;
        if (!(this.lives < 0)) {
            this.livesLabel.setText("Lives: " + this.lives);
            this.gamePanel.createGameObjects(this.level, false);
            this.frame.addKeyListener(this.gamePanel.getPaddle());
        } else {
            gameOver();
        }
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLives() {
        return this.lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public JLabel getScoreLabel() {
        return this.scoreLabel;
    }

    public JLabel getLivesLabel() {
        return this.livesLabel;
    }

    public GamePanel getGamePanel() {
        return this.gamePanel;
    }


    public static void main(String[] args) {
        game = new Game();
    }
}
