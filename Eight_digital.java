import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

class panel extends JPanel implements ActionListener {
    int a[][]; //九宫格
    public int blank_position[]; //空格的位置
    public JButton[] cells; //九宫格对应的按钮
    int go[][] = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; //可移动的方向（上下左右）

    public panel() {
        a = new int[3][3];
        blank_position = new int[2];
        this.setLayout(new GridLayout(3, 3));
//        this.setPreferredSize(new Dimension(300, 300));
        cells = new JButton[9];
        for (int i = 0; i < 9; i++) {
            if (i == 8)
                cells[i] = new JButton(" ");
            else {
                cells[i] = new JButton(String.valueOf(i + 1));
            }
            cells[i].addActionListener(this);
            this.add(cells[i]);
        }
//        for(int i=0;i<3;i++){
//            for(int j=0;j<3;j++) {
//                a[i][j] = i * 3 + j + 1;
//                System.out.println(a[i][j]);
//            }
//        }
        //初始空格坐标（2，2）
        blank_position[0] = 2;
        blank_position[1] = 2;
    }

    public boolean judge_move(int pressButton) { //判断当前点击的按钮能否移动（即周围是否有空格）
        int loc_x = pressButton / 3;
        int loc_y = pressButton - loc_x * 3;
        for (int i = 0; i < 4; i++) {
            int x = loc_x + go[i][0];
            int y = loc_y + go[i][1];
            if (x < 0 || x >= 3 || y < 0 || y >= 3 || x != blank_position[0] || y != blank_position[1]) {
                continue;
            }
            return true;
        }
        return false;
    }

    public void actionPerformed(ActionEvent e) { //为九宫格按钮添加点击事件
        for (int i = 0; i < 9; i++) {
            if (e.getSource() == cells[i]) {
//                System.out.println(i + 1);
//                System.out.println(judge_move(i));
                if (judge_move(i)) {
                    String s = cells[i].getText();
                    cells[i].setText(" ");
                    cells[blank_position[0] * 3 + blank_position[1]].setText(s);
                    blank_position[0] = i / 3;
                    blank_position[1] = i - blank_position[0] * 3;
                }
            }
        }
    }

    public int[][] getgrid() { //获取当前的九宫格状态
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String s = cells[i * 3 + j].getText();
                if (s == " ") {
                    a[i][j] = 0;
                } else
                    a[i][j] = Integer.parseInt(s);
            }
        }
        return a;
    }
}

class func extends JPanel implements ActionListener {
    panel first, second;  //first是左边的初始状态，second是右边的目标状态
    JButton bu1, bu2, bu3, bu4, bu5, bu6; //6个功能按钮
    int last_grid[][]; //左边上次打乱的结果 这是为了比较三个算法在同一初始状态下恢复到目标状态要用到的
    public func(panel init, panel goal) {
        first = init;
        second = goal;
        int[][] t = first.getgrid();
        last_grid = new int[3][3];
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                last_grid[i][j] = t[i][j];
            }
        }

        this.setLayout(new GridLayout(6, 1));
        bu1 = new JButton("打乱初始");
        bu1.addActionListener(this);

        bu2 = new JButton("打乱目标");
        bu2.addActionListener(this);

        bu3 = new JButton("深度优先搜索");
        bu3.addActionListener(this);

        bu4 = new JButton("广度优先搜索");
        bu4.addActionListener(this);

        bu5 = new JButton("A*搜索");
        bu5.addActionListener(this);

        bu6 = new JButton("恢复初始");
        bu6.addActionListener(this);

        this.add(bu1);
        this.add(bu2);
        this.add(bu3);
        this.add(bu4);
        this.add(bu5);
        this.add(bu6);
