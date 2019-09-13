package com.yhh.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 基于消息体头部定义长度的解码器
 */
public class HeadLengthBasedFrameDecoder extends ByteToMessageDecoder {


    private static final Logger logger = LoggerFactory.getLogger(HeadLengthBasedFrameDecoder.class);



    // 临时使用，为了清晰的看出decode接收的次数
    private int count = 1;
    /**
     * 消息体的总长度
     */
    private int messageLength;

    /**
     * 表明消息长度的字节数量（头部有多少位是代表消息长度的）
     */
    private int headLength;

    public HeadLengthBasedFrameDecoder(int headLength) {
        this.messageLength = 0;
        this.headLength = headLength;
    }


    /**
     * decode方法，用于接收消息，若方法中return了，代表本次不保存数据，继续循环接收，每次接收到的数据，保存在in对象中
     * <p>
     * 传递过来的消息是一个字符串，但是字符串前8位定义了整条消息的长度
     * 经过测试，本条消息的完整长度 = 8 + 消息长度
     * <p>
     * 解码原理就是先得知将要接收消息的总长度，消息长度为1906加上前面8位，一共需要接收1914个字节的消息
     * 第一步：判断本次接收的字节数够不够我们获取消息的长度（够不够8位），若够，就取出来长度，将ByteBuf读下标归零，相当于我们拿了8个字节又给它放回去，但是我们已经知道消息长度了
     * 第二步：由于第一次我们没有存消息（我们使用了return，表示不取出消息，继续接收），所以此次接收到的内容会加上第一次接收的，判断消息长度是否达到1914，若没有的话继续接收，直到接收到的字节等于1914
     *
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        logger.info("第{}次接收，in一共接收了{} 位", count, in.readableBytes());
        count++;

        if (in.readableBytes() == 4) {
            //嗅探报文
            ByteBuf sniffBuf = in.readBytes(4);
            byte[] sniffArr = new byte[4];
            sniffBuf.readBytes(sniffArr);
            out.add(new String(sniffArr));
        }


        // 此次接收的数据达不到获取长度的位数，直接返回
        if (in.readableBytes() < this.headLength) {
            return;
        }

        // 若还没获取过消息总长度，则获取
        if (this.messageLength == 0) {

            logger.info("开始获取消息体总长度");
            // 记录长度的头
            ByteBuf headLengthByteBuf = in.readBytes(this.headLength);

            // 字节数组的长度等于headLengthByteBuf的容量（此处数值为this.headLength的值）
            byte[] headLengthArray = new byte[headLengthByteBuf.capacity()];

            // 从headLengthByteBuf中读取字节数组，设置到headLengthArray
            headLengthByteBuf.readBytes(headLengthArray);

            this.messageLength = Integer.parseInt(new String(headLengthArray)) + this.headLength;
            logger.info("获取消息体总长度完毕，共{}字节，当前已经接收到了：{}", this.messageLength, in.readableBytes());

            // 将buf读下标归零，因为前面已经读了this.headLength个字节，将其设置为读下标的起始位置了，而我们并未将这个值保存，所以归零，让它放在buf里面继续接收
            in.resetReaderIndex();
        }

        // ByteBuf.readableBytes() 表示当前有多少个字节可读
        // 若接收到的数据小于数据的总长度，则继续接收
        if (in.readableBytes() < messageLength) {
            logger.info("消息未达到最大长度，当前readIndex为：" + in.readerIndex() + "继续接收消息......");
            return;
        }

        logger.info("接收完毕，共收到{}字节，开始解析消息", this.messageLength);

        // 如果走到这一步，就说明消息已经达到总长度了，可以直接返回，handler的channelRead0方法就会接收到一条完整的消息
        byte[] message = new byte[this.messageLength];
        in.readBytes(message);
        out.add(new String(message));
    }


}
