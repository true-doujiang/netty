package com.imooc.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 客户端线程类，专门接收服务器端响应信息
 */
public class NioClientHandler implements Runnable {

    private Selector selector;

    public NioClientHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            for (;;) {
                int readyChannels = selector.select();

                if (readyChannels == 0) continue;

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator iterator = selectionKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey selectionKey = (SelectionKey) iterator.next();
                    iterator.remove();
                    if (selectionKey.isReadable()) {
                        // 可读事件处理器
                        this.readHandler(selectionKey, selector);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 可读事件处理器
     *
     * @param selectionKey
     * @param selector
     * @throws IOException
     */
    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {

        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        String response = "";
        while (socketChannel.read(byteBuffer) > 0) {
            byteBuffer.flip();
            response += Charset.forName("UTF-8").decode(byteBuffer);
        }
        // TODO
        //socketChannel.register(selector, SelectionKey.OP_READ);

        // 将服务器端响应信息打印到本地
        if (response.length() > 0) {
            System.out.println(response);
        }
    }

}
