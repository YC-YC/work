package com.zhonghong.chelianupdate.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 根据参数(GET或POST参数)列表和SecretKey生成签名，该签名就是URL中的sign参数
 *
 * @author: mike.ma
 * @Date: 2014/6/12
 */
public class SignatureGenerator {
    /**
     * 根据Url（资源部分），url参数列表和SecretKey生成签名
     *
     * @param urlResourcePart URL资源部分，例如有如下URL：http://faw-vw.timanetwork.com/access/tm/user/getUserInfo?apramX=valueX, 其资源部分是"user/getUserInfo"
     * @param params
     * @param secretKey
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    public static String generate(String urlResourcePart, Map<String, String> params, String secretKey) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //对参数按名称排序(升序)
        List<Map.Entry<String, String>> parameters = new LinkedList<Map.Entry<String, String>>(params.entrySet());
        Collections.sort(parameters, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        //形成参数字符串, 并把SecretKey加在末尾（salt）
        StringBuilder sb = new StringBuilder();
        sb.append(urlResourcePart).append("_");
        for (Map.Entry<String, String> param : parameters) {
            sb.append(param.getKey()).append("=").append(param.getValue()).append("_");
        }
        sb.append(secretKey);

        String baseString = URLEncoder.encode(sb.toString(), "UTF-8");
        return MD5Util.md5(baseString);
    }

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("paramB", "valueB");
        params.put("paramA", "valueA");
        params.put("paramC", "valueC");
        params.put("appkey", "1234567890");
        String urlResourcePart = "user/user/getUserInfo";
        String sign = generate(urlResourcePart, params, "abcdefghijklmnopqrstuvwxyz123456");
        System.out.println(sign);
    }
}
