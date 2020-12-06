package com.gmm;

import sun.java2d.loops.MaskBlit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanelPongGame extends JPanel implements Runnable{

    static final int GAME_WIDTH = 1080;
    static final int GAME_HEIGHT = (int)(GAME_WIDTH *(1.0*5/9));
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH,GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 100;
    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;
    Paddle paddle2;
    Ball ball;
    Score score;
    JButton button3;

    GamePanelPongGame() {

        button3 = new JButton("返回主界面");
        button3.setSize(198, 2000);

        newBall();
        newPaddles();
        score = new Score(GAME_WIDTH, GAME_HEIGHT);
        this.setFocusable(true);

        this.addKeyListener(new AL());//监听器
        this.setPreferredSize(SCREEN_SIZE);

        gameThread = new Thread(this);
//        gameThread.start();
        this.add(button3);
    }
    public void start(){
        newBall();
        newPaddles();
        score.player1 = 0;
        score.player2 = 0;
    }

    public void newBall(){
        random = new Random();
        int r =random.nextInt(GAME_HEIGHT);
        if (r <= 21){
            r = 21;
        }
        ball = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2),r - BALL_DIAMETER ,BALL_DIAMETER,BALL_DIAMETER);

    }
    public void newPaddles(){
        paddle1 = new Paddle(0, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
        paddle2 = new Paddle(GAME_WIDTH-PADDLE_WIDTH,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,2);
    }
    public void paint(Graphics g){
        if (Math.abs(score.player1-score.player2) <= 4) {
            image = createImage(getWidth(), getHeight());
            graphics = image.getGraphics();
            draw(graphics);
            g.drawImage(image, 0, 0, this);
        }else{
            g.setColor(Color.red);
            Font myfont01 = new Font("Ink Free",Font.BOLD, 75);
            g.setFont(myfont01);
            FontMetrics metrics01 = getFontMetrics(g.getFont());
            g.drawString("Game Over!!",(GAME_WIDTH-metrics01.stringWidth("Game Over!!"))/2,GAME_HEIGHT/2);
        }
    }
    public void draw(Graphics g){
        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);
        button3.setVisible(true);
        this.validate();
    }
    public void move(){
        paddle1.move();
        paddle2.move();
        ball.move();
    }
    public void checkCollision(){
        //防止球越界
        if(ball.y <= 0){
            ball.setYDirection(-ball.yVelocity);
        }
        if(ball.y >= GAME_HEIGHT - BALL_DIAMETER){
            ball.setYDirection(-ball.yVelocity);
        }

        //防止击球板越界
        if(paddle1.y <= 0){
            paddle1.y=0;
        }
        if (paddle1.y >= (GAME_HEIGHT - PADDLE_HEIGHT)){
            paddle1.y = GAME_HEIGHT - PADDLE_HEIGHT;
        }
        if(paddle2.y <= 0){
            paddle2.y=0;
        }
        if (paddle2.y >= (GAME_HEIGHT - PADDLE_HEIGHT)){
            paddle2.y = GAME_HEIGHT - PADDLE_HEIGHT;
        }

        //击球板击中球
        int tempRandom = random.nextInt(2);
        if(ball.intersects(paddle1)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            if(tempRandom == 1) {
                ball.xVelocity++;
                if (ball.yVelocity > 0) {
                    ball.yVelocity++;
                } else {
                    ball.yVelocity--;
                }
            }
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }
        if (ball.intersects(paddle2)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            if(tempRandom == 1) {
                ball.xVelocity++;
                if (ball.yVelocity > 0) {
                    ball.yVelocity++;
                } else {
                    ball.yVelocity--;
                }
            }
            ball.setXDirection(-ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }
        if(ball.x <= 0){
            score.player2++;
            newPaddles();
            newBall();
        }
        if(ball.x >= GAME_WIDTH-BALL_DIAMETER){
            score.player1++;
            newPaddles();
            newBall();
        }
    }
    public void run(){
        //游戏循环
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000/amountOfTicks;
        double delta = 0;
        while(true){
//            if( Math.abs(score.player1 - score.player2) <= 4){
                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;
                if (delta >= 1) {
                    move();
                    checkCollision();
                    repaint();
                    delta--;
                }
        }
    }
    //内部类 行为监视器
    public class AL extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            paddle1.keyPressed(e);
            paddle2.keyPressed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            super.keyReleased(e);
            paddle1.keyReleased(e);
            paddle2.keyReleased(e);
        }
    }
}
