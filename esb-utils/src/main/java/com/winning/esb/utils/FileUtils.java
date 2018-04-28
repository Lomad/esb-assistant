package com.winning.esb.utils;

import com.winning.esb.stable.CharsetConst;

import java.io.*;
import java.net.URLDecoder;

/**
 * Created by xuehao on 2017/7/24.
 */
public class FileUtils {
    public static final String FINAL_NEW_LINE = "\n"; // 换行
    public static final String FINAL_RETURN = "\r"; // 回车
    /**
     * 斜线“/”
     */
    public static final String DIR_SPLIT_SLASH = "/";
    /**
     * 斜线“\\”
     */
    public static final String DIR_SPLIT_BACKSLASH = "\\";
    /**
     * 字符编码：UTF-8
     */
    public static final String CHARSET_UTF8 = "UTF-8";

    /**
     * 获取程序根目录，格式：D:\Temp\service\WinPTProxyTest
     */
    public static String getRootPath() {
        //因为类名为"FileUtils"，因此" FileUtils.class"一定能找到
        String result = FileUtils.class.getResource("FileUtils.class").toString();
        int index = result.indexOf("WEB-INF");
        if (index == -1) {
            index = result.indexOf("bin");
        }
        if (index >= 0){
            result = result.substring(0, index);
            if (result.startsWith("jar")) {
                // 当class文件在jar文件中时，返回"jar:file:/F:/ ..."样的路径
                result = result.substring(10);
            } else if (result.startsWith("file")) {
                // 当class文件在class文件中时，返回"file:/F:/ ..."样的路径
                result = result.substring(6);
            }
            if (result.endsWith(DIR_SPLIT_SLASH) || result.endsWith(DIR_SPLIT_BACKSLASH)) {
                result = result.substring(0, result.length() - 1);// 不包含最后的"/"
            }
            //转码，例如，将目录中的“%20”转为空格
            try {
                result = URLDecoder.decode(result, CHARSET_UTF8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return result;
        }
        return "";
    }

    /**
     * 读取文件内容
     */
    public static String readFile(String filePathName) {
        File file = new File(filePathName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + CHARSET_UTF8);
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 读取文件内容
     */
    public static String readFileNeedRootPath(String filePathName) {
        filePathName = getRootPath()+filePathName;
        return readFile(filePathName);
    }

    /**
     * 创建目录
     */
    public static void createPath(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 创建文件
     */
    public static void createFile(String filePathName) {
        try {
            File file = new File(filePathName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 写入文件（覆盖方式）
     */
    public static void writeFile(String path, String fileName, String msg) {
        writeFile(path, fileName, msg, false);
    }

    /**
     * 写入文件（覆盖方式）
     */
    public static void writeFile(String filePathName, String msg) {
        writeFile(filePathName, msg, false);
    }

    /**
     * 写入文件
     */
    public static void writeFile(String path, String fileName, String msg, boolean append) {
        createPath(path);
        writeFile(path + "\\" + fileName, msg, append);
    }

    /**
     * 写入文件
     */
    public static void writeFile(String filePathName, String msg, boolean append) {
        try {
            createFile(filePathName);
            File file = new File(filePathName);
            FileOutputStream out = new FileOutputStream(file, append);
            out.write(msg.getBytes(CharsetConst.CHARSET_UTF8));
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 删除文件
     */
    public static void deleteFile(String filePathName) {
        try {
            File file = new File(filePathName);
            if(file.exists() && file.isFile()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}