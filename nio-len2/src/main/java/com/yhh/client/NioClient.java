package com.yhh.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioClient {


    /**
     * https://www.cnblogs.com/gaotianle/p/3325451.html
     * https://blog.csdn.net/ns_code/article/details/15545057
     */
    public static void main(String[] args) throws IOException {
//
//        //获取socket通道
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        Selector selector = Selector.open();

        //客户端连接服务器，需要调用channel.finishConnect();才能实际完成连接。 39.106.63.228  127.0.0.1
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 9898));
        System.out.println("SocketChannel.validOps() = " + socketChannel.validOps()); // 13

        SelectionKey sk = socketChannel.register(selector, SelectionKey.OP_CONNECT);
        System.out.println("SelectionKey= " + sk + " interestOps() = " + sk.interestOps()); // 8

        //轮询访问selector
        int count = 0;
        while ((count = selector.select()) > 0) {
            System.out.println(Thread.currentThread().getName() + "========select===== count = " + count);

            Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
            while (ite.hasNext()) {

                SelectionKey key = ite.next();
                System.out.println("while ++++ SelectionKey= " + sk + " interestOps() = " + sk.interestOps()); //

                //删除已选的key，防止重复处理
                ite.remove();

                if (key.isConnectable()) {
                    SocketChannel channel = (SocketChannel) key.channel();

                    //如果正在连接，则完成连接
                    if (channel.isConnectionPending()) {
                        channel.finishConnect();
                    }

                    channel.configureBlocking(false);

                    channel.write(ByteBuffer.wrap(new String("send message to server.").getBytes()));

                    channel.register(selector, SelectionKey.OP_READ);
                    System.out.println("客户端连接成功");

                } else if (key.isReadable()) {

                    //有可读数据事件。
                    SocketChannel channel = (SocketChannel) key.channel();

                    ByteBuffer buffer = ByteBuffer.allocate(10);
                    channel.read(buffer);
                    byte[] data = buffer.array();
                    String message = new String(data);

                    System.out.println("recevie message from server:, size:" + buffer.position() + " msg: " + message);

                    ByteBuffer outbuffer = ByteBuffer.wrap(("client.".concat(message)).getBytes());
                    channel.write(outbuffer);
                }
            }
        }


        //
        //multiThread();
    }



    static ExecutorService threadPool = Executors.newCachedThreadPool();
    public static void multiThread() {
        Scanner scan = new Scanner(System.in);
        int str = 0;
        while (scan.hasNext()) {
            str = scan.nextInt();
            if ("close".equals(str+"")) {
                break;
            }

            for(int i=0; i< str; i++) {
                final int task = i;

                threadPool.execute(new Runnable() {
                    public void run() {
                        try {

                            //获取socket通道
                            SocketChannel socketChannel = SocketChannel.open();
                            socketChannel.configureBlocking(false);
                            Selector selector = Selector.open();

                            //客户端连接服务器，需要调用channel.finishConnect();才能实际完成连接。
                            socketChannel.connect(new InetSocketAddress("39.106.63.228", 9898));
                            socketChannel.register(selector, SelectionKey.OP_CONNECT);

                            //轮询访问selector
                            int count = 0;
                            while ((count = selector.select()) > 0) {
                                System.out.println(Thread.currentThread().getName() + "========select===== count = " + count);

                                Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
                                while (ite.hasNext()) {

                                    SelectionKey key = ite.next();
                                    //删除已选的key，防止重复处理
                                    ite.remove();

                                    if (key.isConnectable()) {
                                        SocketChannel channel = (SocketChannel) key.channel();

                                        //如果正在连接，则完成连接
                                        if (channel.isConnectionPending()) {
                                            channel.finishConnect();
                                        }

                                        channel.configureBlocking(false);

                                        channel.write(ByteBuffer.wrap(new String("send message to server.").getBytes()));

                                        channel.register(selector, SelectionKey.OP_READ);
                                        System.out.println("客户端连接成功");

                                    } else if (key.isReadable()) {

                                        //有可读数据事件。
                                        SocketChannel channel = (SocketChannel) key.channel();

                                        ByteBuffer buffer = ByteBuffer.allocate(10);
                                        channel.read(buffer);
                                        byte[] data = buffer.array();
                                        String message = new String(data);

                                        System.out.println("recevie message from server:, size:" + buffer.position() + " msg: " + message);

                                        ByteBuffer outbuffer = ByteBuffer.wrap(("client.".concat(message)).getBytes());
                                        channel.write(outbuffer);
                                    }
                                }
                            }


                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
            }

        }
    }
}
