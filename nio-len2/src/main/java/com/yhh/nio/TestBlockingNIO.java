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
 * @author: youhh
 * @date: 2018/6/28 上午1:08
 * @description:
 *
 *
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
        FileChannel inChannel = FileChannel.open(Paths.get("2receive.md"), StandardOpenOption.READ);

        //1. 获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
        //2. 分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        Thread t = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("client当前时间: " + new Date().toLocaleString());
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
            //Thread.sleep(100);
            buf.clear();
        }
        System.out.println("=======client发送完毕 退出进程========");
        //4. 关闭通道
        inChannel.close();
        /**
         *
         *  客户端socket通道不关闭，服务端就不知道数据有没有发送完毕，就在那傻傻的等着(TCP 连接一直连着)，
         *  但是客户端进程发送完数据进程就退出了，socket异常关闭，服务端就会出现java.io.IOException: 远程主机强迫关闭了一个现有的连接。
         *
         *  **********看看JDK API文档，比啥都强***********
         *  调用关联套接字对象的 shutdownInput 方法来关闭某个通道的输入端将导致该通道上的后续读取操作返回 -1（指示流的末尾）。
         *  调用关联套接字对象的 shutdownOutput 方法来关闭通道的输出端将导致该通道上的后续写入操作抛出 ClosedChannelException。
         */
        //sChannel.socket().shutdownInput();
        //sChannel.shutdownOutput();  // server read() 返回-1
        //sChannel.close();          // server read() 返回-1
        Thread.sleep(20000);
        System.out.println("=======client 退出进程========");
    }

    //服务端
    @Test
    public void server() throws IOException {
        FileChannel outChannel = FileChannel.open(Paths.get("3receive.md"),
                StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        //1. 获取通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        //2. 绑定连接
        ssChannel.bind(new InetSocketAddress(9898));
        System.out.println("=========init accept===========");
        //3. 获取客户端连接的通道
        SocketChannel sChannel = ssChannel.accept();
        System.out.println("=========accept===========");

        Thread t = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("server当前时间: " + new Date().toLocaleString());
            }
        });
        t.setDaemon(true);
        t.start();

        //4. 分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);//1KB   单位字节B

        int i = 1;
        int len = -1;
        //5. 接收客户端的数据，并保存到本地  套接字关闭会收到-1  发送数据结束会收到0
        while((len = sChannel.read(buf)) != -1){
            buf.flip();
           // Thread.sleep(200);
            System.out.println("========接收数据===len="+len+"=======" + i++ + "KB");
            outChannel.write(buf);
            buf.clear();
        }
        System.out.println("========接收数据===len="+len+"=======");

        //6. 关闭通道
        sChannel.close();
        outChannel.close();
        ssChannel.close();
        System.out.println("=======server 退出进程========");
    }
}
