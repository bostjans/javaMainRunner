package com.stupica.mainRunner;

import static org.junit.jupiter.api.Assertions.*;


class MainRunBaseTestPrint extends MainRunBase {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void test_printProgramInfo() {
        printProgramInfo();
    }
}