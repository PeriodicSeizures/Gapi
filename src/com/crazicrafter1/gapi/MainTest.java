package com.crazicrafter1.gapi;

public class MainTest {

    public static class A {
        void foo() {
            System.out.println("Parent foo!");
        }

        void bar() {
            System.out.println("Parent bar!");
            this.foo();
        }
    }

    public static class B extends A {
        @Override
        void foo() {
            System.out.println("Child foo!");
        }
    }

    static void print(int i) {
        System.out.println("i: " + i);
    }

    public static void main(String[] args) {
        //new B().bar();

        int i = 4;
        print(i++);
    }

}
