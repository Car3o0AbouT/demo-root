package com.demo.netty;


import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lec on 2018/6/7.
 */
public class SocketDemo {
    public static void main(String[] args) throws Exception{
        //建立一个服务端socket对象
        ServerSocket serverSocket = new ServerSocket(10086);
        ExecutorService threadPool = Executors.newCachedThreadPool();
        System.out.println("服务器启动了！");
        while (true){
            //获取客户端socket对象
            final Socket socket = serverSocket.accept();
            System.out.println("新进来了一个客户端！");
            //处理业务逻辑
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    handeler(socket);
                }
            });
        }
    }

    private static void handeler(Socket socket) {
        try {
            byte[] bytes = new byte[1024];
            InputStream in = socket.getInputStream();
            while (true) {
                int read = in.read(bytes);
                if (read != -1) {
                    System.out.println(new String(bytes, 0, read));
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("socket关闭！");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
