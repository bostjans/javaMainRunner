package com.stupica.mainRunner;

import com.stupica.GlobalVar;

import static org.junit.jupiter.api.Assertions.*;


class MainRunBaseTestPrint extends MainRunBase {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        GlobalVar.bIsModeVerbose = true;
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void test_printProgramInfo() {
        readVersionManifest();
        printProgramInfo();
    }
}