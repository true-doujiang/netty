//package com.yhh.nio;
//
//import java.net.Socket;
//import java.nio.ByteBuffer;
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
//import java.nio.channels.ServerSocketChannel;
//import java.nio.channels.SocketChannel;
//
//public class Test {
//
//    private static int clientCount;
//    //4. 获取选择器
//    static Selector selector = Selector.open();
//
//    public static void handleSelectionKey(SelectionKey selectionKey)
//            throws Exception {
//        if (selectionKey.isAcceptable()) {
//
//            // 有客户端进来
//            clientCount++;
//
//            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
//            SocketChannel socketChannel = serverSocketChannel.accept();
//            socketChannel.configureBlocking(false);
//            Socket socket = socketChannel.socket();
//
//            // 立即注册一个 OP_READ 的SelectionKey, 接收客户端的消息
//            SelectionKey key = socketChannel.register(selector, SelectionKey.OP_READ);
//            key.attach("第 " + clientCount + " 个客户端 ["
//                    + socket.getRemoteSocketAddress() + "]: ");
//
//            p(key.attachment()
//                    + "\t[接入] =========================================");
//
//        } else if (selectionKey.isReadable()) {
//
//            ByteBuffer byteBuffer= ByteBuffer.allocate(100);
//            SocketChannel socketChannel=(SocketChannel)selectionKey.channel();
//
//            try
//            {
//                int len=socketChannel.read(byteBuffer);
//                String newMsg="";
//                // 如果len>0，表示有输入。如果len==0, 表示输入结束。需要关闭
//                // socketChannel
//                if (len>0)
//                {
//
//
//
//                } else
//                {
//                    // 输入结束，关闭 socketChannel
//                    p(selectionKey.attachment()
//                            +"read finished. close socketChannel. ");
//                    removelist(selectionKey);
//                    socketChannel.close();
//                    clientCount--;
//                }
//
//            } catch (Exception e)
//            {
//
//                // 如果read抛出异常，表示连接异常中断，需要关闭
//                // socketChannel
//                // e.printStackTrace();
//                removelist(selectionKey);
//                p(selectionKey.attachment() +"第一个客户端关闭了 ");
//                socketChannel.close();
//                clientCount--;
//            }
//
//        } else
//        if (selectionKey.isWritable())
//        {
//            p(selectionKey.attachment()
//                    +"TODO: isWritable() ???????????????????????????? ");
//        } else
//        if (selectionKey.isConnectable())
//        {
//            p(selectionKey.attachment()
//                    +"TODO: isConnectable() ????????????????????????? ");
//        } else
//        {
//            p(selectionKey.attachment()
//                    +"TODO: else. ");
//        }
//
//    }
//}
