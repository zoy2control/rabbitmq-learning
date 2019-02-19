package com.zoy.rabbitmq.basic.quickstart;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by zoypong on 2018/10/21.
 */
public class Producer {
    public static void main(String[] args) throws IOException, TimeoutException {
        // ·1、创建一个ConnectionFactory，并配置
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        // ·2、通过 连接工厂创建 Connection
        Connection connection = connectionFactory.newConnection();
        // ·3、通过 Connection创建 Channel
        Channel channel = connection.createChannel();

        // ·4、通过 Channel发送消息
        for(int i = 0; i < 5; i++) {
            String msg = "Hello,rabbitMQ~~。" + i;
            channel.basicPublish("","test001",null,msg.getBytes());
        }

        // ·5、记得关闭相关连接（从内到外关闭）
        channel.close();
        connection.close();
    }
}
