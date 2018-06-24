package com.demo.netty;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 一个NIO服务端的例子
 * @author lec
 * @date 2018/6/24
 */
public class NIODemo {
    //通道管理器
    private Selector selector;

    /**
     * 获取一个ServerSocketChannel 服务器套接字通道，并对通道做一些初始化工作
     * @param port 端口号
     * @throws Exception
     */
    public void initServer(int port) throws Exception {
        //获取一个服务器套接字通道
        ServerSocketChannel channel = ServerSocketChannel.open();

        //设置是否阻塞
        channel.configureBlocking(false);

        //绑定端口
        channel.socket().bind(new InetSocketAddress(port));

        //获取一个通道管理器并赋给selector;
        this.selector = Selector.open();

        //注册监听事件,当未出现监听事件时，线程可以做其他事情，出现监听事件时再返回处理
        channel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器启动成功！");
    }

    /**
     * 使用轮循的方式处理监听事件
     * @throws Exception
     */
    public void listenerHandeler()throws Exception {
        //一直循环，看监听事件是否发生，发生则进行处理
        while (true){
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                //删除已选的key，避免重复处理
                iterator.remove();
                handeler(key);
            }
        }
    }

    /**
     * 处理监听的请求
     * @param key
     * @Exception
     */
    public void handeler(SelectionKey key) throws Exception {
        if (key.isAcceptable()) {
            //如果请求事件为客户端连接事件
            handelerAccept(key);
        } else if (key.isReadable()) {
            //如果请求时间为读取事件
            handelerRead(key);
        }
    }

    /**
     * 处理读取事件
     * @param key
     * @throws Exception
     */
    public void handelerRead(SelectionKey key) throws Exception {
        // 服务器可读取消息:得到事件发生的Socket通道
        SocketChannel channel = (SocketChannel) key.channel();
        // 创建读取的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int read = channel.read(buffer);
        if(read > 0){
            byte[] data = buffer.array();
            String msg = new String(data).trim();
            System.out.println("服务端收到信息：" + msg);

            //回写数据
            ByteBuffer outBuffer = ByteBuffer.wrap("好的".getBytes());
            // 将消息回送给客户端
            channel.write(outBuffer);
        }else{
            System.out.println("客户端关闭");
            key.cancel();
        }
    }

    /**
     * 处理连接事件
     * @param key
     * @throws Exception
     */
    public void handelerAccept(SelectionKey key) throws Exception {
        //通过key获取服务器套接字通道
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();

        //通过服务器套接字通道获取客户端套接字通道
        SocketChannel socketChannel = channel.accept();

        //设置客户端套接字通道为非阻塞
        socketChannel.configureBlocking(false);

        System.out.println("新客户端已连接！");
        //注册读取事件
        socketChannel.register(this.selector,SelectionKey.OP_READ);
    }

    public static void main(String[] args) throws Exception {
        NIODemo server = new NIODemo();
        server.initServer(10086);
        server.listenerHandeler();
    }
}
