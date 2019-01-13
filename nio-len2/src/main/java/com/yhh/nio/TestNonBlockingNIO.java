package com.yhh.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
 * 			|--SelectableChannel
 * 				|--SocketChannel
 * 				|--ServerSocketChannel
 * 				|--DatagramChannel
 *
 * 				|--Pipe.SinkChannel
 * 				|--Pipe.SourceChannel
 *
 * 2. 缓冲区（Buffer）：负责数据的存取
 *
 * 3. 选择器（Selector）：是 SelectableChannel 的多路复用器。用于监控 SelectableChannel 的 IO 状况
 *
 */

/**
 * 1. 通道触发了一个事件意思是该事件已经就绪。
 * 所以，某个channel成功连接到另一个服务器称为”连接就绪“。（表示客户与服务器的连接已经建立成功）
 * 一个server socket channel准备号接收新进入的连接称为”接收就绪“。（表示服务器监听到了客户连接，服务器可以接收这个连接了）
 * ”读就绪“: 表示通道中已经有了可读的数据，可以执行读操作了（通道目前有数据，可以进行读操作了）
 * ”写就绪“: 表示已经可以向通道写数据了（通道目前可以用于写操作）
 * <p>
 * 2. 通过Selector选择就绪的通道
 * 一旦向Selector注册了一个或多个通道，就可以调用几个重载的select()方法。
 * select()             —— 阻塞到至少有一个通道在你注册的事件上就绪了  ,返回的Int值表示多少通道就绪。
 * select(long timeout) —— 和select()一样，除了最长会阻塞timeout毫秒 ,还没有就绪事件程序就退出了
 * selectNow()          —— 不会阻塞，不管什么通道就绪都立刻返回；此方法执行非阻塞的选择操作，
 * 如果自从上一次选择操作后，没有通道变成可选择的，则此方法直接返回0
 * <p>
 * 一旦调用了select()方法，并且返回值表明有一个或更多个通道就绪了，然后可以通过调用selector的selectorKeys()方法，访问”已选择键集“中的就绪通道
 * Set selectedKeys = selector.selectedKeys();
 */
public class TestNonBlockingNIO {


    /**
     * 非阻塞式的
     */

    private static int clientCount;

