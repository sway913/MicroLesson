package com.laifeng.sopcastsdk.controller;


/**
 * @author ljq
 * @date 2018/12/28
 * @description
 */

public interface StreamListener {

    void start();

    void pause();

    void resume();

    void mute();

    void stop();

    void duration(long duration);
}
