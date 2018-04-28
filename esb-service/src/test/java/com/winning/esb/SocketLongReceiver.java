package com.winning.esb;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.winning.esb.utils.TokenUtils;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by xuehao on 2017/9/4.
 */
public class SocketLongReceiver {
    private static final String SERVER_IP = "192.168.33.238";
    private static final Integer SERVER_PORT = 36001;

//    public static void main(String[] args) {
//        try {
//            String secret = "secret";
//            String message = "Message";
//
//            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
//            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA512");
//            sha512_HMAC.init(secret_key);
//
//            String hash = new String(Base64.encodeBase64(sha512_HMAC.doFinal(message.getBytes()), true));
//            System.out.println(hash);
//
//            String hash2 = new String(Base64.encodeBase64(sha512_HMAC.doFinal(message.getBytes()), false));
//            System.out.println(hash2);
//
//            String hash3 = new String(Base64.encodeBase64(sha512_HMAC.doFinal(message.getBytes())));
//            System.out.println(hash3);
//        }
//        catch (Exception e){
//            System.out.println("Error");
//        }
//    }

    public static void main(String[] args) {
        try {
            String secret = "grace";
            String head = "eyJhbGciOiJIUzUxMiJ9";
            String body = "eyJzdWIiOiJISVMwMTAxIiwicm9sZSI6WyJhZG1pbiIsInVzZXIiXSwiY3J0IjoxNTEwMjk3NjI1NjMyLCJhdXRoIjoie1wiL2cvYXBpL3YxL2dldEp6amxzanpcXHRcXHRcIjpcImlkLGp6bHNoLHlsamdkbSx5bGpnbWNcIn0iLCJleHAiOjQ2NjM4OTc2MjV9";
            String message = head + "." + body;

            System.out.println(TokenUtils.generateToken("HIS0101", "{\"/g/api/v1/getJzjlsjz\\t\\t\":\"id,jzlsh,yljgdm,yljgmc\"}"));

            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA512");
            sha512_HMAC.init(secret_key);
            String hash = new String(Base64.encodeBase64Chunked(sha512_HMAC.doFinal(message.getBytes())));
            System.out.println(hash);
            System.out.println(sha512_HMAC.doFinal(message.getBytes()).toString());
        } catch (Exception e){
            System.out.println("Error");
        }
    }

//    public static void main(String[] args) {
//        accept();
//    }

    public static void accept() {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("服务器端--开始监听");

            while(true){
                Socket socket  = serverSocket.accept();
                SocketLongReceiverHandler hm = new SocketLongReceiverHandler(socket);
                Thread t = new Thread(hm);
                t.start();
            }

            //1.socket 的输入输出流任意一个关闭，则socket都不可再用了，所以要关闭就一起关闭了。
            //2.socket 流的读是阻塞的，A不要输入流关闭前时，要考虑B端的输出流是否还需要写。否者，B端一直等待A端接收，而A端却接受不了，B一直阻塞,在长连接中尤其要注意，注意流的结束标志
            //3.io 流最后一定要关闭，不然会一直占用内存，可能程序会崩溃。文件输出也可能没有任何信息
            //4.字符输出推荐使用printWriter 流,它也可以直接对文件操作，它有一个参数，设置为true 可以自动刷新，强制从缓冲中写出数据
            //5.缓冲流 都有 bufw.newLine();方法，添加换行
            //6.输入流 是指从什么地方读取/输出流是指输出到什么地方
            //7.OutputStreamWriter和InputStreamReader是转换流,把字节流转换成字符流
            //8.Scanner 取得输入的依据是空格符,包括空格键,Tab键和Enter键,任意一个按下就会返回下一个输入，如果需要包括空格之类的则用bufferreader来获取输入
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}