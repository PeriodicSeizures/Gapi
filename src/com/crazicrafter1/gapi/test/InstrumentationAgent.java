package com.crazicrafter1.gapi.test;

import java.lang.instrument.Instrumentation;

public class InstrumentationAgent {
    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(Object o) {
        return instrumentation.getObjectSize(o);
    }
}
