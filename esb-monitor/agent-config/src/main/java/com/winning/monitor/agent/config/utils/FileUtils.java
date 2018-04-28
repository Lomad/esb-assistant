package com.winning.monitor.agent.config.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;

/**
 * Created by xuehao on 2017/7/24.
 */
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    public static final String FINAL_NEW_LINE = "\n"; // 换行
    public static final String FINAL_RETURN = "\r"; // 回车
    /**
     * 斜线“/”
     */
    public static final String DIR_SPLIT_SLASH="/";
    /**
     * 斜线“\\”
     */
    public static final String DIR_SPLIT_BACKSLASH="\\";
    /**
     * 字符编码：UTF-8
     */
    public static final String CHARSET_UTF8="UTF-8";

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
            result= URLDecoder.decode(result, CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 读取文件内容
     */
    public static String readFile(String filePathName) {
//        File file = new File(filePathName);
//        BufferedReader reader = null;
//        StringBuilder strs = new StringBuilder();
//        String ret=null;
//        try {
//            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), CHARSET_UTF8));
//            String tempString = null;
//            // 一次读入一行，直到读入null为文件结束
//            while ((tempString = reader.readLine()) != null) {
//                strs.append(tempString).append(FINAL_RETURN).append(FINAL_NEW_LINE);
//            }
//            reader.close();
//            ret=strs.toString();
//        } catch (IOException e) {
//            logger.error("读取文件内容发生异常错误("+filePathName+")！"+e.getMessage());
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e1) {
//                }
//            }
//        }
//        return ret;

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

}