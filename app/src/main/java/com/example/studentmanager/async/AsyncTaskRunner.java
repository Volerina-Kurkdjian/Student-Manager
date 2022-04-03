package com.example.studentmanager.async;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncTaskRunner {
    private final Executor executor= Executors.newCachedThreadPool();
    private final Handler handler=new Handler(Looper.getMainLooper());

    public<R> void executeAsync(Callable<R> asyncOperation, Callback<R> mainThreadOperation) {
        try {
            executor.execute(new RunnableTask<>(asyncOperation, handler, mainThreadOperation));

        } catch (Exception e) {
            Log.i("AsyncTask","failed call executeAsyncTask"+e.getMessage());
        }
    }

}
