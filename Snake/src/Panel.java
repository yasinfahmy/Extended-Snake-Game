import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * This is a program about the well known snake game. The only difference is, that there is always a chance of a golden apple spawning.
 * When eating it, the snake becomes immortal and can not collide with itself nor athe borders.
 *
 * @author Bro Code (from YouTube) gave me the kickstart to extend the game
 * https://www.youtube.com/watch?v=bI6e6qjJ8JQ
 *
 * @author Yasin Fahmy
 * Implemented a new feature with goldenApples that makes the snake invulnerable.
 * Impmeneted timers for the lsd effect and invulnerability.
 * Made it possible to restart the game in the end screen
 * Solved collision bug, which was caused by pressing two keys at once
 */

public class Panel extends JPanel implements ActionListener {
    /**
     * ===============================================================ATTRIBUTES========================================================================
     */
    //Static finals
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE; //Number of game units
    static final int DELAY = 75;
    static final int GOLD_DURATION = 5000; //Duration of invulnerability
    static final int LSD_DURATION = 1500; //Duration of visual lsd effect

    //Array of x and y coordinates of all body parts of the snake
    int[] x = new int[GAME_UNITS];
    int[] y = new int[GAME_UNITS];

    int bodyParts = 6;
    int applesEaten;

    //Coordinates of normal and golden apples
    int appleX; int appleY;
    int goldenAppleX = -UNIT_SIZE*10; //Everytime a game is started, an apple will spawn at (0,0). Setting the coordinates out of the game field prevents this.
    int goldenAppleY = -UNIT_SIZE*10;

    char direction = 'S'; //Initial direction of the snake

    boolean running = false;
    boolean lsdsnake;
    boolean invulnerable;
    boolean goldenAppleActive;
    boolean keyPressed; //Very important to prevent collision bug, when two keys are pressed at the same time. With this only one key can be pressed per unit

