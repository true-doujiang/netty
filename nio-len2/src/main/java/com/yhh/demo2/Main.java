package com.yhh.demo2;

import javax.swing.plaf.TableHeaderUI;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws IOException {
        new Reactor(8081).run();
    }
}

/**
 * Basic Reactor Design
 */
class Reactor implements Runnable {


    final Selector selector;
    final ServerSocketChannel serverSocket;

    Reactor(int port) throws IOException { //Reactor初始化
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(port));
        //非阻塞
        serverSocket.configureBlocking(false);
        //分步处理,第一步,接收accept事件
        SelectionKey sk = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        //attach callback object, Acceptor
        Acceptor acceptorAttach = new Acceptor();
        System.out.println(Thread.currentThread().getName() + " acceptorAttach = " + acceptorAttach);
        sk.attach(acceptorAttach);
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                // 轮训IO、accept事件
                selector.select();
                Set<SelectionKey> selected = selector.selectedKeys();

                System.out.println(Thread.currentThread().getName() + " selected.size = " + selected.size());

                Iterator<SelectionKey> it = selected.iterator();
                while (it.hasNext()) {
                    SelectionKey clientsk = it.next();
                    System.out.println(Thread.currentThread().getName() + " clientSK = " + clientsk);
                    //Reactor负责dispatch收到的事件
                    dispatch(clientsk);
                }

                selected.clear();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void dispatch(SelectionKey k) {
        // 调用之前注册的callback对象
        Runnable r = (Runnable) (k.attachment());
        System.out.println(Thread.currentThread().getName() + " dispatch.r = " + r);

        if (r != null) {
            r.run();
        }

    }

    /**
     * inner class
     * attachment
     */
    class Acceptor implements Runnable {

        @Override
        public void run() {
            try {
                SocketChannel c = serverSocket.accept();
                if (c != null) {
                    Handler2 handler = new Handler2(selector, c);
                    System.out.println(Thread.currentThread().getName() + " SocketChannel = " + c + " handler = " + handler);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}

/**
 *
 */
final class Handler implements Runnable {

    final SocketChannel socket;
    final SelectionKey sk;

    ByteBuffer input = ByteBuffer.allocate(10000);
    ByteBuffer output = ByteBuffer.allocate(10000);

    static final int READING = 0;
    static final int SENDING = 1;
    int state = READING;

    Handler(Selector sel, SocketChannel c) throws IOException {
        socket = c;
        c.configureBlocking(false);
        // Optionally try first read now
        sk = socket.register(sel, 0);
        //将Handler作为callback对象
        sk.attach(this);
        //第二步,接收Read事件
        sk.interestOps(SelectionKey.OP_READ);
        sel.wakeup();
    }


    @Override
    public void run() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void read() throws IOException {
        socket.read(input);
        if (inputIsComplete()) {
            // 转换为读取模式
            input.flip();
            byte b = input.get();
            System.out.println("read:" + (char) b);

            state = SENDING;
            // Normally also do first write now
            //第三步,接收write事件
            sk.interestOps(SelectionKey.OP_WRITE);
        }
    }

    void send() throws IOException {
        socket.write(output);
        if (outputIsComplete()) {
            //write完就结束了, 关闭select key
            sk.cancel();
        }
    }

    boolean inputIsComplete() {
        /* ... */
        System.out.println(Thread.currentThread().getName() + " inputIsComplete");
        return true;
    }

    boolean outputIsComplete() {
        /* ... */
        System.out.println(Thread.currentThread().getName() + " outputIsComplete");
        return false;
    }

}

//上面 的实现用Handler来同时处理Read和Write事件, 所以里面出现状态判断
//我们可以用State-Object pattern来更优雅的实现
//class Handler { // ...
//
//
//    public void run() { // initial state is reader
//        socket.read(input);
//        if (inputIsComplete()) {
//            process();
//            //状态迁移, Read后变成write, 用Sender作为新的callback对象
//            sk.attach(new Sender());
//            sk.interest(SelectionKey.OP_WRITE);
//            sk.selector().wakeup();
//        }
//    }
//
//    class Sender implements Runnable {
//        @Override
//        public void run() { // ...
//            socket.write(output);
//            if (outputIsComplete()) sk.cancel();
//        }
//    }
//}


/**
 *
 */
class Handler2 implements Runnable {

    // uses util.concurrent thread pool
    static ExecutorService pool = Executors.newFixedThreadPool(10);
    static final int PROCESSING = 3;

    final SocketChannel socket;
    final SelectionKey sk;

    ByteBuffer input = ByteBuffer.allocate(10);
    ByteBuffer output = ByteBuffer.allocate(10);

    static final int READING = 0;
    static final int SENDING = 1;
    int state = READING;

    Handler2(Selector sel, SocketChannel c) throws IOException {
        socket = c;
        c.configureBlocking(false);
        // Optionally try first read now
        sk = socket.register(sel, 0);
        //将Handler作为callback对象
        sk.attach(this);
        //第二步,接收Read事件
        sk.interestOps(SelectionKey.OP_READ);
        sel.wakeup();
    }



    @Override
    public void run() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    // ...
    synchronized void read() throws IOException {
        socket.read(input);
        if (inputIsComplete()) {
            state = PROCESSING;
            //使用线程pool异步执行
            pool.execute(new Processer());
        }
    }

    void send() throws IOException {
        output.clear();
        output.put("hi".getBytes());
        output.flip();
        socket.write(output);
        if (outputIsComplete()) {
            //write完就结束了, 关闭select key
            sk.cancel();
        }
    }


    boolean inputIsComplete() {
        /* ... */
        System.out.println(Thread.currentThread().getName() + " inputIsComplete");
        return true;
    }

    boolean outputIsComplete() {
        /* ... */
        System.out.println(Thread.currentThread().getName() + " outputIsComplete");
        return false;
    }

    synchronized void processAndHandOff() {
        //process();
        // 转换为读取模式
        input.flip();
        byte b = input.get();
        System.out.println(Thread.currentThread().getName() + " read:" + (char) b);

        // or rebind attachment
        state = SENDING;
        //process完,开始等待write事件
        //sk.interestOps(SelectionKey.OP_WRITE);
    }

    class Processer implements Runnable {

        @Override
        public void run() {
            processAndHandOff();
        }
    }

}