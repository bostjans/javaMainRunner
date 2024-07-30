package com.stupica.mainRunner;


class MainRunBaseTestSysinfo extends MainRunBase {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void test_printSysInfo() {
        printSystemInfo();
        System.out.println("--");
        System.out.println("java.library.path: " + System.getProperty("java.library.path"));
        System.out.println("java.class.path: " + System.getProperty("java.class.path"));
        System.out.println("sun.java.command: " + System.getProperty("sun.java.command"));
    }
}