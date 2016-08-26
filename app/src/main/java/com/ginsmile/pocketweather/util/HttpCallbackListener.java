package com.ginsmile.pocketweather.util;

/**
 * Created by xujin on 15/12/14.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
