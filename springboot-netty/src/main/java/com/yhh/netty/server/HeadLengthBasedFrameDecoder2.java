package com.yhh.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jq on 2019/8/12.
 */
public class HeadLengthBasedFrameDecoder2 extends ByteToMessageDecoder {

    /**
     * 消息体的总长度
     */
    private int messageLength;

    /**
     * 表明消息长度的字节数量（头部有多少位是代表消息长度的）
     */
    private int headLength;

    public HeadLengthBasedFrameDecoder2(int headLength) {
        this.messageLength = 0;
        this.headLength = headLength;
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        // 此次接收的数据达不到获取长度的位数，直接返回
        if(in.readableBytes() < 4) {
            return;
        }

        // 若还没获取过消息总长度，则获取
        if(this.messageLength == 0) {

            if(in.writerIndex() < headLength) {
                this.headLength = 4;
            }

            if(!in.hasArray()) {
                handleDirectBuf(in);
            } else {
                handleHeapBuf(in);
            }
        }


        // 若接收到的数据小于数据的总长度，则继续接收
        if (in.readableBytes() < messageLength) {
            return;
        }

        // 如果走到这一步，就说明消息已经达到总长度了，可以直接返回，handler的channelRead0方法就会接收到一条完整的消息
        byte[] message = new byte[this.messageLength];
        in.readBytes(message);
        out.add(new String(message));

    }

    /**
     * 处理使用直接内存的ByteBuf
     * @param in
     */
    private void handleDirectBuf(ByteBuf in) {
        // 如果使用的是直接内存
        byte[] headLengthArray = new byte[this.headLength];
        in.getBytes(in.readerIndex(), headLengthArray);
        this.messageLength = Integer.parseInt(new String(headLengthArray)) + this.headLength;
    }


    /**
     * 处理使用堆内存的ByteBuf
     * @param in
     */
    private void handleHeapBuf(ByteBuf in) {
        // 如果使用的是堆内存
        byte[] headLengthArray = in.array();
        this.messageLength = Integer.parseInt(new String(Arrays.copyOf(headLengthArray, headLength))) + this.headLength;
    }


}
