package com.gmm;

import com.sun.deploy.security.MSCryptoDSASignature;

import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.util.Timer; // 应该是swing的timer

//自动贪吃蛇需要重构代码，和之前的控制逻辑不一样。
public class GamePanelAutoWithWall extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 1080;
    static final int SCREEN_HEIGHT = (int) (SCREEN_WIDTH * (1.0 * 5 / 9));
    static final int UNIT_SIZE = 40;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 1;//100
    final int x[] = new int[GAME_UNITS];
    final int lastx[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    final int lasty[] = new int[GAME_UNITS];
    int bodyParts = 6; //贪吃蛇初始6个块
    int appleEaten;
    int appleX;
    int appleY;
    char direction = 'R';//有R、U、D、L
    boolean running = false;
    Timer timer;
    Random random;
    static final int UNIT_Y = SCREEN_HEIGHT / UNIT_SIZE;
    static final int UNIT_X = SCREEN_WIDTH / UNIT_SIZE;
    int[][] matrix = new int[UNIT_X + 10][UNIT_X + 10];
    int[][] Virtualmatrix = new int[UNIT_X + 10][UNIT_X + 10];
    JButton button3;
    Color appleColor;
    Color appleLastColor;
    char []someDirections = new char[]{'L','R','U','D'};
    final int virtualX[] = new int[GAME_UNITS];
    final int virtualY[] = new int[GAME_UNITS];
    int[][] visit= new int[UNIT_X + 10][UNIT_X + 10];
    int[][] VirtualVisit= new int[UNIT_X + 10][UNIT_X + 10];
    char [][]LastDirection = new char[GAME_UNITS][GAME_UNITS];
    int step;

    GamePanelAutoWithWall() {
        //初始化
        button3 = new JButton("返回主界面");
        this.add(button3);
        button3.setVisible(true);
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
        button3.setVisible(true);
        bodyParts = 6;
        for (int i = 0; i < bodyParts; i++) {
            lastx[i]= x[i] = 0;
            lasty[i] = y[i] = 0;
        }
        direction = 'R';
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();

        for (int i = 0; i < UNIT_X ;i++){
            for (int j = 0; j < UNIT_Y;j++){
                matrix[i][j] = i+j;
            }
        }
        matrix[0][0] = -1;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void clearVirtualVisitAndStep(){
        for (int i = 0 ; i < UNIT_X ;i++){
            for (int j = 0; j< UNIT_Y ;j++){
                VirtualVisit[i][j] = 0;
            }
        }
        step = 0;
        for (int i = 0 ; i < UNIT_X ;i++){
            for (int j = 0; j< UNIT_Y ;j++){
                Virtualmatrix[i][j] = -1;
            }
        }
        for (int i = 1 ; i< bodyParts;i++){
            VirtualVisit[virtualX[i]/UNIT_SIZE][virtualY[i]/UNIT_SIZE] = 1;
        }
    }
    public void getVirtualMatrix(int x, int y){
        if (x < 0 || x >= UNIT_X || y < 0 || y >= UNIT_Y){
            return;
        }
        VirtualVisit[x][y] = 1;
        boolean flag = true;
        Virtualmatrix[x][y] = step;
        //左边
        Queue<xy> queue = new LinkedList<xy>();
        xy temp = new xy(x,y,step);
        queue.offer(temp);
        while(!queue.isEmpty()){
            temp = queue.poll();
            int xx = temp.x;
            int yy = temp.y;
            step = temp.step;
            if ( xx - 1 >= 0 && VirtualVisit[xx-1][yy] == 0){
                xy t = new xy(xx-1,yy,step+1);
                queue.offer(t);
                Virtualmatrix[xx-1][yy] = step+1;
                VirtualVisit[xx-1][yy] = 1;
            }
            if (xx + 1 < UNIT_X && VirtualVisit[xx+1][yy] == 0){
                xy t = new xy(xx+1,yy,step+1);
                queue.offer(t);
                VirtualVisit[xx+1][yy] = 1;
                Virtualmatrix[xx+1][yy] = step+1;
            }
            if (yy+1 < UNIT_Y && VirtualVisit[xx][yy+1] == 0 ){
                xy t = new xy(xx,yy+1,step+1);
                queue.offer(t);
                VirtualVisit[xx][yy+1] = 1;
                Virtualmatrix[xx][yy+1] = step+1;
            }
            if (yy-1 >= 0 && VirtualVisit[xx][yy-1] ==0){
                xy t = new xy(xx,yy-1,step+1);
                queue.offer(t);
                VirtualVisit[xx][yy-1] = 1;
                Virtualmatrix[xx][yy-1] = step+1;
            }
        }
        return;
    }

    public void draw(Graphics g) {
        if (running) {
            //高度除以单元个数，就得到了结果
            for (int i = 0; i < UNIT_Y; i++) {
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);//这里画线，画出单元格。
            }
            for (int i = 0; i < UNIT_X; i++) {
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
            for (int i = 0; i < bodyParts;i++){
                lasty[i] = y[i];
                lastx[i] = x[i];
            }
            g.setColor(Color.red);
            Font myfont = new Font("Ink Free", Font.BOLD, 40);
            g.setFont(myfont);
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Your score: " + appleEaten, (SCREEN_WIDTH - metrics.stringWidth("Your score: " + appleEaten)) / 2, g.getFont().getSize()+30);
        } else {
            for (int i = 0; i < UNIT_Y; i++) {
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);//这里画线，画出单元格。
            }
            for (int i = 0; i < UNIT_X; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
            }
            g.setColor(appleColor);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
            for (int i = 0; i < bodyParts; i++) {            //打印蛇的身子
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(lastx[i], lasty[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(appleLastColor);
                    g.fillRect(lastx[i], lasty[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            Font myfont = new Font("Ink Free", Font.BOLD, 40);
            g.setFont(myfont);
            FontMetrics metrics = getFontMetrics(g.getFont());
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
        appleX = random.nextInt((int) UNIT_Y) * UNIT_SIZE;
        appleY = random.nextInt((int) UNIT_Y) * UNIT_SIZE;
        flag = true;
        while (flag) {
            flag = false;
            for (int i = 0; i < bodyParts; i++) {
                if (appleX == x[i] && appleY == y[i]) {
                    flag = true;
                }
            }
            if (flag == true) {
                appleX = random.nextInt((int) UNIT_Y) * UNIT_SIZE;
                appleY = random.nextInt((int) UNIT_Y) * UNIT_SIZE;
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
                System.out.println("11");
            }
        }
        //左越界
        if (x[0] < 0) {
            running = false;
            System.out.println("22");
        }
        //右越界
        if (x[0] > SCREEN_WIDTH - 1) {
            running = false;
            System.out.println("33");
        }
        //上越界
        if (y[0] < 0) {
            running = false;
            System.out.println("44");
        }
        //下越界
        if (y[0] > SCREEN_HEIGHT - 1) {
            running = false;
            System.out.println("55");
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
        g.drawString("The final score is: " + appleEaten, (SCREEN_WIDTH - metrics02.stringWidth("The final score is: " + appleEaten)) / 2, g.getFont().getSize()+30);

        button3.setVisible(true);
        this.validate();
        System.out.println("能力不足，我的贪吃蛇失败了");
    }
    public void clearVisitAndStep(){
        for (int i = 0 ; i < UNIT_X ;i++){
            for (int j = 0; j< UNIT_Y ;j++){
                visit[i][j] = 0;
            }
        }
        step = 0;
        for (int i = 0 ; i < UNIT_X ;i++){
            for (int j = 0; j< UNIT_Y ;j++){
                matrix[i][j] = -1;
            }
        }
        for (int i = 1 ; i< bodyParts;i++){
            visit[x[i]/UNIT_SIZE][y[i]/UNIT_SIZE] = 1;
        }
    }
    public class xy{
        int x;
        int y;
        int step;
        xy(int x,int y,int step){
            this.x = x;
            this.y = y;
            this.step = step;
        }
    }
    public void getMatrix(int x, int y){
        if (x < 0 || x >= UNIT_X || y < 0 || y >= UNIT_Y){
            return;
        }
        visit[x][y] = 1;
        boolean flag = true;
        matrix[x][y] = step;
        //左边
        Queue<xy> queue = new LinkedList<xy>();
        xy temp = new xy(x,y,step);
        queue.offer(temp);
        while(!queue.isEmpty()){
            temp = queue.poll();
            int xx = temp.x;
            int yy = temp.y;
            step = temp.step;
            if ( xx - 1 >= 0 && visit[xx-1][yy] == 0){
                xy t = new xy(xx-1,yy,step+1);
                matrix[xx-1][yy] = step+1;
                queue.offer(t);
                visit[xx-1][yy] = 1;
            }
            if (xx + 1 < UNIT_X && visit[xx+1][yy] == 0){
                xy t = new xy(xx+1,yy,step+1);
                matrix[xx+1][yy] = step+1;
                queue.offer(t);
                visit[xx+1][yy] = 1;
            }
            if (yy+1 < UNIT_Y && visit[xx][yy+1] == 0 ){
                xy t = new xy(xx,yy+1,step+1);
                queue.offer(t);
                matrix[xx][yy+1] = step+1;
                visit[xx][yy+1] = 1;
            }
            if (yy-1 >= 0 && visit[xx][yy-1] ==0){
                xy t = new xy(xx,yy-1,step+1);
                queue.offer(t);
                matrix[xx][yy-1] = step+1;
                visit[xx][yy-1] = 1;
            }
        }

        return;
    }
    public boolean findTail() {
        int xx = (x[bodyParts - 1]) / UNIT_SIZE;
        int yy = (y[bodyParts - 1]) / UNIT_SIZE;
        int sum = 0;
        boolean flag = false;
        if (xx - 1 >= 0) {
            if (Virtualmatrix[xx - 1][yy] != -1) {
                flag = true;
            }
        }
        if(xx+1 < UNIT_X){
            if (Virtualmatrix[xx+1][yy] != -1){
                flag = true;
            }
        }
        if (yy - 1 >= 0){
            if (Virtualmatrix[xx][yy-1] != -1){
                flag = true;
            }
        }
        if (yy+1 < UNIT_Y){
            if (Virtualmatrix[xx][yy+1] != -1){
                flag = true;
            }
        }
        return  flag;
    }
    @Override//这是自动添加的
    public void actionPerformed(ActionEvent e) {
        if (running == true) {
            clearVisitAndStep();
            getMatrix(x[0]/UNIT_SIZE,y[0]/UNIT_SIZE);

            if (matrix[appleX/UNIT_SIZE][appleY/UNIT_SIZE] > 0){
                int maxi = -1;
                int mini = -1;
                int be = -1;
                int ci = -1;
                int distance = matrix[appleX/UNIT_SIZE][appleY/UNIT_SIZE];
                for (int i = 0; i < 4;i++){
                    direction = someDirections[i];
                    virtualReset();
                    virtualMove();
                    if (virtualCheckCollisions() == false){
                        clearVirtualVisitAndStep();
                        getVirtualMatrix(virtualX[0]/UNIT_SIZE,virtualY[0]/UNIT_SIZE);
                        if (Virtualmatrix[appleX/UNIT_SIZE][appleY/UNIT_SIZE] == -1){
                            System.out.println("进入第一个");
                            if (findTail()){
                                maxi = i;
                                System.out.println("findtail true");
                            }else{
                                ci = i;
                            }
                        }else if (Virtualmatrix[appleX/UNIT_SIZE][appleY/UNIT_SIZE] <= distance){
                            System.out.println("进入第二个");;
                            if (findTail()  ) {
                                if ( someDirections[i]!=LastDirection[x[0]/UNIT_SIZE][y[0]/UNIT_SIZE]) {
                                    distance = Virtualmatrix[appleX / UNIT_SIZE][appleY / UNIT_SIZE];
                                    maxi = i;
                                    System.out.println("distance true");
                                }else{
                                    mini = i;
                                }
                            }else{
                                System.out.println("没有找到尾巴");
                                System.out.println("尾巴坐标 x:"+(x[bodyParts-1])/UNIT_SIZE+" y:"+(y[bodyParts-1])/UNIT_SIZE );
                                be = i;
                            }
                        }
                    }
                }
                if (maxi == -1){
                    maxi = mini;
                }
                if (maxi == -1){
                    maxi = be;
                }
                if (maxi == -1){
                    maxi = ci;
                }
                System.out.println("尾巴坐标 x:"+(x[bodyParts-1])/UNIT_SIZE+" y:"+(y[bodyParts-1])/UNIT_SIZE );
                System.out.println("苹果坐标"+" x:"+appleX/UNIT_SIZE + " y:"+appleY/UNIT_SIZE);
                System.out.println("头坐标"+" x:" + x[0]/UNIT_SIZE + " y:" + y[0]/UNIT_SIZE);
                System.out.println(maxi+"\n");
                direction = someDirections[maxi];
                System.out.println("进入是");
            }else{
                System.out.println("进入否");
                int maxi = -1;
                int mini = -1;
                for(int i = 0; i < 4;i++){
                    direction = someDirections[i];
                    virtualReset();
                    virtualMove();
                    if (virtualCheckCollisions()==false){
                        clearVirtualVisitAndStep();
                        getVirtualMatrix(virtualX[0]/UNIT_SIZE,virtualY[0]/UNIT_SIZE);
                        if (findTail()){
                            maxi = i;
                        }else {
                            mini = i;
                        }
                    }
                }
                if (maxi != -1){
                    direction = someDirections[maxi];
                }else if(mini!=-1){
                    direction = someDirections[mini];
                }else{
                    direction = 'R';
                }
            }
            int index;
            boolean flag = true;
            int number = 0;
            while(flag){
                number++;
                flag = false;
                virtualReset();
                virtualMove();
                if (virtualCheckCollisions()){
                    index= random.nextInt(4);
                    direction = someDirections[index];
                    flag = true;
                }
                if (number>200){
                    break;
                }
            }
            LastDirection[x[0]/UNIT_SIZE][y[0]/UNIT_SIZE] = direction;
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }



}

