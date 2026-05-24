package com.quickstart.common;

import org.junit.jupiter.api.Test;

class QsBaseApplicationTests {

    @Test
    void baseTestClassLoads() {
        QsBaseApplicationTests testInstance = new QsBaseApplicationTests();
        org.junit.jupiter.api.Assertions.assertNotNull(testInstance);
    }

}
