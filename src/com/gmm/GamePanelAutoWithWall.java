package com.gmm;

import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;
//import java.util.Timer; // 应该是swing的timer

//自动贪吃蛇需要重构代码，和之前的控制逻辑不一样。
public class GamePanelAutoWithWall extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 1080;
    static final int SCREEN_HEIGHT = (int) (SCREEN_WIDTH * (1.0 * 5 / 9));
    static final int UNIT_SIZE = 40;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 100;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6; //贪吃蛇初始6个块
    int appleEaten;
    int appleX;
    int appleY;
    char direction = 'R';//有R、U、D、L
    boolean running = false;
    Timer timer;
    Random random;
    static final int UNIT_NUM1 = SCREEN_HEIGHT / UNIT_SIZE;
    static final int UNIT_NUM2 = SCREEN_WIDTH / UNIT_SIZE;
    int[][] matrix = new int[UNIT_NUM2 + 10][UNIT_NUM2 + 10];
    JButton button3;
    Color appleColor;
    Color appleLastColor;
    char []someDirections = new char[]{'R','U','D','L'};
    final int virtualX[] = new int[GAME_UNITS];
    final int virtualY[] = new int[GAME_UNITS];

    GamePanelAutoWithWall() {
        //初始化
        button3 = new JButton("返回主界面 v");
        this.add(button3);
        button3.setVisible(false);
        appleColor = new Color(45, 180, 100);

        random = new Random();
        Dimension DIM = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.setPreferredSize(DIM);
        this.setBackground(Color.black);
        this.setFocusable(true);
//        startGame();
    }

    public void startGame() {
        appleColor = new Color(45, 180, 100);
        newApple();
        appleLastColor = new Color(45, 180, 100);
        this.appleEaten = 0;
        button3.setVisible(false);
        bodyParts = 6;
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        direction = 'R';
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            //高度除以单元个数，就得到了结果
            for (int i = 0; i < UNIT_NUM1; i++) {
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);//这里画线，画出单元格。
            }
            for (int i = 0; i < UNIT_NUM2; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
            }
            g.setColor(appleColor);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
            for (int i = 0; i < bodyParts; i++) {            //打印蛇的身子
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(appleLastColor);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            Font myfont = new Font("Ink Free", Font.BOLD, 40);
            g.setFont(myfont);
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Your score: " + appleEaten, (SCREEN_WIDTH - metrics.stringWidth("Your score: " + appleEaten)) / 2, g.getFont().getSize());
        } else {
            timer.stop();
            gameOver(g);
        }
    }

    public void newApple() {
        boolean flag = true;
        appleLastColor = appleColor;
        appleColor = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        while (flag) {
            flag = false;
            if (appleColor.getRed() + appleColor.getBlue() + appleColor.getGreen() < 100) {
                appleColor = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                flag = true;
            }
            if (Math.abs(appleColor.getRed() - appleLastColor.getRed()) < 50 && Math.abs(appleColor.getBlue() - appleLastColor.getBlue()) < 50 && Math.abs(appleColor.getGreen() - appleLastColor.getGreen()) < 50) {
                appleColor = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                flag = true;
            }
            System.out.println("苹果的RGB为:" + "R:" + appleColor.getRed() + " G:" + appleColor.getGreen() + " B:" + appleColor.getBlue());
        }
        appleX = random.nextInt((int) UNIT_NUM1) * UNIT_SIZE;
        appleY = random.nextInt((int) UNIT_NUM1) * UNIT_SIZE;
        flag = true;
        while (flag) {
            flag = false;
            for (int i = 0; i < bodyParts; i++) {
                if (appleX == x[i] && appleY == y[i]) {
                    flag = true;
                }
            }
            if (flag == true) {
                appleX = random.nextInt((int) UNIT_NUM1) * UNIT_SIZE;
                appleY = random.nextInt((int) UNIT_NUM1) * UNIT_SIZE;
            }
        }
    }
    public void virtualReset(){
        for (int i = bodyParts; i >= 0;i--){
            virtualX[i] = x[i];
            virtualY[i] = y[i];
        }
    }
    public void virtualMove(){
        for (int i = bodyParts; i > 0; i-- ){
            virtualX[i] = virtualX[i-1];
            virtualY[i] = virtualY[i-1];
        }
        switch (direction){
            case 'U':
                virtualY[0] = virtualY[0] - UNIT_SIZE;
                break;
            case 'D':
                virtualY[0] = virtualY[0] + UNIT_SIZE;
                break;
            case 'L':
                virtualX[0] = virtualX[0] - UNIT_SIZE;
                break;
            case 'R':
                virtualX[0] = virtualX[0] + UNIT_SIZE;
                break;
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
        if ((appleX == x[0]) && (appleY == y[0])) {
            bodyParts++;
            appleEaten++;
            newApple();
        }
    }

    public boolean virtualCheckCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if (virtualX[0] == virtualX[i] && virtualY[0] == virtualY[i]) {
                return true;
            }
        }
        //左越界
        if (virtualX[0] < 0) {
            return true;
        }
        //右越界
        if (virtualX[0] > SCREEN_WIDTH - 1) {
            return true;
        }
        //上越界
        if (virtualY[0] < 0) {
            return true;
        }
        //下越界
        if (virtualY[0] > SCREEN_HEIGHT - 1) {
            return true;
        }
        return false;
    }
    
    public void checkCollisions() {
        //检查是否头撞身体
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
//                gameOver();
            }
        }
        //左越界
        if (x[0] < 0) {
            running = false;
        }
        //右越界
        if (x[0] > SCREEN_WIDTH - 1) {
            running = false;
        }
        //上越界
        if (y[0] < 0) {
            running = false;
        }
        //下越界
        if (y[0] > SCREEN_HEIGHT - 1) {
            running = false;
        }
    }
    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        Font myfont01 = new Font("Ink Free", Font.BOLD, 75);
        g.setFont(myfont01);
        FontMetrics metrics01 = getFontMetrics(g.getFont());
        g.drawString("Game Over!!", (SCREEN_WIDTH - metrics01.stringWidth("Game Over!!")) / 2, SCREEN_HEIGHT / 2);

        g.setColor(Color.red);
        Font myfont02 = new Font("Ink Free", Font.BOLD, 40);
        g.setFont(myfont02);
        FontMetrics metrics02 = getFontMetrics(g.getFont());
        g.drawString("The final score is: " + appleEaten, (SCREEN_WIDTH - metrics02.stringWidth("The final score is: " + appleEaten)) / 2, g.getFont().getSize());

        button3.setVisible(true);
        this.validate();
    }


    @Override//这是自动添加的
    public void actionPerformed(ActionEvent e) {
        if (running == true) {
            int index= random.nextInt(4);
            direction = someDirections[index];
            boolean flag = true;
            while(flag){
                flag = false;
                virtualReset();
                virtualMove();
                if (virtualCheckCollisions()){
                    index= random.nextInt(4);
                    direction = someDirections[index];
                    flag = true;
                }
            }

            move();
            checkApple();
            checkCollisions();
        }
        repaint();

    }
}

