package com.fangbinbin.appframework.serverinteraction;

import java.io.IOException;

import org.json.JSONException;

public interface Command<T> {
    public T execute () throws JSONException, IOException, FrameworkException, FrameworkForbiddenException;
}
