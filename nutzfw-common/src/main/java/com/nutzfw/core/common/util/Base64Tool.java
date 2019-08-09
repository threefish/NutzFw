package com.nutzfw.core.common.util;

import org.nutz.lang.Encoding;
import org.nutz.repo.Base64;

import java.io.*;

/**
 * 依赖javabase64-1.3.1.jar 或 common-codec
 */
public class Base64Tool {
    /**
     * 文件读取缓冲区大小
     */
    private static final int CACHE_SIZE = 1024;

    /**
     * 加密
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getBase64(String str) throws UnsupportedEncodingException {
        byte[] b = str.getBytes(Encoding.UTF8);
        String s = null;
        if (b != null) {
            s = Base64.encodeToString(b, false);
        }
        return s;
    }

    /**
     * 解密
     *
     * @param s
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getFromBase64(String s) throws UnsupportedEncodingException {
        String result = null;
        if (s != null) {
            result = new String(Base64.decode(s), Encoding.UTF8);
        }
        return result;
    }

    /**
     * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     *
     * @param imgFilePath
     * @return
     */
    public static String getImageStr(String imgFilePath) {
        byte[] data = null;
        try (InputStream in = new FileInputStream(imgFilePath)) {
            data = new byte[in.available()];
            in.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(data, false);
    }

    /** */

    /**
     * 将Base64编码图片字符串转化为图片
     *
     * @param imgFilePath
     * @return
     */
    public static boolean generateImage(String imgStr, String imgFilePath) {
        if (imgStr == null) {
            return false;
        }
        String index = "data:image/png;base64,";
        imgStr = imgStr.replace(index, "");
        try (OutputStream out = new FileOutputStream(imgFilePath)) {
            byte[] bytes = Base64.decode(imgStr);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {
                    // 调整异常数据
                    bytes[i] += 256;
                }
            }
            out.write(bytes);
            out.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** */

    /**
     * <p>
     * BASE64字符串解码为二进制数据
     * </p>
     *
     * @param base64
     * @return
     */
    public static byte[] decode(String base64) {
        return Base64.decode(base64);
    }

    /**
     * <p>
     * 二进制数据编码为BASE64字符串
     * </p>
     *
     * @param bytes
     * @return
     */
    public static String encode(byte[] bytes) {
        return Base64.encodeToString(bytes, false);
    }

    /**
     * <p>
     * 将文件编码为BASE64字符串
     * </p>
     * <p>
     * 大文件慎用，可能会导致内存溢出
     * </p>
     *
     * @param filePath 文件绝对路径
     * @return
     * @throws Exception
     */
    public static String encodeFile(String filePath) throws Exception {
        return encode(fileToByte(filePath));
    }

    /**
     * <p>
     * BASE64字符串转回文件
     * </p>
     *
     * @param filePath 文件绝对路径
     * @param base64   编码字符串
     * @throws Exception
     */
    public static void decodeToFile(String filePath, String base64) throws Exception {
        byteArrayToFile(decode(base64), filePath);
    }

    /**
     * <p>
     * 文件转换为二进制数组
     * </p>
     *
     * @param filePath 文件路径
     * @return
     * @throws Exception
     */
    public static byte[] fileToByte(String filePath) throws Exception {
        byte[] data = new byte[0];
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
            in.close();
            data = out.toByteArray();
        }
        return data;
    }

    /**
     * <p>
     * 文件转换为二进制数组
     * </p>
     *
     * @param in 文件
     * @return
     * @throws Exception
     */
    public static byte[] fileToByte(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
        byte[] cache = new byte[CACHE_SIZE];
        int nRead = 0;
        while ((nRead = in.read(cache)) != -1) {
            out.write(cache, 0, nRead);
            out.flush();
        }
        out.close();
        in.close();
        return out.toByteArray();
    }

    /**
     * <p>
     * 二进制数据写文件
     * </p>
     *
     * @param bytes    二进制数据
     * @param filePath 文件生成目录
     */
    public static void byteArrayToFile(byte[] bytes, String filePath) throws Exception {
        InputStream in = new ByteArrayInputStream(bytes);
        File destFile = new File(filePath);
        destFile.createNewFile();
        OutputStream out = new FileOutputStream(destFile);
        byte[] cache = new byte[CACHE_SIZE];
        int nRead = 0;
        while ((nRead = in.read(cache)) != -1) {
            out.write(cache, 0, nRead);
            out.flush();
        }
        out.close();
        in.close();
    }
}  