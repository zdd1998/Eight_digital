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
                            int num = ran.nextInt(9);
                            if(first.judge_move(num))
                                first.cells[num].doClick(100);
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
                            int num = ran.nextInt(9);
                            if(second.judge_move(num))
                                second.cells[num].doClick(100);
                        }
                    }
                }
            };
            thread.start();
        }
        if (e.getSource() == bu3 ) { //dfs算法
            Thread thread = new Thread() {
                public void run() {
                    int[][] t = first.getgrid();
                    for(int i=0;i<3;i++){
                        for(int j=0;j<3;j++){
                            last_grid[i][j] = t[i][j];
                        }
                    }
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
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e1) {
//                                e1.printStackTrace();
//                            }
                        }
                    }
                    String msg="dfs用时(ms)："+dfs.time+"\n操作："+way.length()+"步\n"+s;
                    JOptionPane.showMessageDialog(null, msg);
                }
            };
            thread.start();
        }
        //下面是两个算法按钮，你们可以照着我的写，建一个bfs class和A* class

        if (e.getSource() == bu4 ){//bfs算法
            Thread thread = new Thread(){
                public void run(){
                    int[][] t = first.getgrid();
                    for(int i=0;i<3;i++){
                        for(int j=0;j<3;j++){
                            last_grid[i][j] = t[i][j];
                        }
                    }
                    bfs_solve bfs = new bfs_solve(first.getgrid(),second.getgrid());
                    ArrayList<Integer> way = bfs.path;
                    String s = "";
                    for (int i = way.size()-1; i >= 0; i--) {
                        synchronized (this) {
                            if (way.get(i) == 1) {
                                int press = first.blank_position[0] * 3 + first.blank_position[1] - 1;
                                first.cells[press].doClick(100);
                                s += "左";
                            }
                            if (way.get(i) == 2) {
                                int press = first.blank_position[0] * 3 + first.blank_position[1] + 1;
                                first.cells[press].doClick(100);
                                s += "右";

                            }
                            if (way.get(i) == 3) {
                                int press = (first.blank_position[0] - 1) * 3 + first.blank_position[1];
                                first.cells[press].doClick(100);
                                s += "上";

                            }
                            if (way.get(i) == 4) {
                                int press = (first.blank_position[0] + 1) * 3 + first.blank_position[1];
                                first.cells[press].doClick(100);
                                s += "下";
                            }
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e1) {
//                                e1.printStackTrace();
//                            }
                        }
                    }
                    String msg="bfs用时(ms)："+bfs.time+"\n操作："+way.size()+"步\n"+s;
                    JOptionPane.showMessageDialog(null, msg);
                }
            };
            thread.start();
        }
        if (e.getSource() == bu5 ){//A*算法
            Thread thread = new Thread() {
                public void run() {
                    int[][] t = first.getgrid();
                    for(int i=0;i<3;i++){
                        for(int j=0;j<3;j++){
                            last_grid[i][j] = t[i][j];
                        }
                    }
                    Astar_solve As = new Astar_solve(first.getgrid(),second.getgrid());
                    ArrayList<Integer> way = As.path;
                    String s = "";
                    for (int i = way.size()-1; i >= 0; i--) {
                        synchronized (this) {
                            if (way.get(i) == 1) {
                                int press = first.blank_position[0] * 3 + first.blank_position[1] - 1;
                                first.cells[press].doClick(100);
                                s += "左";
                            }
                            if (way.get(i) == 2) {
                                int press = first.blank_position[0] * 3 + first.blank_position[1] + 1;
                                first.cells[press].doClick(100);
                                s += "右";

                            }
                            if (way.get(i) == 3) {
                                int press = (first.blank_position[0] - 1) * 3 + first.blank_position[1];
                                first.cells[press].doClick(100);
                                s += "上";

                            }
                            if (way.get(i) == 4) {
                                int press = (first.blank_position[0] + 1) * 3 + first.blank_position[1];
                                first.cells[press].doClick(100);
                                s += "下";
                            }
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e1) {
//                                e1.printStackTrace();
//                            }
                        }
                    }
                    String msg="A*用时(ms)："+As.time+"\n操作："+way.size()+"步\n"+s;
                    JOptionPane.showMessageDialog(null, msg);
                }
            };
            thread.start();
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
        if(status.equals(goal))
            return true;
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
class puzzle_bfs{
    puzzle_bfs parent;
    int[][] data;
    puzzle_bfs(int[][] a){
        data = a;
    }
}
class bfs_solve{
    private final int[][] go= new int[][]{{0,-1},{0,1},{-1,0},{1,0}};
    public long time;
    public ArrayList<Integer> path;

    private String encode(int[][] a){
        String s="";
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                s+=Integer.toString(a[i][j]);
            }
        }
        return s;
    }
    public bfs_solve(int start[][],int goal[][]){
        path = new ArrayList<>();
        Set<String> state = new HashSet<>();
        Queue<puzzle_bfs> queue = new LinkedList<>();
        puzzle_bfs pz = new puzzle_bfs(start);
        pz.parent = null;
        queue.offer(pz);
        puzzle_bfs end=null;
        state.add(encode(pz.data));
        long startTime =  System.currentTimeMillis();
        while(!queue.isEmpty()){

            System.out.println(state.size());
            puzzle_bfs ns = queue.poll();
            end = ns;
            if(check(ns.data,goal)){
                break;
            }
            for(int i=0;i<4;i++){
                int[][] tmp = new int[3][3];
                int old_x = 0;
                int old_y = 0;
                for(int m=0;m<3;m++){
                    for(int n=0;n<3;n++) {
                        tmp[m][n] = ns.data[m][n];
                        if(tmp[m][n] == 0){
                            old_x = m;
                            old_y = n;
                        }
                    }
                }
                int new_x = old_x+go[i][0];
                int new_y = old_y+go[i][1];
                if(new_x<0||new_x>2||new_y<0||new_y>2)
                    continue;
                swap(tmp,old_x,old_y,new_x,new_y);
                if(!state.contains(encode(tmp))){
                    puzzle_bfs new_pz = new puzzle_bfs(tmp);
                    new_pz.parent = end;
                    queue.offer(new_pz);
                    state.add(encode(tmp));
                }
            }
        }
        while(end!=null){
            show(end.data);
            if(end.parent!=null)
                getpath(end.parent.data,end.data);
            end = end.parent;
        }
        for(int i=path.size()-1;i>=0;i--){
            System.out.print(path.get(i));
        }
        System.out.print("\n");
        long endTime =  System.currentTimeMillis();
        time = endTime - startTime;
    }
    private boolean check(int[][] a,int[][] b){
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++){
                if(a[i][j]!=b[i][j])
                    return false;
            }
        return true;
    }
    private void swap(int[][] a,int ox,int oy,int nx,int ny){
        int t=a[ox][oy];
        a[ox][oy] = a[nx][ny];
        a[nx][ny] = t;
    }
    private void show(int[][] a){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++)
                System.out.print(a[i][j]);
            System.out.print("\n");
        }
        System.out.print("\n");

    }
    private void getpath(int[][] a,int[][] b){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                if(a[i][j]==0){
                    int m=0;
                    for(;m<4;m++){
                        int x=i+go[m][0];
                        int y=j+go[m][1];
                        if(x<0 || x>2 || y<0 || y>2 || b[x][y]!=0)
                            continue;
                        path.add(m+1);
                        break;
                    }
                }
            }
        }
    }
}
class puzzle{
    public int h=0;
    public int g;
    public int[][] new_start;
    puzzle parent;
    public puzzle(int[][] now,int[][] goal,int cost){
        new_start = new int[3][3];
        g=cost;
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++){
                if(now[i][j]!=goal[i][j]){
                    int a[] =find(goal,now[i][j]);
                    h += Math.abs(i-a[0]) + Math.abs(j-a[1]);
                }
                new_start[i][j] = now[i][j];
            }
    }
    private int[] find(int a[][],int num){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                if(a[i][j] == num){
                    return new int[]{i,j};
                }
            }
        }
        return null;
    }
}
class Astar_solve{
    Queue<puzzle> queue;
    public  long time;
    private final int[][] go= new int[][]{{0,-1},{0,1},{-1,0},{1,0}};
    public ArrayList<Integer> path;
    String encode(int[][] a){
        String s="";
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                s+=Integer.toString(a[i][j]);
            }
        }
        return s;
    }
    public Astar_solve(int[][] start,int[][] goal){
        path = new ArrayList<>();
        Comparator cmp = new Comparator<puzzle>() {
            public int compare(puzzle e1, puzzle e2) {
                return (e1.h+e1.g) - (e2.h+e2.g);
            }
        };
        Set<String> state = new HashSet<>();
        puzzle pz = new puzzle(start,goal,0);
        queue = new PriorityQueue<puzzle>(cmp);
        queue.add(pz);
        pz.parent=null;
        puzzle end=null;
        state.add(encode(pz.new_start));
        long startTime =  System.currentTimeMillis();
        while (!queue.isEmpty()){
            puzzle ns = queue.poll();
            System.out.println(state.size());
            end = ns;
            if(check(ns.new_start,goal))
                break;
            for(int i=0;i<4;i++){
                int[][] tmp = new int[3][3];
                int blank_x = 0, blank_y = 0;
                for(int x=0;x<3;x++){
                    for(int y=0;y<3;y++) {
                        tmp[x][y] = ns.new_start[x][y];
                        if(tmp[x][y] == 0){
                            blank_x = x;
                            blank_y = y;
                        }
                    }
                }
                int new_x = blank_x + go[i][0];
                int new_y = blank_y + go[i][1];
                if(new_x<0 || new_x>2 || new_y<0 || new_y>2)
                    continue;
                move(tmp,blank_x,blank_y,new_x,new_y);
                if(!state.contains(encode(tmp))){
                    puzzle new_pz = new puzzle(tmp,goal,ns.g+1);
                    new_pz.parent = end;
                    queue.add(new_pz);
                    if(new_pz.h==0)
                        break;
                    state.add(encode(ns.new_start));
                }
            }
        }
        while(end!=null){
            show(end.new_start);
            if(end.parent!=null)
                getpath(end.parent.new_start,end.new_start);
            end = end.parent;
        }
        for(int i=path.size()-1;i>=0;i--){
            System.out.print(path.get(i));
        }
        long endTime =  System.currentTimeMillis();
        time = endTime - startTime;
    }
    private void move(int[][] t,int old_x,int old_y,int new_x,int new_y){
        int tmp = t[old_x][old_y];
        t[old_x][old_y] = t[new_x][new_y];
        t[new_x][new_y] = tmp;
    }
    private boolean check(int[][] a,int[][] b){
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++){
                if(a[i][j]!=b[i][j])
                    return false;
            }
        return true;
    }
    private void show(int[][] a){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++)
                System.out.print(a[i][j]);
            System.out.print("\n");
        }
        System.out.print("\n");

    }
    private void getpath(int[][] a,int[][] b){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                if(a[i][j]==0){
                    int m=0;
                    for(;m<4;m++){
                        int x=i+go[m][0];
                        int y=j+go[m][1];
                        if(x<0 || x>2 || y<0 || y>2 || b[x][y]!=0)
                            continue;
                        path.add(m+1);
                        break;
                    }
                }
            }
        }
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
