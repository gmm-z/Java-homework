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

public class GamePanelMaze extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 1080;
    static final int SCREEN_HEIGHT = (int)(SCREEN_WIDTH *(1.0*5/9));
    static final int UNIT_SIZE =24;//24
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 100;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int flag;
    char direction = 'R';//有R、U、D、L
    boolean running = false;
    Timer timer;
    Random random;
    static final int UNIT_NUM1 = SCREEN_HEIGHT/UNIT_SIZE;
    static final int UNIT_NUM2 = SCREEN_WIDTH/UNIT_SIZE;
    JButton button3;
    JButton button4;
    int maze[][] = new int[UNIT_NUM2][UNIT_NUM1];
    int visit[][] = new int[UNIT_NUM2][UNIT_NUM1];
    int ballx;
    int bally;
    int path[][] = new int[UNIT_NUM2][UNIT_NUM1];
    int R;

    GamePanelMaze(){
        //初始化
        button3 = new JButton("返回主界面");
        this.add(button3);
        button3.setVisible(true);
        button4 = new JButton("重新生成");
        this.add(button4);
        button4.setVisible(true);

        random = new Random();
        Dimension DIM = new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT);
        this.setPreferredSize(DIM);
        this.setBackground(Color.black);
        this.setFocusable(true);
        MykeyAdapter keyAdapter = new MykeyAdapter();
        this.addKeyListener(keyAdapter);
    }
    public void startGame(){
        R = 6;
        button3.setVisible(true);
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
        flag = 0;
        for (int i =0 ; i< UNIT_NUM2;i++){
            for (int j = 0 ; j< UNIT_NUM1;j++){
                visit[i][j] = 0;
            }
        }
        for (int i = 0 ; i < UNIT_NUM2;i++){
            for (int j = 0 ; j< UNIT_NUM1 ;j++){
                if ( (i+1) % 2 == 0 && (j+1)%2 == 0){
                    maze[i][j] = 1;
                }else{
                    maze[i][j] = 0;
                }
            }
        }
        createMaze(1,1);
        ballx = 1  ;
        bally = 1 ;
        for (int i = 0 ; i < UNIT_NUM2; i++){
            for (int j = 0;  j < UNIT_NUM1;j++){
                path[i][j] = 0;
            }
        }
    }
    public void createMaze(int x,int y){
        visit[x][y] = 1;
        maze[x][y] = 2;//道路
        int num = 0;
        int direct = random.nextInt(4); //0 是北边 ,顺时针
        boolean some = true;
        while (some){
            num++;
            some = false;
            switch (direct){
                case 0:
                    if (y-2>=0 &&visit[x][y-2]==0){
                        createMaze(x,y-2);
                        maze[x][y-1] = 2;
                    }
                    break;
                case 1:
                    if (x+2 < UNIT_NUM2 &&visit[x+2][y]==0){
                        createMaze(x+2,y);
                        maze[x+1][y] = 2;
                    }
                    break;
                case 2:
                    if (y+2< UNIT_NUM1 &&visit[x][y+2]==0){
                        createMaze(x,y+2);
                        maze[x][y+1] = 2;
                    }
                    break;
                case 3:
                    if (x-2>=0 &&visit[x-2][y]==0){
                        createMaze(x-2,y);
                        maze[x-1][y] = 2;
                    }
                    break;
            }
            if (some = true){
                direct = random.nextInt(4);
            }
            if (num > 100){
                break;
            }
        }
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        if(running) {
                for (int i = 0; i < UNIT_NUM1; i++) {
                    g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
                }
                for (int i = 0; i < UNIT_NUM2; i++) {
                    g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                }
                g.setColor(Color.red);
                for (int i = 0; i < UNIT_NUM2; i++) {
                    for (int j = 0; j < UNIT_NUM1; j++) {
                        if (maze[i][j] == 0) {
                            g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                        }
                    }
                }
                g.setColor(Color.blue);
                g.fillRect(0,1*UNIT_SIZE,UNIT_SIZE,UNIT_SIZE);
                g.fillRect((UNIT_NUM2-1)*UNIT_SIZE,(UNIT_NUM1-2)*UNIT_SIZE,UNIT_SIZE,UNIT_SIZE);
                g.setColor(Color.GREEN);
                g.fillOval(ballx * UNIT_SIZE, bally * UNIT_SIZE, UNIT_SIZE,UNIT_SIZE);
                for (int i = 0; i < UNIT_NUM2;i++){
                    for (int j = 0; j<UNIT_NUM1;j++){
                        if (path[i][j] == 1 ){
                            g.setColor(Color.green);
                            g.fillOval( i * UNIT_SIZE + (UNIT_SIZE/2 - R/2), j*UNIT_SIZE +(UNIT_SIZE/2-R/2),R,R);
                        }else if (path[i][j] ==2){
                            g.setColor(Color.yellow);
                            g.fillOval( i * UNIT_SIZE + (UNIT_SIZE/2 - R/2), j*UNIT_SIZE +(UNIT_SIZE/2-R/2),R,R);
                        }
                    }
                }
            }
        else{
            for (int i = 0; i < UNIT_NUM1; i++) {
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            for (int i = 0; i < UNIT_NUM2; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
            }
            g.setColor(Color.red);
            for (int i = 0; i < UNIT_NUM2; i++) {
                for (int j = 0; j < UNIT_NUM1; j++) {
                    if (maze[i][j] == 0) {
                        g.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    }
                }
            }
            for (int i = 0; i < UNIT_NUM2;i++){
                for (int j = 0; j<UNIT_NUM1;j++){
                    if (path[i][j] == 1 ){
                        g.setColor(Color.green);
                        g.fillOval( i * UNIT_SIZE + (UNIT_SIZE/2 - R/2), j*UNIT_SIZE +(UNIT_SIZE/2-R/2),R,R);
                    }else if (path[i][j] ==2){
                        g.setColor(Color.yellow);
                        g.fillOval( i * UNIT_SIZE + (UNIT_SIZE/2 - R/2), j*UNIT_SIZE +(UNIT_SIZE/2-R/2),R,R);
                    }
                }
            }
            g.setColor(Color.blue);
            g.fillRect(0,1*UNIT_SIZE,UNIT_SIZE,UNIT_SIZE);
            g.fillRect((UNIT_NUM2-1)*UNIT_SIZE,(UNIT_NUM1-2)*UNIT_SIZE,UNIT_SIZE,UNIT_SIZE);
            g.setColor(Color.GREEN);
            g.fillOval(ballx * UNIT_SIZE, bally * UNIT_SIZE, UNIT_SIZE,UNIT_SIZE);
                timer.stop();
                gameOver(g);
            }
        }

    public void checkCollisions(){
        if ( ballx == UNIT_NUM2-2 && bally == UNIT_NUM1-2){
            running = false;
        }
    }
    public void gameOver(Graphics g){
        g.setColor(Color.white);
        Font myfont01 = new Font("Ink Free",Font.BOLD, 75);
        g.setFont(myfont01);
        FontMetrics metrics01 = getFontMetrics(g.getFont());
        g.drawString("Congratulations!!",(SCREEN_WIDTH-metrics01.stringWidth("Game Over!!"))/2,SCREEN_HEIGHT/2);

        button3.setVisible(true);
        this.validate();
    }
    @Override//这是自动添加的
    public void actionPerformed(ActionEvent e) {
        if(running == true){
            checkCollisions();
        }
        repaint();
    }
    public class MykeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            switch (e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if (maze[ballx-1][bally] != 0){
                        if (path[ballx][bally] == 1){
                            path[ballx][bally] = 2;
                        }else if (path[ballx][bally]==2){
                            path[ballx][bally] = 1;
                        }else{
                            path[ballx][bally] = 1;
                        }
                        ballx -= 1;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (maze[ballx+1][bally]!=0){
                        if (path[ballx][bally] == 1){
                            path[ballx][bally] = 2;
                        }else if (path[ballx][bally]==2){
                            path[ballx][bally] = 1;
                        }else{
                            path[ballx][bally] = 1;
                        }
                        ballx +=1;
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (maze[ballx][bally-1]!=0){
                        if (path[ballx][bally] == 1){
                            path[ballx][bally] = 2;
                        }else if (path[ballx][bally]==2){
                            path[ballx][bally] = 1;
                        }else{
                            path[ballx][bally] = 1;
                        }
                        bally-=1;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (maze[ballx][bally+1]!=0){
                        if (path[ballx][bally] == 1){
                            path[ballx][bally] = 2;
                        }else if (path[ballx][bally]==2){
                            path[ballx][bally] = 1;
                        }else{
                            path[ballx][bally] = 1;
                        }
                        bally+=1;
                    }
                    break;
            }
        }
    }
}
