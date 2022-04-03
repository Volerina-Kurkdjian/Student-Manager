package com.example.studentmanager.async;
import android.os.Handler;
import android.util.Log;

import java.util.concurrent.Callable;

public class RunnableTask<R> implements  Runnable {

    private final Callable<R> asyncOperation;//va lua de pe net ce vrem noi sa prelucram
    private final Handler handler;
    private final Callback<R> mainThreadOperation;//prelucreaza ce am luat de pe net

    public RunnableTask(Callable<R> asyncOperation, Handler handler, Callback<R> mainThreadOperation) {
        this.asyncOperation = asyncOperation;
        this.handler = handler;
        this.mainThreadOperation = mainThreadOperation;
    }

    @Override
    public void run() {
        try {
            R result=asyncOperation.call();//luam jsonul de pe net
            handler.post(new HandlerMessage<>(result,mainThreadOperation));//jsonul luat il dam handlerului prin handlermessage
        } catch (Exception e) {
            Log.i("RunnableTask","failed call runnable task"+e.getMessage());
        }
    }

}
