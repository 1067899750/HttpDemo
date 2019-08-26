package com.example.mylibrary.net.retrofit.dns;

import android.text.TextUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Dns;

/**
 * @describe
 * @euthor puyantao
 * @email puyantao@purang.com
 * @create 2019/8/26 11:30
 */
public class OkDns implements Dns {
    private String mIps;
    private long mLastDnsTime;
    private static final long DNS_TTL = 600 * 1000;//自定义DNS缓存时间
    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        try {
            List<InetAddress> list = Dns.SYSTEM.lookup(hostname);
            return list;
        } catch (Exception e) {
            if (System.currentTimeMillis() - mLastDnsTime > DNS_TTL) {//超过ttl 时间就重新请求dns
                mIps = null;
            }
            if (TextUtils.isEmpty(mIps)) {
                mIps = HttpDns.getInstance().getIp();
                mLastDnsTime = System.currentTimeMillis();
            }
            if (!TextUtils.isEmpty(mIps)) {
                return Arrays.asList(InetAddress.getAllByName(mIps));
            } else {
                return Dns.SYSTEM.lookup(hostname);
            }
        }
    }
}












