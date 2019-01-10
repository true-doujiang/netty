package com.yhh.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Author: yhh
 * @Date: 2019/1/10 13:46
 * @Desc:
 */
public class SocketTest {


    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
//        socket.connect(new InetSocketAddress("39.106.63.228", 8080));
//        System.out.println("Connected.");

        long t1 = 0;
        try {
            t1 = System.currentTimeMillis();
            socket.connect(new InetSocketAddress("39.106.63.228", 8080));
        } catch (IOException e) {
            long t2 = System.currentTimeMillis();
            e.printStackTrace();
            System.out.println("Connect failed, take time -> " + (t2 - t1) + "ms.");
        }
    }
}
