package com.epam.jira.testng;

import com.epam.jira.JIRATestKey;
import com.epam.jira.util.XMLWriter;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

public class ExecutionListener extends TestListenerAdapter {

    private Map<String, String> tests = new TreeMap<String, String>();

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        super.onTestSuccess(iTestResult);
        String key = getTestJIRATestKey(iTestResult);
        if (key != null) {
            tests.put(key, "Passed");
        }
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        super.onTestFailure(iTestResult);
        String key = getTestJIRATestKey(iTestResult);
        if (key != null) {
            tests.put(key, "Failed");
        }
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        super.onTestSkipped(iTestResult);
        String key = getTestJIRATestKey(iTestResult);
        if (key != null) {
            tests.put(key, "Blocked");
        }
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        super.onFinish(iTestContext);
        XMLWriter.writeXmlFile(tests);
    }

    private String getTestJIRATestKey(ITestResult result) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        JIRATestKey annotation = method.getAnnotation(JIRATestKey.class);
        if (annotation != null) {
            return annotation.key();
        }
        return null;
    }
}
