package com.fangbinbin.appframework.serverinteraction;

public interface ResultListener<T> {
    public void onResultsSucceded(T result);
    public void onResultsFail();
}