    public static void main(String[] args) throws IOException {
        //1. 获取通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        System.out.println("SocketChannel.validOps() = " + ssChannel.validOps()); //

        //2. 切换非阻塞模式
        ssChannel.configureBlocking(false);
        //3. 绑定连接
        ssChannel.bind(new InetSocketAddress(9898));
        //4. 获取选择器
        Selector selector = Selector.open();
        //5. 将通道注册到选择器上, 并且指定“监听接收事件”
        SelectionKey key = ssChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("SelectionKey= " + key + " interestOps() = " + key.interestOps());

        /**
         * 当前有selector.select()阻塞，就直接返回，
         * 当前没有selector.select()阻塞，则下次谁调用selector.select()也不用阻塞，直接返回
         */
        selector.wakeup();

        int count = 0;
        //6. 轮询式的获取选择器上已经“准备就绪”的事件，不知道具体是哪个事件
        while ((count = selector.select()) > 0) {
            System.out.println(Thread.currentThread().getName() + "========select===== count = " + count);

            //7. 获取当前选择器中所有注册的“选择键(已就绪的监听事件)”
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            while (it.hasNext()) {
                //8. 获取准备“就绪”的是事件
                SelectionKey sk = it.next();


                //9. 判断具体是什么事件准备就绪
                if (sk.isAcceptable()) {
                    System.out.println(Thread.currentThread().getName() + "----------Acceptable事件 ------");

                    System.out.println("while +++++ SelectionKey= " + sk + " interestOps() = " + sk.interestOps());

                    //10. 若“连接就绪”，获取客户端连接
                    SocketChannel sChannel = ssChannel.accept();
                    Socket socket = sChannel.socket();

                    // 这种方式也可以获取到
                    //ServerSocketChannel serverSocketChannel = (ServerSocketChannel) sk.channel();
                    //SocketChannel socketChannel = serverSocketChannel.accept();
                    //Socket socket = socketChannel.socket();

                    //11. 客户端连接切换非阻塞模式
                    sChannel.configureBlocking(false);
                    System.out.println(Thread.currentThread().getName() + "=====" + "Acceptable 事件的SocketChannel: " + sChannel);

                    /**
                     * 每次Acceptable事件使用的SelectionKey都是ssChannel.register(....) 返回的那个
                     *
                     * OP_ACCEPT 16
                     */
                    System.out.println("while +++++ SelectionKey= " + sk + "  1 --> SelectionKey.interestOps() = " + key.interestOps());
                    /**
                     * 12. 将该通道注册到选择器上
                     *  也可以在这里注册SelectionKey.OP_WRITE事件
                     *  [SelectionKey key1 = sChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);]，
                     *  检测IO缓冲区是否有空间，有空间sk.isWritable() 返回true。
                     *
                     *  一般IO缓存区都是可用的，没必要注册该事件， 注册了的话，会不停的触发该事件，烦死。
                      */
                    SelectionKey key1 = sChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("while +++++ SelectionKey= " + sk + "  2 --> SelectionKey.interestOps() = " + sk.interestOps());
                    /**
                     *  这个SelectionKey是新的
                     */
                    System.out.println("while +++++ SelectionKey= " + key1 + "  3 --> SelectionKey.interestOps() = " + key1.interestOps());

                    // 有客户端进来
                    clientCount++;
                    String request = "第 " + clientCount + " 个客户端 [" + socket.getRemoteSocketAddress() + "]: ";
                    System.out.println(request);
                    sk.attach(request);

                    //15. 取消选择键 SelectionKey
                    it.remove();

                } else if (sk.isReadable()) {

                    System.out.println(Thread.currentThread().getName() + "---------- Readable事件 ------");
                    /**
                     * 这里的SelectionKey 是Acceptable 事件中对SocketChannel注册OP_READ事件返回的
                     *
                     * OP_READ 1
                     */
                    System.out.println("while +++++ SelectionKey= " + sk + " interestOps() = " + sk.interestOps()); // 1

                    //13. 获取当前选择器上“读就绪”状态的通道.  完全可以把这个SocketChannel扔给线程池去处理。
                    SocketChannel sChannel = (SocketChannel) sk.channel();
                    System.out.println(Thread.currentThread().getName() + "=====" + "Readable事件的 SocketChannel: " + sChannel);

                    //14. 读取数据
                    ByteBuffer buf = ByteBuffer.allocate(1024);

                    try {
                        /**
                         * 直接关闭客户端进程 read() 抛异常
                         */
                        int len = 0;
                        while ((len = sChannel.read(buf)) > 0) {
                            buf.flip();
                            //Thread.sleep(1000);  //
                            System.out.println("len=" + len + " : \n" + new String(buf.array(), 0, len));
                            buf.clear();
                        }

                        /**
                         * server和client已建立链接，此时我把客户端网线拔了，服务端read()=0进入这里，
                         * 之后再把网线插上用的还是原来的socket，直接就能发数据，而且每次再发数据，都会带结束标记，也就是都会len=0
                         * 【网线恢复后并没有重写连接，用的还是原来的socket】
                         */
                        if (len == 0) {
                            System.out.println("客户端本次发送结束,但是有的client发送会进入，有的就不会呢 len=" + len);
                        }

                        /**
                         *   -1在网络io中就是socket关闭的含义  客户端正常调用close() 方法这里收到-1，
                         *   但是如果是直接关闭进程，则会read()异常，进入try-catch
                         */
                        if (len == -1) {
                            System.out.println(Thread.currentThread().getName() + "==== attachment= " + sk.attachment() +" read finished. close socketChannel. ");
                            System.out.println(Thread.currentThread().getName() + "===== read() = -1 正常 close socketChannel = " + sChannel);
                            // 关闭socket
                            sChannel.close();
                        }

                        /**
                         * 如果客户端已经关闭链接你还写就会异常 java.nio.channels.ClosedChannelException
                         * https://blog.csdn.net/github_34606293/article/details/78201154
                         */
                        if (sChannel.isConnected() && !sChannel.socket().isClosed()) {
                            System.out.println(Thread.currentThread().getName() + "===== 回写数据 [hao de] ");
                            //回写数据  将消息回送给客户端
                            ByteBuffer outBuffer = ByteBuffer.wrap("hao de".getBytes());
                            sChannel.write(outBuffer);
                        }

                        //15. 取消选择键 SelectionKey
                        it.remove();

                    } catch (IOException e) {
                        // 如果read抛出异常，表示连接异常中断，需要关闭 socketChannel
                        e.printStackTrace();

                        //15. 取消选择键 SelectionKey
                        it.remove();

                        System.out.println(Thread.currentThread().getName() + "===== attachment= " + sk.attachment() +" read抛出异常. close socketChannel = " + sChannel);

                        // 关闭socket
                        sChannel.close();
                        clientCount--;
                    }
                } else if (sk.isWritable() && sk.isValid()) {
                    /**
                     * 想触发 Writable 事件  需要在 Acceptable事件 对获取到的 [SocketChannel sChannel = ssChannel.accept();] 注册Writable 事件
                     *
                     * 触发 Writable 事件 说明IO缓冲区有空间，可以往客户端发送数据了，一般IO缓存区都是可用的，没必要注册该事件， 注册了的话，会不停的触发该事件，烦死。
                     */
                    System.out.println(Thread.currentThread().getName() + "---------- Writable 事件 ------");
                    System.out.println("while +++++ SelectionKey= " + sk + " interestOps() = " + sk.interestOps()); //

                    /**
                     * sk.isValid() 键在创建时是有效的，并在被取消、其通道已关闭或者其选择器已关闭之前保持有效。
                     */
                    SocketChannel sChannel = (SocketChannel) sk.channel();
                    System.out.println(Thread.currentThread().getName() + "=====" + "Writable 事件的 SocketChannel: " + sChannel);

                    ByteBuffer outBuffer = ByteBuffer.wrap("服务端接收数据成功".getBytes());
                    sChannel.write(outBuffer);

                    //15. 取消选择键 SelectionKey
                    it.remove();
                }

                // 以前remove()同一放在这里

            }
        }//select()

        System.out.println(Thread.currentThread().getName() + "========服务端关闭================");
    }

