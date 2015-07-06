package com.yac.yacapp.test;

import junit.framework.TestSuite;
import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;
import com.yac.yacapp.test.MainActivityTest;


public class MusicPlayerFunctionalTestRunner extends InstrumentationTestRunner {
	
    @Override
    public TestSuite getAllTests() {
        TestSuite suite = new InstrumentationTestSuite(this);  
        suite.addTestSuite(MainActivityTest.class);
        return suite;
    }

    @Override
    public ClassLoader getLoader() {
        return MusicPlayerFunctionalTestRunner.class.getClassLoader();
    }
}
