package com.gy.dcloudaipan.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;

@Slf4j
public class CommonUtil {
    /**
     * 获取ip
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }


    /**
     * MD5加密
     * @param data
     * @return
     */
    public static String MD5(String data) {
        try {
            java.security.MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }

            return sb.toString().toUpperCase();
        } catch (Exception exception) {
        }
        return null;
    }

    /**
     * 获取验证码随机数
     * @param length 验证码长度
     * @return
     */
    public static String getRandomCode(int length){

        String sources = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int j=0; j<length; j++){
            sb.append(sources.charAt(random.nextInt(9)));
        }
        return sb.toString();
    }

    /**
     * 生成指定长度随机字母和数字
     *
     * @param length
     * @return
     */
    private static final String ALL_CHAR_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static String getStringNumRandom(int length) {
        //生成随机数字和字母,
        Random random = new Random();
        StringBuilder saltString = new StringBuilder(length);
        for (int i = 1; i <= length; ++i) {
            saltString.append(ALL_CHAR_NUM.charAt(random.nextInt(ALL_CHAR_NUM.length())));
        }
        return saltString.toString();
    }

    /**
     * 响应json数据给前端
     *
     * @param response
     * @param obj
     */
    public static void sendJsonMessage(HttpServletResponse response, Object obj) {

        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json; charset=utf-8");

        try (PrintWriter writer = response.getWriter()) {
            writer.print(objectMapper.writeValueAsString(obj));

            response.flushBuffer();

        } catch (IOException e) {
            log.warn("响应json数据给前端异常:{}",e);
        }


    }

    /**
     * 根据文件名获取文件后缀
     */
    public static String getFileSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 根据文件后缀，生成文件存储：年/月/日/uuid.suffix 格式
     */
    public static String getFilePath(String fileName) {
        String suffix = getFileSuffix(fileName);
        // 生成文件在存储桶中的唯一键
        return String.format("%s/%s/%s/%s.%s",
                DateUtil.thisYear(),
                DateUtil.thisMonth() + 1,
                DateUtil.thisDayOfMonth(),
                IdUtil.randomUUID(),
                suffix);
    }

    /**
     * 获取当前时间戳
     * @return
     */
    public static long getCurrentTimestamp(){
        return System.currentTimeMillis();
    }

    /**
     * 生成uuid
     * @return
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-","").substring(0,32);
    }

}