package com.example.mylibrary.net.retrofit.interceptor;


import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.HttpUrl;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Desc: http 统计
 * @Author: puyantao
 * @CreateDate: 2022/9/9 10:20
 */
public class HttpEventListener extends EventListener {

    /**
     * 自定义EventListener工厂
     */
    public static final Factory FACTORY = new Factory() {
        final AtomicLong nextCallId = new AtomicLong(1L);

        @Override
        public EventListener create(Call call) {
            long callId = nextCallId.getAndIncrement();
            return new HttpEventListener(callId, call.request().url(), System.currentTimeMillis());
        }
    };

    /**
     * 每次请求的标识
     */
    private final long callId;

    /**
     * 每次请求的开始时间
     */
    private final long callStartTime;

    private StringBuilder sbLog;

    public HttpEventListener(long callId, HttpUrl url, long callStartTime) {
        this.callId = callId;
        this.callStartTime = callStartTime;
        this.sbLog = new StringBuilder(url.toString()).append(" ").append(callId).append(":");
    }

    //上报
    public void report(Long callEndTime, Call call, Response response){
        Log.i("----->2", callEndTime - callStartTime + "");
        int code = response.code();
        String url = call.request().url().toString();
        String method = call.request().method();
    }

    @Override
    public void callStart(Call call) {
        super.callStart(call);
    }

    @Override
    public void callEnd(Call call) {
        super.callEnd(call);
    }

    @Override
    public void callFailed(Call call, IOException ioe) {
        super.callFailed(call, ioe);
    }

    @Override
    public void dnsStart(Call call, String domainName) {
        super.dnsStart(call, domainName);
    }


    @Override
    public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
        super.dnsEnd(call, domainName, inetAddressList);
    }

    @Override
    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        super.connectStart(call, inetSocketAddress, proxy);
    }


    @Override
    public void connectEnd(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol) {
        super.connectEnd(call, inetSocketAddress, proxy, protocol);
    }

    @Override
    public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol, IOException ioe) {
        super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
    }


    @Override
    public void requestBodyStart(Call call) {
        super.requestBodyStart(call);
    }

    @Override
    public void requestBodyEnd(Call call, long byteCount) {
        super.requestBodyEnd(call, byteCount);
    }


    @Override
    public void requestHeadersStart(Call call) {
        super.requestHeadersStart(call);
    }

    @Override
    public void requestHeadersEnd(Call call, Request request) {
        super.requestHeadersEnd(call, request);
    }


    @Override
    public void responseBodyStart(Call call) {
        super.responseBodyStart(call);
    }

    @Override
    public void responseHeadersEnd(Call call, Response response) {
        super.responseHeadersEnd(call, response);
        report(System.currentTimeMillis(), call, response);
    }


    @Override
    public void responseHeadersStart(Call call) {
        super.responseHeadersStart(call);
    }

    @Override
    public void responseBodyEnd(Call call, long byteCount) {
        super.responseBodyEnd(call, byteCount);
    }
}












