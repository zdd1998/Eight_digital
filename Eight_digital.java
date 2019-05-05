import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

class panel extends JPanel implements ActionListener {
    int a[][];
    public int blank_position[];
    public JButton[] cells;
    int go[][] = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

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
        blank_position[0] = 2;
        blank_position[1] = 2;
    }

    public boolean judge_move(int pressButton) {
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

    public void actionPerformed(ActionEvent e) {
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

    public int[][] getgrid() {
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
    panel first, second;
    JButton bu1, bu2, bu3, bu4, bu5, bu6;
    int last_grid[][]; //init上次打乱的结果
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
        if (e.getSource() == bu1) {
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
        if (e.getSource() == bu2) {
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
        if (e.getSource() == bu3 ){//dfs
            int t[][] = first.getgrid();
            for(int i=0; i<3;i++) {
                for (int j = 0; j < 3; j++) {
                    System.out.print(t[i][j]);
                    System.out.print(" ");
                }
                System.out.print("\n");
            }
            System.out.print("\n");
        }
        if (e.getSource() == bu4 ){//bfs
            first.getgrid();
            second.getgrid();
        }
        if (e.getSource() == bu5 ){//A*
            first.getgrid();
            second.getgrid();
        }
        if (e.getSource() == bu6 ) {//reback init
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
