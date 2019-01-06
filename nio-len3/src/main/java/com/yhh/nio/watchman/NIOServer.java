package com.yhh.nio.watchman;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {

    private int countMsg =  1;
    private final int blockSize = 4096;
    private ByteBuffer sendbuffer = ByteBuffer.allocate(blockSize);
    private ByteBuffer receivebuffer = ByteBuffer.allocate(blockSize);

    private Selector selector;

    public NIOServer (int port) throws IOException {
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        //设置非阻塞模式
        ssChannel.configureBlocking(false);

        ServerSocket serverSocket = ssChannel.socket();
        //绑定ip和端口
        serverSocket.bind(new InetSocketAddress(port));
        //打开选择器
        selector = Selector.open();
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("server start --> " + port);
    }

    //监听客户端连接
    public void listen () throws IOException {
        while (true) {
            int select = selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()) {
                SelectionKey selectionKey = it.next();
                it.remove();
                //业务处理
                handleKey(selectionKey);
            }
        }
    }

    public void handleKey(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel server = null;
        SocketChannel client = null;
        String receiveText;
        String sendText;
        int count = 0;

        if (selectionKey.isAcceptable()) {
            //获取服务端连接
            server = (ServerSocketChannel) selectionKey.channel();
            //接收客户端连接
            client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } else if (selectionKey.isReadable()){
            client = (SocketChannel) selectionKey.channel();
            count = client.read(receivebuffer);
            if (count>0) {
                receiveText = new String(receivebuffer.array(), 0, count);
                System.out.println("服务端接收到客户端的信息: <-----" + receiveText);

                //客户端注册OP_WRITE
                client.register(selector, selectionKey.OP_WRITE);
            }
        } else if (selectionKey.isWritable()) {
            sendbuffer.clear();
            client = (SocketChannel) selectionKey.channel();
            //发送数据
            sendText = "今天是大晴天: " + (countMsg++) + " 日期：" + new Date().toLocaleString();
            sendbuffer.put(sendText.getBytes());
            sendbuffer.flip();
            client.write(sendbuffer);
            System.out.println("服务端发送数据到客户端: ----->" + sendText);
        }
    }


    public static void main(String[] args) throws IOException {
        NIOServer nioServer = new NIOServer(8888);
        nioServer.listen();
    }
}

