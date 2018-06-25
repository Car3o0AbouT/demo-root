package com.demo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * 自定义处理器
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if(msg instanceof HttpRequest){
            //要返回的内容, Channel可以理解为连接，而连接中传输的信息要为ByteBuf
            ByteBuf content = Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8);

            //构造响应
            FullHttpResponse response =
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

            //设置头信息的的MIME类型
            response.headers().set(DefaultHttpHeaders.Names.CONTENT_TYPE, "text/plain");
            //设置要返回的内容长度
            response.headers().set(DefaultHttpHeaders.Names.CONTENT_LENGTH, content.readableBytes());
            //将响应对象返回
            ctx.writeAndFlush(response);
        }
    }

    /**
     * 通道注册成功
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel register...");
        super.channelRegistered(ctx);
    }

    /**
     * 自定义的Handler被添加,也就是在TestChannelnitializer的initChannel方法中，
     * pipeline.addLast("testHttpServerHandler", new TestHttpServerHandler());
     * 这行代码执行的时候，该方法被触发
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handler added...");
        super.handlerAdded(ctx);
    }

    /**
     * 通道处于活动状态，即可用状态
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active...");
        super.channelActive(ctx);
    }

    /**
     * 通道处于不活动状态
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel inactive...");
        super.channelInactive(ctx);
    }

    /**
     * 通道取消注册
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel unregister...");
        super.channelUnregistered(ctx);
    }
}