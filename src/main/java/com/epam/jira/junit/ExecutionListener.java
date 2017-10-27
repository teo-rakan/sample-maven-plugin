package com.epam.jira.junit;

import com.epam.jira.JIRATestKey;
import com.epam.jira.util.XMLWriter;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.Map;
import java.util.TreeMap;

public class ExecutionListener extends RunListener {

    private Map<String, String> tests = new TreeMap<String, String>();

    @Override
    public void testFinished(Description description) {
        String key = getTestJIRATestKey(description);
        if (key != null) {
            tests.put(key, "Passed");
        }
    }

    @Override
    public void testFailure(Failure failure) {
        String key = getTestJIRATestKey(failure.getDescription());
        if (key != null) {
            tests.put(key, "Failed");
        }
    }

    @Override
    public void testIgnored(Description description)  {
        String key = getTestJIRATestKey(description);
        if (key != null) {
            tests.put(key, "Blocked");
        }
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        XMLWriter.writeXmlFile(tests);
    }

    private String getTestJIRATestKey(Description description) {
        JIRATestKey annotation = description.getAnnotation(JIRATestKey.class);
        if (annotation != null) {
            return annotation.key();
        }
        return null;
    }
}
