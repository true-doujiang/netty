package com.yhh.nio.client;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

public class TestBlockingNIO {

	public static void main(String[] args) throws Exception {
		
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

}
