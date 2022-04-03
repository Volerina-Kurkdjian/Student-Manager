package com.example.studentmanager.async;

public interface Callback<R> {
    void runResultOnUIThread(R result);
}
