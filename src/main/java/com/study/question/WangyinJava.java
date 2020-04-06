package com.study.question;

public class WangyinJava {
    public static void main(String[] args) {
        String[] a = new String[2];
        Object[] b = a;
        a[0] = "hi";
        b[1] = Integer.valueOf(42);
    }
}
