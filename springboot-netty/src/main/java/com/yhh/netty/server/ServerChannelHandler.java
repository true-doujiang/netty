package com.yhh.netty.server;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * Created by jq on 2019/8/6.
 */
@Component
@ChannelHandler.Sharable
public class ServerChannelHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(ServerChannelHandler.class);

    /**
     * 拿到传过来的msg数据，开始处理
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

//        // 预期接收到的值，仅作为验证使用
//        String expectMsg = "000019061137001137002019080616393000000abc0000000000000000000000000000000000000000000000000000000000000000000007680000051246C36F74EA70E0EF1697B79EA2039BC6C1ADB1929B577372F698CF49A19164D004B264F7405A5B9ECFFF85194A18EA99B7D2A6E9D366EA7EB5E052E2B28ECECE8B8021D1AD5936430873406C51400B265FC27ED94D340B6CE4DB876A3F15AFD194C0CD688810357C70C781089A0AA26DFFA6CB64BECD08C0F2E54A78A27D908B95DDD2FD31966E6FEED5A39D157807A31525698E299334ECA0A4C962187725F1B90808DCCC1A07C24E5CD4D0940ED0D4ECA10A12746762279ECF29F746867711FD23F2ADB10D0265A9F766F855594CA3630C19B0DB12BA607ABC7FDCA8164BC4837870ED894D5DB807359D48F4C8BD65E9EA117E87853D66EDE7B7C6612A48CC6F5E19ADB5DF219FFB7E4506B71BD29DEDC0CFBC63F418E89EC3369C96730E381E4CE60E7841B22067B89568A4B0A10EAF230228D40F051BF365FD9A04B22EC51FB3EAFB195EC9FA3DD181E77FB09E474F5E0598ACCDD134478EDA47781D67627B1C7FDA10CE29C2FF56AD9A108A12C2B3EE360CFD53D097C5DA2111FDB3A7BE7EB5923C33268FBE17ADCB805EB646835580A0E1A2E937C108EB19CAFD6D366DD70B93A130E54964DB81F17419B660521EFBB1E5D96EED83D3B026A3CCF972AA5E1DE2A1524C2970738AE1C39F95E73463F532629E052EC3F77EE06A02D2D89ACF535C098F4C2EDE528C8E558F5742C03E00DBD0AA762DFE8540FA1C053C94DB4456EE474DAA190F2B020638C9197C46610D512787C676B91DF9576716EB014B91A8FF9723D29F2CEB9A211DCE4598B70D0A6BCD72F8AFBCCBAC36ACB4CCEADD3095E913CE147D869D7B0E383714CD8AE8A38E710CD050DD0CBAB8CFC4D4F795CB17B50E7A9B982450919ACEE86CC71056309F860BA3E8DBD5CF98B7CE96A34421738228F0E0BF1DD1C34B76FCA61C9EDA2BF3577E611DA9D9F1CFE194A7E96EE53FAB0179A52275C7F713D6BDF7A652C78770FEB0F5FAC9786ED56ACE6A9697022C9795D8EBC52C6E7C43B9D1F76044BA5555AF836A0085278FA19C8C589DCB9C20E7EF461C2FACB65DF99E0A6EC112CFA709F37CA7461B179F878CBDDD80F19F34F00A8E5A6A0731561A512787CF366E8D4DE6EB2AC76C672C5EDA9239000C114E508A821A74C8234FAAF8E0DC84DACB0059949459754B75232A49DF0E31EFA8C999CEA45337B6C3AD0BC263D83EC386DF4CF57EE51495A0A79502E86341AD1E004D7CA5BB3B064155E9552A187E6AAF65D2C15DB679292050F38A453BB607";
        logger.info("服务器接收到一条消息：{}", msg.toString());

//        Assert.isTrue(expectMsg.equals(msg.toString()), "服务器接收到的消息和客户端发送的不一致");

        /**
         * 此时字符串已经完整接收完毕了，自行解析咯(#^.^#)
         */

        //嗅探报文处理
        if (msg.equals("0000")) {
            ctx.channel().writeAndFlush("0").syncUninterruptibly();
        } else {

            String resultMsg = String.valueOf(msg);

            // 如果长度小于8说明消息不完整
            if (resultMsg.length() < 8) {
                logger.error("消息不完整！");
            }

            // 报文长度
            int bodyLength = Integer.valueOf(resultMsg.substring(0, 8));
            logger.info("当前报文内容长度为：" + bodyLength);

            //SYSID
            String SYSID = resultMsg.substring(8, 14);
            logger.info("SYSID: " + SYSID);

            //28位流水号
            String seqNo = resultMsg.substring(14, 42);
            logger.info("seqNo: " + seqNo);

            //预留位
            String reserved = resultMsg.substring(42, 106);

            //encryptPkgDataLen
            int encryptPkgDataLen = Integer.valueOf(String.format("%d", Integer.valueOf(resultMsg.substring(106, 114))));
            logger.info("encryptPkgDataLen: " + encryptPkgDataLen);

            //encryptRndLen
            int encryptRndLen = Integer.valueOf(String.format("%d", Integer.valueOf(resultMsg.substring(114, 122))));
            logger.info("encryptRndLen: " + encryptRndLen);

            //encryptPkgData + encryptRnd + signData
            String encryptPkgRndSignData = resultMsg.substring(122, resultMsg.length());
//        LOGGER.info("encryptPkgRndSignData: " + encryptPkgRndSignData);

            byte[] encryptPkgByte = new byte[encryptPkgDataLen];
            byte[] encryptRndByte = new byte[encryptRndLen];
            byte[] signByte = new byte[encryptPkgRndSignData.length() - encryptPkgDataLen - encryptRndLen];
//        String repoMsg = "000019061137001137002019080616393000000abc0000000000000000000000000000000000000000000000000000000000000000000007680000051246C36F74EA70E0EF1697B79EA2039BC6C1ADB1929B577372F698CF49A19164D004B264F7405A5B9ECFFF85194A18EA99B7D2A6E9D366EA7EB5E052E2B28ECECE8B8021D1AD5936430873406C51400B265FC27ED94D340B6CE4DB876A3F15AFD194C0CD688810357C70C781089A0AA26DFFA6CB64BECD08C0F2E54A78A27D908B95DDD2FD31966E6FEED5A39D157807A31525698E299334ECA0A4C962187725F1B90808DCCC1A07C24E5CD4D0940ED0D4ECA10A12746762279ECF29F746867711FD23F2ADB10D0265A9F766F855594CA3630C19B0DB12BA607ABC7FDCA8164BC4837870ED894D5DB807359D48F4C8BD65E9EA117E87853D66EDE7B7C6612A48CC6F5E19ADB5DF219FFB7E4506B71BD29DEDC0CFBC63F418E89EC3369C96730E381E4CE60E7841B22067B89568A4B0A10EAF230228D40F051BF365FD9A04B22EC51FB3EAFB195EC9FA3DD181E77FB09E474F5E0598ACCDD134478EDA47781D67627B1C7FDA10CE29C2FF56AD9A108A12C2B3EE360CFD53D097C5DA2111FDB3A7BE7EB5923C33268FBE17ADCB805EB646835580A0E1A2E937C108EB19CAFD6D366DD70B93A130E54964DB81F17419B660521EFBB1E5D96EED83D3B026A3CCF972AA5E1DE2A1524C2970738AE1C39F95E73463F532629E052EC3F77EE06A02D2D89ACF535C098F4C2EDE528C8E558F5742C03E00DBD0AA762DFE8540FA1C053C94DB4456EE474DAA190F2B020638C9197C46610D512787C676B91DF9576716EB014B91A8FF9723D29F2CEB9A211DCE4598B70D0A6BCD72F8AFBCCBAC36ACB4CCEADD3095E913CE147D869D7B0E383714CD8AE8A38E710CD050DD0CBAB8CFC4D4F795CB17B50E7A9B982450919ACEE86CC71056309F860BA3E8DBD5CF98B7CE96A34421738228F0E0BF1DD1C34B76FCA61C9EDA2BF3577E611DA9D9F1CFE194A7E96EE53FAB0179A52275C7F713D6BDF7A652C78770FEB0F5FAC9786ED56ACE6A9697022C9795D8EBC52C6E7C43B9D1F76044BA5555AF836A0085278FA19C8C589DCB9C20E7EF461C2FACB65DF99E0A6EC112CFA709F37CA7461B179F878CBDDD80F19F34F00A8E5A6A0731561A512787CF366E8D4DE6EB2AC76C672C5EDA9239000C114E508A821A74C8234FAAF8E0DC84DACB0059949459754B75232A49DF0E31EFA8C999CEA45337B6C3AD0BC263D83EC386DF4CF57EE51495A0A79502E86341AD1E004D7CA5BB3B064155E9552A187E6AAF65D2C15DB679292050F38A453BB607";

            ctx.channel().writeAndFlush("0").syncUninterruptibly();

        }

    }

    /**
     * 活跃的、有效的通道
     * 第一次连接成功后进入的方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("tcp client " + getRemoteAddress(ctx) + " connect success");
        //往channel map中添加channel信息
        NettyTcpServer.map.put(getIPString(ctx), ctx.channel());
    }

    /**
     * 不活动的通道
     * 连接丢失后执行的方法（client端可据此实现断线重连）
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //删除Channel Map中的失效Client
        NettyTcpServer.map.remove(getIPString(ctx));
        ctx.close();
    }

    /**
     * 异常处理
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        //发生异常，关闭连接
        logger.error("引擎 {} 的通道发生异常，即将断开连接", getRemoteAddress(ctx));
        ctx.close();//再次建议close
    }

    /**
     * 心跳机制，超时处理
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String socketString = ctx.channel().remoteAddress().toString();
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                logger.info("Client: " + socketString + " READER_IDLE 读超时");
                ctx.disconnect();//断开
            } else if (event.state() == IdleState.WRITER_IDLE) {
                logger.info("Client: " + socketString + " WRITER_IDLE 写超时");
                ctx.disconnect();
            } else if (event.state() == IdleState.ALL_IDLE) {
                logger.info("Client: " + socketString + " ALL_IDLE 总超时");
                ctx.disconnect();
            }
        }
    }

    /**
     * 获取client对象：ip+port
     *
     * @param ctx
     * @return
     */
    public String getRemoteAddress(ChannelHandlerContext ctx) {
        String socketString = "";
        socketString = ctx.channel().remoteAddress().toString();
        return socketString;
    }

    /**
     * 获取client的ip
     *
     * @param ctx
     * @return
     */
    public String getIPString(ChannelHandlerContext ctx) {
        String ipString = "";
        String socketString = ctx.channel().remoteAddress().toString();
        int colonAt = socketString.indexOf(":");
        ipString = socketString.substring(1, colonAt);
        return ipString;
    }


}
