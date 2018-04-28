package com.winning.esb.utils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    /**
     * 字符集：UTF-8
     */
    public static final String Charset_UTF_8 = "UTF-8";
    /**
     * 字符集：ASCII
     */
    public static final String Charset_ASCII = "ASCII";
    /**
     * 字符集：Unicode
     */
    public static final String Charset_Unicode = "Unicode";
    /**
     * 字符集：GBK
     */
    public static final String Charset_GBK = "GBK";
    /**
     * 字符集：GB2312
     */
    public static final String Charset_GB2312 = "GB2312";
    /**
     * 字符集：ISO-8859-1
     */
    public static final String Charset_ISO_8859_1 = "ISO-8859-1";
    /**
     * 正则表达式：[0-9]+
     */
    public static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");

    /**
     * 未知格式代码
     */
    public static final int DATA_FORMAT_UNKNOWN = -1;
    /**
     * XML格式代码
     */
    public static final int DATA_FORMAT_XML = 0;
    /**
     * JSON格式代码
     */
    public static final int DATA_FORMAT_JSON = 1;
    /**
     * HL7格式代码
     */
    public static final int DATA_FORMAT_HL7 = 2;

    /**
     * 模仿C#格式化字符串
     *
     * @param str
     * @param args
     * @return
     */
    public static String format(String str, Object... args) {
        if (args != null && args.length == 0 && args[0] instanceof Object[]) {
            args = (Object[]) args[0];
        }
        for (int i = 0; i < args.length; i++) {
            Object s = args[i];
            if (s == null) {
                s = "";
            }
            str = str.replaceAll("\\{" + i + "\\}", s.toString());
        }
        return str;
    }

    /**
     * 比较字符串是否相同：如果都为null，则认为相同，其他情况，比较内容
     */
    public static boolean compare(String str1, String str2) {
        return compare(str1, str2, true);
    }

    /**
     * 比较字符串是否相同：如果都为null或空，则认为相同，其他情况，比较内容
     *
     * @param caseSize true-大小写敏感（即A与a不同），false-大小写不敏感（即A与a相同）
     */
    public static boolean compare(String str1, String str2, boolean caseSize) {
        if (isEmpty(str1) && isEmpty(str2)) {
            return true;
        } else if ((isEmpty(str1) && !isEmpty(str2)) || (!isEmpty(str1) && isEmpty(str2))) {
            return false;
        } else if (str1.equals(str2) && caseSize == true) {
            return true;
        } else if (str1.toLowerCase().equals(str2.toLowerCase()) && caseSize == false) {
            return true;
        } else {
            return false;
        }
    }

    public static String join(Object[] array, String separator) {
        return org.apache.commons.lang3.StringUtils.join(array, separator);
    }

    public static String join(List<String> strList, String separator) {
        return org.apache.commons.lang3.StringUtils.join(strList, separator);
    }

    public static String joinWithEmpty(String... args) {
        return join("", args);
    }

    public static String join(String separator, String... args) {
        return org.apache.commons.lang3.StringUtils.join(args, separator);
    }

    public static String joinWithEmpty(Object[] array) {
        return org.apache.commons.lang3.StringUtils.join(array, "");
    }

    public static String joinWithEmpty(List<String> strList) {
        return org.apache.commons.lang3.StringUtils.join(strList, "");
    }

    //是否全部为空
    public static boolean isEmpty(String arg) {
        if (arg == null || "".equals(arg.toString().trim())) {
            return true;
        } else {
            return false;
        }
    }

    //是否全部为空
    public static boolean isEmpty(Object... args) {
        for (int i = 0; i < args.length; i++) {
            Object s = args[i];
            if (s == null || "".equals(s.toString().trim())) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    //是否全部为空
    public static boolean isListEmpty(List<Object> objList) {
        if (objList == null || objList.size() < 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将NULL转为空值
     */
    public static String nullToEmpty(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    public static String trim(String str) {
        str = nullToEmpty(str);
        return str.trim();
    }

    public static StringBuffer trimLeft(StringBuffer str) {
        if (str == null || str.length() < 1) {
            return str;
        }

        //递归删除前缀空格
        if (" ".equals(str.substring(0, 1))) {
            trim(str.deleteCharAt(0));
        }

        return str;
    }

    public static StringBuffer trimRight(StringBuffer str) {
        if (str == null || str.length() < 1) {
            return str;
        }

        //递归删除后缀空格
        if (" ".equals(str.substring(str.length() - 1))) {
            trim(str.deleteCharAt(str.length() - 1));
        }

        return str;
    }

    public static StringBuffer trim(StringBuffer str) {
        str = trimLeft(str);
        str = trimRight(str);
        return str;
    }

    //是否整数
    public static boolean isInt(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return NUMBER_PATTERN.matcher(str).matches();
    }

    /**
     * 是否中文字符
     *
     * @param c
     * @return
     */
    private static final boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 是否英文字符
     */
    private static final boolean isEnglish(String inStr) {
        String regex = "^[a-zA-Z]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(inStr);
        return match.matches();
    }

    /**
     * 是否存在中文字符
     */
    public static final boolean hasChinese(String str) {
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    public static final boolean hasEnglish(String str) {
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isEnglish(String.valueOf(c))) {
                return true;
            }
        }
        return false;
    }

    public static final boolean hasInt(String str) {
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isInt(String.valueOf(c))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取中文字符
     */
    public static final String getChinese(String str) {
        StringBuilder ret = new StringBuilder();
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                ret.append(c);
            }
        }
        return ret.toString();
    }

    /**
     * 获取英文字符
     */
    public static String getEnglish(String str) {
        StringBuilder ret = new StringBuilder();
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isEnglish(String.valueOf(c))) {
                ret.append(c);
            }
        }
        return ret.toString();
    }

    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String ToSBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    /**
     * 获取字符串长度（中文和全角类型的字符占用两个长度）
     */
    public static final int getLen(String str) {
        int len = 0;
        if (!isEmpty(str)) {
            char[] ch = str.toCharArray();
            for (int i = 0; i < ch.length; i++) {
                char c = ch[i];
                if (isChinese(c)) {
                    len += 2;        //如果是中文和全角类型的字符，增加两个长度
                } else {
                    len += 1;        //如果是英文字符，增加一个长度
                }
            }
        }
        return len;
    }

    /**
     * 获取字符串中的数字
     */
    public static String getNumber(String str) {
        if (str != null) {
            String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            str = m.replaceAll("").trim();
        }
        return str;
    }

    /**
     * 截取指定长度的信息
     * (如果是中文或全角类型的字符，按两个长度计算)
     */
    public static String substrLen(String str, int len) {
        StringBuilder strTemp = new StringBuilder();
        if (!isEmpty(str)) {
            int lenTemp = 0;
            char[] ch = str.toCharArray();
            for (int i = 0; i < ch.length; i++) {
                char c = ch[i];
                if (isChinese(c)) {
                    lenTemp += 2;        //如果是中文和全角类型的字符，增加两个长度
                } else {
                    lenTemp += 1;        //如果是英文字符，增加一个长度
                }

                //拼接返回的字符串
                if (lenTemp <= len) {
                    strTemp.append(c);
                } else {
                    break;
                }
            }
        }
        return strTemp.toString();
    }

    /**
     * 乱码转换：将ISO-8859-1转为UTF-8
     */
    public static String TransFromIso88591ToUtf8(String str) throws UnsupportedEncodingException {
        if (isEmpty(str) || !str.equals(new String(str.getBytes(Charset_ISO_8859_1)))) {
            return str;
        } else {
            return new String(str.getBytes(Charset_ISO_8859_1), Charset_UTF_8);
        }
    }

    /**
     * 删除特殊字符
     *
     * @param str
     * @return
     */
    public static String delSpeStr(String str) {
        if (!isEmpty(str)) {
            // 只允许字母和数字
            // String   regEx  =  "[^a-zA-Z0-9]";                     
            // 清除掉所有特殊字符  
//    		String regEx="[`~!@#$%^&*+=|{}':;',\\[\\].<>/?~！@#￥%……&——+|{}【】‘；：”“’。，、？	 ]";  
            String regEx = "[	 ]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            String retString = m.replaceAll("").trim();
            return retString;
        }
        return str;

    }

    /**
     * 检测数据格式
     *
     * @return -1 - 未知，0-XML，1-JSON，2-HL7
     */
    public static int checkDataFormat(String msg) {
        int result = DATA_FORMAT_UNKNOWN;
        if (!isEmpty(msg)) {
            String msgBak = msg.trim();
            String firstChar = msgBak.substring(0, 1);
            if ("<".equals(firstChar)) {
                result = DATA_FORMAT_XML;
            } else if ("{".equals(firstChar) || "[".equals(firstChar)) {
                result = DATA_FORMAT_JSON;
            } else {
                result = DATA_FORMAT_HL7;
            }
        }
        return result;
    }

    public static void main(String[] args) {
//        //测试
//        test1();
    }

    public static void test1() {
//    	String s1="ds", s2 = null, s3="", s4="df";
//    	String strTemp=StringUtils.join(new String[] { s1, s2, s3, s4 }, "");
//    	String str="测试姓名";
//    	int len=getLen(str);
//    	str="啊的sd但ｓｄ　是";
//    	len=getLen(str);

//    	String s1="12312", s2;
//    	s2=getNumber(s1);
//    	s1="123chch12";
//    	s2=getNumber(s1);
//    	s1="123  12";
//    	s2=getNumber(s1);
//    	s1=null;
//    	s2=getNumber(s1);
//    	s1="";
//    	s2=getNumber(s1);
//    	s1="cdsd";
//    	s2=getNumber(s1);
        System.out.println(delSpeStr("测试       ；、、*（）特殊！！!字符"));

        StringBuffer str = new StringBuffer();
        str.append("   dand   ");
        System.out.println(str);
        str.deleteCharAt(0);
        System.out.println(str);
        str.deleteCharAt(0);
        System.out.println(str);
        str.deleteCharAt(0);
        System.out.println(str);

        String s = str.substring(str.length() - 1);
        System.out.println(s);

        str.deleteCharAt(str.length() - 1);
        System.out.println(str);
        str.deleteCharAt(str.length() - 1);
        System.out.println(str);


        str = new StringBuffer();
        str.append("   dand   ");
        System.out.println(trim(str));

    }
}
