package com.yhh.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;


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


    public static void main(String[] args) throws IOException {
		//1. 获取通道
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		//2. 切换非阻塞模式
		ssChannel.configureBlocking(false);
		//3. 绑定连接
		ssChannel.bind(new InetSocketAddress(9898));
		//4. 获取选择器
		Selector selector = Selector.open();
		//5. 将通道注册到选择器上, 并且指定“监听接收事件”
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);

		System.out.println("========init================");
		//6. 轮询式的获取选择器上已经“准备就绪”的事件，不知道具体是哪个事件
		while(selector.select() > 0){
			System.out.println("========select================");
			//7. 获取当前选择器中所有注册的“选择键(已就绪的监听事件)”
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();

			while(it.hasNext()){
				//8. 获取准备“就绪”的是事件
				SelectionKey sk = it.next();
				System.out.println("========hasNext================");
				//9. 判断具体是什么事件准备就绪
				if(sk.isAcceptable()){
					System.out.println("========isAcceptable================");
					//10. 若“接收就绪”，获取客户端连接
					SocketChannel sChannel = ssChannel.accept();
					//11. 客户端连接切换非阻塞模式
					sChannel.configureBlocking(false);
					//12. 将该通道注册到选择器上
					sChannel.register(selector, SelectionKey.OP_READ);
				}else if(sk.isReadable()){
					System.out.println("========isReadable================");
					//13. 获取当前选择器上“读就绪”状态的通道
					SocketChannel sChannel = (SocketChannel) sk.channel();
					//14. 读取数据
					ByteBuffer buf = ByteBuffer.allocate(1024);

					int len = 0;
					while((len = sChannel.read(buf)) > 0 ){
						buf.flip();
						System.out.println(new String(buf.array(), 0, len));
						buf.clear();
					}
				}
				//15. 取消选择键 SelectionKey
				it.remove();
			}
		}
    }

    //客户端
    @Test
    public void client() throws IOException {

        //新创建的SocketChannel虽已打开却是未连接的。在一个未连接的SocketChannel对象上尝试一个I/O操作会导致NotYetConnectedException异常
        //Exception in thread "main" java.nio.channels.NotYetConnectedException
        //SocketChannel sChannel = SocketChannel.open();

        //1. 获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        //2. 切换非阻塞模式
        sChannel.configureBlocking(false);
        //3. 分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        //4. 发送数据给服务端
        Scanner scan = new Scanner(System.in);

        while (scan.hasNext()) {
            String str = scan.next();
            //JDK1.8出了一套全新的dateAPI没用成
            buf.put((new Date().toString() + "\n" + str).getBytes());
            buf.flip();
            sChannel.write(buf);
            buf.clear();
        }

        //5. 关闭通道
        sChannel.close();
    }

    //服务端
    @Test
    public void server() throws IOException, InterruptedException {
        System.out.println(Thread.currentThread().getName());
        //1. 获取通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        //2. 切换非阻塞模式
        ssChannel.configureBlocking(false);
        //3. 绑定连接
        ssChannel.bind(new InetSocketAddress(9898));
        //4. 获取选择器
        Selector selector = Selector.open();
        //5. 将通道注册到选择器上, 并且指定“监听接收事件”
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println(Thread.currentThread().getName() + "========init================");
        int count = 0;
        //6. 轮询式的获取选择器上已经“准备就绪”的事件，不知道具体是哪个事件
        while ((count = selector.select()) > 0) {
            System.out.println(Thread.currentThread().getName() + "========select===== count = " + count);

            //7. 获取当前选择器中所有注册的“选择键(已就绪的监听事件)”
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            while (it.hasNext()) {
                //8. 获取准备“就绪”的是事件
                SelectionKey sk = it.next();
                System.out.println(Thread.currentThread().getName() + "========hasNext================");

                //9. 判断具体是什么事件准备就绪
                if (sk.isAcceptable()) {
                    System.out.println(Thread.currentThread().getName() + "========isAcceptable================");
                    //10. 若“接收就绪”，获取客户端连接
                    SocketChannel sChannel = ssChannel.accept();
                    //11. 客户端连接切换非阻塞模式
                    sChannel.configureBlocking(false);
                    System.out.println("isAcceptable的: " + sChannel);

                    //12. 将该通道注册到选择器上
                    sChannel.register(selector, SelectionKey.OP_READ);
                } else if (sk.isReadable()) {
                    System.out.println(Thread.currentThread().getName() + "========isReadable================");
                    //13. 获取当前选择器上“读就绪”状态的通道.  完全可以把这个SocketChannel扔给线程池去处理。
                    SocketChannel sChannel = (SocketChannel) sk.channel();
                    System.out.println("isReadable: " + sChannel);

                    //14. 读取数据
                    ByteBuffer buf = ByteBuffer.allocate(1024);

                    int len = 0;
                    while ((len = sChannel.read(buf)) > 0) {
                        buf.flip();
                        //Thread.sleep(1000);  //
                        System.out.println(new String(buf.array(), 0, len));
                        buf.clear();
                    }
                }

                System.out.println(Thread.currentThread().getName() + "========remove================");
                //15. 取消选择键 SelectionKey
                it.remove();
            }
        }//select()

        System.out.println(Thread.currentThread().getName() + "========服务端关闭================");
    }
}
