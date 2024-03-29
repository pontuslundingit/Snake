import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 20;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static int DELAY = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    JButton newGameButton;
    JButton exitButton;
    JPanel buttonPanel;
    boolean startScreenVisible = true;
    boolean gameStarted = false;


    GamePanel() {
        newGameButton = new JButton("New Game");
        exitButton = new JButton("Exit");
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });


        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1));
        buttonPanel.add(newGameButton);
        buttonPanel.add(exitButton);
        buttonPanel.setVisible(false);

        add(buttonPanel);
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 380));

        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(127, 187, 37));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        if (!gameStarted) {
            startGame();
        }
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
        gameStarted = true;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (startScreenVisible) {
            startScreen(g);
        } else if (running) {
            draw(g);
        } else {
            gameOver(g);
        }

    }

    public void draw(Graphics g) {

        if (running) {

            //GRID LINES
            /*
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            */

            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.black);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.setColor(new Color(random.nextInt(255), random.nextInt(1), random.nextInt(50)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.black);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
        }

        else {
            gameOver(g);
        }
    }

    public void newApple() {
        boolean appleOnSnake = true;

        while (appleOnSnake) {
            appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

            for (int i = 0; i < bodyParts; i++) {
                if (appleX == x[i] && appleY == y[i]) {
                    appleOnSnake = true;
                    break;
                } else {
                    appleOnSnake = false;
                }
            }
        }
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            increaseSpeed();
            newApple();
        }
    }

    private void increaseSpeed() {
        DELAY -= 1;
        timer.setDelay(DELAY);
    }

    public void checkCollisions() {
        //This checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && y[0] == y[i]) {
                running = false;
            }
        }
        //Check if head touches LEFT border
        if (x[0] < 0) {
            running = false;
        }
        //Check if head touches RIGHT border
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }
        //Check if head touches TOP border
        if (y[0] < 0) {
            running = false;
        }
        //Check if head touches BOTTOM border
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        //Score display
        g.setColor(Color.black);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
        //Game Over text
        g.setColor(Color.black);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over :(", (SCREEN_WIDTH - metrics2.stringWidth("Game Over :("))/2, SCREEN_HEIGHT/2);
        newGameButton.setVisible(true);
        exitButton.setVisible(true);
        buttonPanel.setVisible(true);
    }

    public void startScreen(Graphics g) {
        //Start screen
        g.setColor(Color.black);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("SNAKE", (SCREEN_WIDTH - metrics3.stringWidth("SNAKE"))/2, SCREEN_HEIGHT/2);
        newGameButton.setVisible(true);
        exitButton.setVisible(true);
        buttonPanel.setVisible(true);
    }

    public void restartGame() {
        newGameButton.setVisible(false);
        exitButton.setVisible(false);
        buttonPanel.setVisible(false);
        bodyParts = 6;
        applesEaten = 0;
        DELAY = 75;
        direction = 'R';


        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        newApple();

        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(DELAY, this);
        timer.start();
        running = true;
        startScreenVisible = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }

}
