package com.yhh.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

/**
 * author: youhh
 * date: 2018/6/28 上午1:08
 * description:
 */

/*
 * 一、使用 NIO 完成网络通信的三个核心：
 *
 * 1. 通道（Channel）：负责连接
 *
 * 	   java.nio.channels.Channel 接口：
 * 			|--SelectableChannel (abstract class)
 * 				|--SocketChannel
 * 				|--ServerSocketChannel              TCP
 * 				|--DatagramChannel
 *
 * 				|--Pipe.SinkChannel                 UDP
 * 				|--Pipe.SourceChannel
 *
 * 2. 缓冲区（Buffer）：负责数据的存取
 *
 * 3. 选择器（Selector）：是 SelectableChannel 的多路复用器。用于监控   的 IO 状况
 *
 */
public class TestBlockingNIO {

    /**
     * 现在还是阻塞式的
     */


    //客户端
    @Test
    public void client() throws IOException, InterruptedException {
        FileChannel inChannel = FileChannel.open(Paths.get("/Users/huanhuanyou/dianda/workspace2/netty-len1/src/1.md"), StandardOpenOption.READ);

        //1. 获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
        //2. 分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("当前时间: " + new Date().getSeconds());
                }
            }
        });
        t.setDaemon(true);
        t.start();

        int i = 1;
        //3. 读取本地文件，并发送到服务端
        while(inChannel.read(buf) != -1){
            buf.flip();
            sChannel.write(buf);
            System.out.println("=======发送数据========" + i++ + "KB" );
            Thread.sleep(500);
            buf.clear();
        }
        System.out.println("=======发送完毕 退出进程========");
        //4. 关闭通道
        inChannel.close();
        //客户端通道不关闭，服务端就不知道数据有没有发送完毕，就在那傻傻的等着。所以客户端一定要给服务端数据发送完毕的信号。
        //sChannel.close();
        Thread.sleep(60000);
    }

    //服务端
    @Test
    public void server() throws IOException, InterruptedException {
        FileChannel outChannel = FileChannel.open(Paths.get("2receive.md"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        //1. 获取通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        //2. 绑定连接
        ssChannel.bind(new InetSocketAddress(9898));
        System.out.println("=========init accept===========");
        //3. 获取客户端连接的通道
        SocketChannel sChannel = ssChannel.accept();
        System.out.println("=========accept===========");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("当前时间: " + new Date().getSeconds());
                }
            }
        }).start();

        //4. 分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);//1KB   单位字节B

        int i = 1;
        //5. 接收客户端的数据，并保存到本地
        while(sChannel.read(buf) != -1){
            buf.flip();
            Thread.sleep(800);
            System.out.println("========接收数据===========" + i++ + "KB");
            outChannel.write(buf);
            buf.clear();
        }

        System.out.println("=======接收完毕 退出进程========");
        //6. 关闭通道
        sChannel.close();
        outChannel.close();
        ssChannel.close();

    }
}