    //Used for managing the time duration of the states of a snake
    ActionListener taskLsdTimer = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            lsdsnake = false;
        }
    };
    ActionListener taskGoldTimer = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            invulnerable = false;
        }
    };
    Timer lsdTimer = new Timer(LSD_DURATION,taskLsdTimer);
    Timer invulnerabilityTimer = new Timer(GOLD_DURATION,taskGoldTimer);
    Timer timer; //Game timer
    Random random;

    /**
     * ===============================================================METHODS========================================================================
     * Methods related to drawing and setting the game up
     */
    //displays panel
    public Panel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true); //What does this do?
        this.addKeyListener(new MyKeyAdapter()); //Was macht KeyAdapter?
        startGame();
    }

    //Starts game and drops red apple
    public void startGame(){
        newApple();
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
    }

    //Method to draw everything on the panel
    public void draw(Graphics graphics){
        if (running) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                graphics.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                graphics.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            graphics.setColor(Color.RED);
            graphics.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            graphics.setColor(Color.ORANGE);
            graphics.fillOval(goldenAppleX, goldenAppleY, UNIT_SIZE, UNIT_SIZE);


            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    graphics.setColor(Color.GREEN);
                    graphics.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
                else if(lsdsnake && !invulnerable){
                    graphics.setColor(new Color(random.nextInt(255),(random.nextInt(255)),(random.nextInt(255))));
                    graphics.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
                else if(invulnerable){
                    graphics.setColor(Color.ORANGE);
                    graphics.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
                else {
                    graphics.setColor(new Color(45, 180, 0));
                    graphics.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            graphics.setColor(Color.RED);
            graphics.setFont(new Font("Ink Free", Font.BOLD,40));
            FontMetrics fontMetrics = getFontMetrics(graphics.getFont());
            graphics.drawString("Score "+applesEaten, (SCREEN_WIDTH - fontMetrics.stringWidth("Score "+applesEaten))/2, graphics.getFont().getSize());
        }
        else{
            gameOver(graphics);
        }
    }

    //is part of the painting process
    public void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        draw(graphics);
    }

    //Displays Game Over Endscreen
    public void gameOver(Graphics graphics){
        keyPressed = false;

        //Display Score
        graphics.setColor(Color.RED);
        graphics.setFont(new Font("Ink Free", Font.BOLD,40));
        FontMetrics fontMetrics1 = getFontMetrics(graphics.getFont());
        graphics.drawString("Score "+applesEaten, (SCREEN_WIDTH - fontMetrics1.stringWidth("Score "+applesEaten))/2, graphics.getFont().getSize());

        //Game Over Text
        graphics.setColor(Color.RED);
        graphics.setFont(new Font("Ink Free", Font.BOLD,75));
        FontMetrics fontMetrics2 = getFontMetrics(graphics.getFont());
        graphics.drawString("Game Over", (SCREEN_WIDTH - fontMetrics2.stringWidth("Game Over"))/2, 250);

        //Press enter to restart game
        graphics.setColor(Color.lightGray);
        graphics.setFont(new Font("Ink Free", Font.BOLD,20));
        FontMetrics fontMetrics3 = getFontMetrics(graphics.getFont());
        graphics.drawString("Press enter to restart", (SCREEN_WIDTH - fontMetrics3.stringWidth("Game Over"))/2, 300);
    }

    /**
     * Methods that are relevant for the game itself
     */
    //correctly moves snake
    public void move(){
        ////Shifting body parts by one spot to let the snake grow
        for (int i=bodyParts; i>0; i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction){
            case 'N':
                y[0] = y[0] - UNIT_SIZE;
                keyPressed = false;
                break;
            case 'S':
                y[0] = y[0] + UNIT_SIZE;
                keyPressed = false;
                break;
            case 'W':
                x[0] = x[0] - UNIT_SIZE;
                keyPressed = false;
                break;
            case 'E':
                x[0] = x[0] + UNIT_SIZE;
                keyPressed = false;
                break;
        }
    }

    //Checks for collision with the snake itself or the border and if immortal moves head of snake to the other side
    public void checkCollisions() {
        if (!invulnerable) {
            //Snake collides with itself
            for (int i = bodyParts; i > 0; i--) {
                if (x[0] == x[i] && y[0] == y[i]) {
                    running = false;
                    break;
                }
            }
            //Check if head touches left border
            if (x[0] < 0) {
                running = false;
            }
            //Check if head touches left border
            if (x[0] > SCREEN_WIDTH - UNIT_SIZE) {
                running = false;
            }
            //Check if head touches top border
            if (y[0] < 0) {
                running = false;
            }
            //Check if head touches bottom border
            if (y[0] > SCREEN_HEIGHT - UNIT_SIZE) {
                running = false;
            }

            if (!running) {
                timer.stop();
            }
        }
        else{
            //Check if head touches left border
            if (x[0] < 0) {
                x[0] = SCREEN_WIDTH - UNIT_SIZE;
            }
            //Check if head touches left border
            if (x[0] > SCREEN_WIDTH - UNIT_SIZE) {
                x[0] = 0;
            }
            //Check if head touches top border
            if (y[0] < 0) {
                y[0] = SCREEN_HEIGHT - UNIT_SIZE;
            }
            //Check if head touches bottom border
            if (y[0] > SCREEN_HEIGHT - UNIT_SIZE) {
                y[0] = 0;
            }
        }
    }

    //Spawns new apple
    public void newApple(){
        appleX = random.nextInt(SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE; //Range, Minimum
        appleY = random.nextInt(SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;

    }

    //Spawns new apple if all conditions are met
    public void newGoldenApple(){
        int x = random.nextInt(SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
        int y = random.nextInt(SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;

        //Calculate chance
        double chance = (double) applesEaten/100;
        if(chance>1.0){
            chance = 1;
        }

        if((Math.random() <=  chance)  //Chance of golden apple spawn
                && !invulnerable && !goldenAppleActive //Snake shouldnt be in the invulnerable state already and golden apple should not spawn if it is already apparent
                && x != appleX && y !=appleY){ //ensuring both apple type dont end up having the same coordinates
            goldenAppleActive = true;
            goldenAppleX = x;
            goldenAppleY = y;

        }
    }

    //Checks if head of snake hits apple
    public void checkApple(){
        if((x[0] == appleX && y[0] == appleY)){
            bodyParts++;
            applesEaten++;
            newApple();
            newGoldenApple();
            lsdsnake = true;
            lsdTimer();
        }
    }

    //Checks if head of snake hits golden apple
    public void checkGoldenApple(){
        if((x[0] == goldenAppleX && y[0] == goldenAppleY)){
            invulnerable = true;
            invulnerabilityTimer();
            goldenAppleActive = false;
            goldenAppleX = -UNIT_SIZE*10; goldenAppleY = -UNIT_SIZE*10; //Making sure the apple wont spawn if invulnerability is on
        }
    }

    //Timer for lsd state, restarts everytime a new apple is eaten
    public void lsdTimer(){
        if(lsdTimer.isRunning()){
            lsdTimer.restart();
        }
        else{
            lsdTimer.start();
        }
    }

    //Timer for invulnerability state, restarts everytime a new golden apple is eaten
    public void invulnerabilityTimer(){
        if(invulnerabilityTimer.isRunning()){
            invulnerabilityTimer.restart();
        }
        else {
            invulnerabilityTimer.start();
        }
    }

    /**
     * Methods for key management
     */
    //Listens for any action
    @Override
    public void actionPerformed(ActionEvent e) { //Method implementation. Is onvoked everytime game state changes
        if(running){
            move();
            checkApple();
            checkGoldenApple();
            checkCollisions();
        }
        repaint();
    }

    //Manages KeyEvents
    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent keyEvent){
            //For restartig the game in end chart
            if(!running && keyEvent.getKeyCode() == KeyEvent.VK_ENTER){
                direction = 'S';

                x = new int[GAME_UNITS];
                y = new int[GAME_UNITS];
                x[0] = 0;
                y[0] = 0;

                applesEaten = 0;
                bodyParts = 6;

                lsdsnake = false;
                invulnerable = false;

                startGame();
            }

            //Prevents bug
            if (keyPressed){
                return;
            }
            //The snake can not change to opposite direction
            //In the end screen pressing enter restarts the game
            switch (keyEvent.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if(direction != 'E'){
                        direction = 'W';
                        keyPressed = true;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'W'){
                        direction = 'E';
                        keyPressed = true;
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'S'){
                        direction = 'N';
                        keyPressed = true;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'N'){
                        direction = 'S';
                        keyPressed = true;
                    }
                    break;
            }
        }
    }
}
