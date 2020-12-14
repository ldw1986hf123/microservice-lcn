package com.ldw.metadata.dbUtil;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @description:
 * @author: ludanwen
 * @time: 2020/12/10 18:44
 */
public class HttpClientUtil {
    public static void sendPost(Map param, String url, String token) {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();

        String jsonParam = JSON.toJSONString(param);
        StringEntity requestEntity = new StringEntity(jsonParam, "utf-8");


        HttpEntity reqEntity = null;
        try {
            reqEntity = new UrlEncodedFormEntity(formparams, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        RequestConfig requestConfig = RequestConfig.custom()
//        一、连接超时：connectionTimeout-->指的是连接一个url的连接等待时间
                .setConnectTimeout(5000)
//        二、读取数据超时：SocketTimeout-->指的是连接上一个url，获取response的返回等待时间
                .setSocketTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        post.setEntity(requestEntity);
        post.setConfig(requestConfig);
        post.setHeader("Content-type", "application/json");
        post.setHeader("Authorization", token);

        HttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity resEntity = response.getEntity();
            String message = null;
            try {
                message = EntityUtils.toString(resEntity, "utf-8");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.out.println(message);
        } else {
            System.out.println(response);
            System.out.println("请求失败");
        }
    }

    public static String sendGet(Map param, String url, String token) {
        return HttpUtil.get(url, param, 10000);
    }

    public static String sendGet(Map param, String url ) {
        return HttpUtil.get(url, param, 10000);
    }
}
