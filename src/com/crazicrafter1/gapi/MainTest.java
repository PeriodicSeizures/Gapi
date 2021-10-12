package com.crazicrafter1.gapi;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

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

        File myJar = new File("anvilgui-1_8_R3.jar");

        try {
            URLClassLoader child = new URLClassLoader(
                    new URL[]{myJar.toURI().toURL()},
                    ClassLoader.getSystemClassLoader()
            );
            Class<?> classToLoad = Class.forName("net.wesjd.anvilgui.version.Wrapper1_8_R3", true, child);
            //Method method = classToLoad.getDeclaredMethod("myMethod");
            //Object instance = classToLoad.newInstance();
            //Object result = method.invoke(instance);

            //for (var method : classToLoad.getMethods()) {
            //    System.out.println("Found method: " + method.getName());
            //}

        } catch (Exception e) {
            e.printStackTrace();
        }
        //int i = 4;
        //print(i++);
    }

}