    //客户端
    @Test
    public void client() throws IOException {

        //新创建的SocketChannel虽已打开却是未连接的。在一个未连接的SocketChannel对象上尝试一个I/O操作会导致NotYetConnectedException异常
        //Exception in thread "main" java.nio.channels.NotYetConnectedException
        //SocketChannel sChannel = SocketChannel.open();

        //1. 获取通道 127.0.0.1
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("39.106.63.228", 9898));

        //2. 切换非阻塞模式
        sChannel.configureBlocking(false);
        //3. 分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        //4. 发送数据给服务端
        Scanner scan = new Scanner(System.in);

        while (scan.hasNext()) {
            String str = scan.next();
            if ("close".equals(str)) {
                break;
            }
            buf.put((new Date().toLocaleString() + "\n" + str).getBytes());
            buf.flip();
            sChannel.write(buf);
            buf.clear();
        }

        //5. 关闭通道
        sChannel.close();


        //
        //multiThread();
    }


    static ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void multiThread() {
        Scanner scan = new Scanner(System.in);
        int str = 0;
        while (scan.hasNext()) {
            str = scan.nextInt();
            if ("close".equals(str + "")) {
                break;
            }

            for (int i = 0; i < str; i++) {
                final int task = i;

                threadPool.execute(new Runnable() {
                    public void run() {
                        try {
                            SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("39.106.63.228", 9898));
                            sChannel.configureBlocking(false);
                            ByteBuffer buf = ByteBuffer.allocate(1024);

                            buf.put((new Date().toLocaleString() + "\n" + "uuu" + task)
                                    .getBytes());
                            buf.flip();
                            sChannel.write(buf);
                            buf.clear();

                            sChannel.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
            }
        }
    }


}
