package com.zoy.rabbitmq.basic.message;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by zoypong on 2018/10/27.
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

        // ·自定义属性，放到 headers中
        Map<String, Object> myHeader = new HashMap<>();
        myHeader.put("header1", "1111");
        myHeader.put("header2", "2222");

        // ·设置 Message属性
        // ·链式调用，一个点接着一个点
        AMQP.BasicProperties myProperties = new AMQP.BasicProperties().builder()
                .deliveryMode(2) // ·消息送达模式，2 表示消息持久化
                .contentEncoding("UTF-8")
                .expiration("10000") // ·消息超时时间，10s
                .headers(myHeader)
                .build();

        // ·4、通过 Channel发送消息
        for(int i = 0; i < 5; i++) {
            String msg = "Hello,rabbitMQ~~。" + i;
            channel.basicPublish("","test001", myProperties, msg.getBytes());
        }

        // ·5、记得关闭相关连接（从内到外关闭）
        channel.close();
        connection.close();
    }
}
