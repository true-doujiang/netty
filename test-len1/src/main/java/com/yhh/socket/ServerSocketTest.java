package com.yhh.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author: yhh
 * @Date: 2019/1/10 13:46
 * @Desc: 参考 https://my.oschina.net/shipley/blog/715196
 */
public class ServerSocketTest {

    /**
     *  本地测试不出来，打包放到阿里云上测试
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println(serverSocket.getSoTimeout());
        Socket socket = serverSocket.accept();
        System.out.println(socket);
    }
}
