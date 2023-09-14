package com.asv.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordUtils {
    public static String encode(String password) {
        String encodedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                String hex = Integer.toHexString(0xff & aByte);
                if (hex.length() == 1) sb.append('0');
                sb.append(hex);
            }
            encodedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encodedPassword;
    }

    public static String generateRandomString() {
        // 定义密码字符集合
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        // 生成随机密码字符串
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }

    public static String hashPassword(String password) {
        try {
            // 创建MD5哈希算法实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 将密码转换为字节数组
            byte[] passwordBytes = password.getBytes();

            // 计算MD5哈希值
            byte[] hashedBytes = md.digest(passwordBytes);

            // 将哈希值转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static void main(String[] args) {
        System.out.println(PasswordUtils.encode("123456"));
    }
}
