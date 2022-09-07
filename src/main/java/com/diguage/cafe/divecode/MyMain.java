package com.diguage.cafe.divecode;

public class MyMain {
    public int a = 0;
    public int b = 1;
    public int abc = 2;
    public String s;

    public void test1() {

    }

    public void test2() {

    }

    public void xyz() {

    }

    public int foo(int a) {
        System.out.println("hello foo");
        return a; // 修改为 return a + 100;
    }

    public void divzero() {
        System.out.println("step 1");
        int a = 1 / 0;
        System.out.println("step 2");
    }

    public static void main(String[] args) {
        MyMain object = new MyMain();
        System.out.println(object.foo(1));
        object.divzero();
    }
}
