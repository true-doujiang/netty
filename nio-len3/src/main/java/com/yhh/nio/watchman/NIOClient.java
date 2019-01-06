package com.yhh.nio.watchman;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


/**

 */
public class NIOClient {

    private static int countMsg =  1;
    private static final int blockSize = 4096;
    private static ByteBuffer sendbuffer = ByteBuffer.allocate(blockSize);
    private static ByteBuffer receivebuffer = ByteBuffer.allocate(blockSize);
    private final static InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 8888);


    public static void main(String[] args) throws IOException {
        SocketChannel sChannel = SocketChannel.open();
        sChannel.configureBlocking(false);
        Selector selector = Selector.open();

        sChannel.register(selector, SelectionKey.OP_CONNECT);
        //发起连接操作
        sChannel.connect(serverAddress);

        Set<SelectionKey> selectionKeys;
        Iterator<SelectionKey> it;
        SelectionKey selectionKey;
        SocketChannel client;
        String receiveText;
        String sendText;
        int count = 0;

        //像服务端一样监听事件
        while (true) {
            selectionKeys = selector.selectedKeys();
            it = selectionKeys.iterator();
            while (it.hasNext()) {
                selectionKey = it.next();

                if (selectionKey.isConnectable()) {
                    System.out.println("client connect");
                    client = (SocketChannel) selectionKey.channel();

                    //判断是否真的连接成功
                    if (client.isConnectionPending()) {
                        client.finishConnect();
                        System.out.println("客户端完成连接操作 connected");

                        sendbuffer.clear();
                        sendbuffer.put("Hello Server".getBytes());
                        sendbuffer.flip();
                        client.write(sendbuffer);
                    }
                    client.register(selector, SelectionKey.OP_READ);

                } else if (selectionKey.isReadable()) {
                    client = (SocketChannel) selectionKey.channel();
                    receivebuffer.clear();
                    count = client.read(receivebuffer);
                    if (count > 0) {
                        receiveText = new String(receivebuffer.array(), 0, count);
                        System.out.println("客户端接收到服务端的数据:<-------" + receiveText);
                        client.register(selector, SelectionKey.OP_WRITE);
                    }

                } else if (selectionKey.isWritable()) {
                    sendbuffer.clear();
                    client = (SocketChannel) selectionKey.channel();
                    sendText = "今天天气怎么样" + countMsg++;
                    sendbuffer.put(sendText.getBytes());
                    sendbuffer.flip();
                    client.write(sendbuffer);
                    System.out.println("客户端发送数据到服务端: ------->" + sendText);
                    client.register(selector, SelectionKey.OP_READ);
                }
            }
            //
            selectionKeys.clear();
        }
    }

}