//        this.setPreferredSize(new Dimension(300, 300));
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bu1) { //随机打乱初始状态并保存为last_grid;
            int[][] t = first.getgrid();
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    last_grid[i][j] = t[i][j];
                }
            }
            Thread thread = new Thread() {
                public void run() {
                    Random ran = new Random();
                    for (int i = 0; i < 100; i++) {
                        synchronized (this) {
                            first.cells[ran.nextInt(9)].doClick(100);
                        }
                    }
                }
            };
            thread.start();
        }
        if (e.getSource() == bu2) { //随机打乱目标状态
            Thread thread = new Thread() {
                public void run() {
                    Random ran = new Random();
                    for (int i = 0; i < 100; i++) {
                        synchronized (this) {
                            second.cells[ran.nextInt(9)].doClick(100);
                        }
                    }
                }
            };
            thread.start();
        }
        if (e.getSource() == bu3 ) { //dfs算法
            Thread thread = new Thread() {
                public void run() {
                    dfs_solve dfs = new dfs_solve(first.getgrid(), second.getgrid());
                    String way = dfs.getMove();
                    String s = "";
                    for (int i = 0; i < way.length(); i++) {
                        synchronized (this) {
                            if (way.charAt(i) == '1') {
                                int press = first.blank_position[0] * 3 + first.blank_position[1] - 1;
                                first.cells[press].doClick(100);
                                s += "左";
                            }
                            if (way.charAt(i) == '2') {
                                int press = first.blank_position[0] * 3 + first.blank_position[1] + 1;
                                first.cells[press].doClick(100);
                                s += "右";

                            }
                            if (way.charAt(i) == '3') {
                                int press = (first.blank_position[0] - 1) * 3 + first.blank_position[1];
                                first.cells[press].doClick(100);
                                s += "上";

                            }
                            if (way.charAt(i) == '4') {
                                int press = (first.blank_position[0] + 1) * 3 + first.blank_position[1];
                                first.cells[press].doClick(100);
                                s += "下";
                            }
                        }
                    }
                    String msg="dfs用时(ms)："+String.valueOf(dfs.time)+"\n操作："+s;
                    JOptionPane.showMessageDialog(null, msg);
                }
            };
            thread.start();
        }
        //下面是两个算法按钮，你们可以照着我的写，建一个bfs class和A* class

        if (e.getSource() == bu4 ){//bfs算法

        }
        if (e.getSource() == bu5 ){//A*算法

        }
        if (e.getSource() == bu6 ) { //回退到上一个初始状态
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    System.out.print(last_grid[i][j]);
                    System.out.print(" ");
                    int t = last_grid[i][j];
                    if (t == 0) {
                        first.cells[i * 3 + j].setText(" ");
                        first.blank_position[0] = i;
                        first.blank_position[1] = j;
                    }
                    else
                        first.cells[i * 3 + j].setText(String.valueOf(t));
                }
                System.out.print("\n");
            }
            System.out.print("\n");
        }
    }
}
class dfs_solve{
    int blank_x;
    int blank_y;
    String  goal;
    int[][] start;
    public long time;
    int go[][] = {{0,1},{0,-1},{1,0},{-1,0}};
    private static final int left = 1;
    private static final int right = 2;
    private static final int up = 3;
    private static final int down = 4;
    private List<Integer> moveArr = new LinkedList<>();
    private Set<String> statusSet = new HashSet<>();
    private boolean canMove(int dir){
        switch (dir){
            case left:
                return blank_y>0;
            case right:
                return blank_y<2;
            case up:
                return blank_x>0;
            case down:
                return blank_x<2;
        }
        return false;
    }
    private void move(int dir){
        int t;
        switch (dir){
            case left:
                t = start[blank_x][blank_y-1];
                start[blank_x][blank_y-1] = 0;
                start[blank_x][blank_y] = t;
                blank_y -= 1;
                break;
            case right:
                t = start[blank_x][blank_y+1];
                start[blank_x][blank_y+1] = 0;
                start[blank_x][blank_y] = t;
                blank_y += 1;
                break;
            case up:
                t = start[blank_x-1][blank_y];
                start[blank_x-1][blank_y] = 0;
                start[blank_x][blank_y] = t;
                blank_x -= 1;
                break;
            case down:
                t = start[blank_x+1][blank_y];
                start[blank_x+1][blank_y] = 0;
                start[blank_x][blank_y] = t;
                blank_x += 1;
                break;
        }
        moveArr.add(dir);
    }
    private void move_back(int dir){ // 撤销更改
        int t;
        switch (dir){
            case left:
                t = start[blank_x][blank_y+1];
                start[blank_x][blank_y+1] = 0;
                start[blank_x][blank_y] = t;
                blank_y += 1;
                break;
            case right:
                t = start[blank_x][blank_y-1];
                start[blank_x][blank_y-1] = 0;
                start[blank_x][blank_y] = t;
                blank_y -= 1;
                break;
            case up:
                t = start[blank_x+1][blank_y];
                start[blank_x+1][blank_y] = 0;
                start[blank_x][blank_y] = t;
                blank_x += 1;
                break;
            case down:
                t = start[blank_x-1][blank_y];
                start[blank_x-1][blank_y] = 0;
                start[blank_x][blank_y] = t;
                blank_x -= 1;
                break;
        }
        moveArr.remove(moveArr.size()-1);
    }
    private String getStatus(){
        String s = "";
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                s += String.valueOf(start[i][j]);
            }
        }
        return s;
    }
    private boolean dfs(int dir){
        if(moveArr.size()>=50)
            return false;
        if(canMove(dir)){
            move(dir);
            String status = getStatus();
            if(status.equals(goal))
                return true;
            if (statusSet.contains(status)) {
                move_back(dir);
                return false;
            }
            statusSet.add(status);
            boolean dfsok = dfs(left) || dfs(right) || dfs(up) || dfs(down);
            if(dfsok)
                return true;
            move_back(dir);
            return false;
        }
        return false;
    }
    private boolean solve(){
        String status = getStatus();
        statusSet.add(status);
        return dfs(left) || dfs(right) || dfs(up) || dfs(down);
    }
    public dfs_solve(int[][] first,int[][] second){
        start = new int[3][3];
        goal = "";
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                if(first[i][j]==0){
                    blank_x=i;
                    blank_y=j;
                }
                start[i][j] = first[i][j];
                goal += String.valueOf(second[i][j]);
            }
        }
        long startTime =  System.currentTimeMillis();
        if(solve()){
            System.out.println("成功");
            System.out.println(getMove());
        }
        else{
            System.out.println("当前dfs深度下无法恢复成目标状态");
        }
        long endTime =  System.currentTimeMillis();
        time = endTime - startTime;
    }
    public String getMove(){
        String way = "";
        for(int i=0;i<moveArr.size();i++){
            way += Integer.toString(moveArr.get(i));
        }
        return way;
    }
}
public class Eight_digital extends JFrame {
    public Eight_digital(String title) {
        this.setTitle(title);
        this.setBounds(200, 200, 1000, 300);
        this.setLayout(new GridLayout(1, 3, 50, 0));
        panel init = new panel();
        panel goal = new panel();
        func function = new func(init, goal);
        this.add(init);
        this.add(goal);
        this.add(function);
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {
        new Eight_digital("8数码");
    }
}
