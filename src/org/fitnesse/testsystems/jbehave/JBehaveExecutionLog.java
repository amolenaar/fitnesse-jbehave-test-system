package org.fitnesse.testsystems.jbehave;

import fitnesse.testsystems.ExecutionLog;

import java.util.Collections;
import java.util.List;

public class JBehaveExecutionLog implements ExecutionLog {
    @Override
    public void addException(Throwable e) {

    }

    @Override
    public List<Throwable> getExceptions() {
        return Collections.emptyList();
    }

    @Override
    public String getCommand() {
        return "jbehave";
    }

    @Override
    public long getExecutionTime() {
        return 0;
    }

    @Override
    public int getExitCode() {
        return 0;
    }

    @Override
    public String getCapturedOutput() {
        return "";
    }

    @Override
    public String getCapturedError() {
        return "";
    }

    @Override
    public boolean hasCapturedOutput() {
        return false;
    }
}
