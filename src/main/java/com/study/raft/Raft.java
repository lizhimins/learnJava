package com.study.raft;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Raft {
    private Timer timer = new Timer();

    public static void main(String[] args) {
        int gapTime = new Random().nextInt() % 150 + 150;

        System.out.println(gapTime);

        new Raft().timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println(System.currentTimeMillis());
            }
        }, 0, gapTime);
    }
}
