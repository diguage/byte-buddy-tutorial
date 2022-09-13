package com.diguage.cafe.divecode;

import java.util.concurrent.TimeUnit;

public class AttachTest {
    public static void main(String[] args) throws InterruptedException {
        while (true) {
            System.out.println(foo());
            TimeUnit.SECONDS.sleep(5);
        }
    }

    private static int foo() {
        return 100;
    }
}
