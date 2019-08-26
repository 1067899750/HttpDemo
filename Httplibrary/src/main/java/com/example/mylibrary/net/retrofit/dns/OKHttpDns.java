package com.example.mylibrary.net.retrofit.dns;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Dns;

/**
 * @describe Dns 用于拦截返回信息
 * @euthor puyantao
 * @email puyantao@purang.com
 * @create 2019/8/23 14:36
 */
public class OKHttpDns implements Dns {
    private long timeout;

    public OKHttpDns(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public List<InetAddress> lookup(final String s) throws UnknownHostException {
        if (s == null) {
            throw new UnknownHostException("hostname == null");
        } else {
            try {
                FutureTask<List<InetAddress>> task = new FutureTask<>(new Callable<List<InetAddress>>() {
                    @Override
                    public List<InetAddress> call() throws Exception {
                        //可以把 s 替换成新的域名, 用于拦截返回信息
                        URL url = new URL(HttpDns.getInstance().getIp());
                        String ip = url.getHost();
//                        String ip = "yanyangtian.purang.com";
                        if (ip != null && !ip.equals("")) {
                            return Arrays.asList(InetAddress.getAllByName(ip));
                        } else {
                            return Arrays.asList(InetAddress.getAllByName(s));
                        }
                    }
                });
                new Thread(task).start();
                return task.get(timeout, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                UnknownHostException unknownHostException =
                        new UnknownHostException("Broken system behaviour for dns lookup of " + s);
                unknownHostException.initCause(e);
                throw unknownHostException;
            }
        }
    }
}























