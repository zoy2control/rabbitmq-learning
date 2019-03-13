package com.zoy.rabbitmq.basic.qos;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by zoypong on 2019/03/13.
 */
public class Producer {
    public static void main(String[] args) throws IOException, TimeoutException {
        // ·1、创建 ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        // ·2、获取 Connection
        Connection connection = connectionFactory.newConnection();

        // ·3、通过 Connection创建一个新的 Channel
        Channel channel = connection.createChannel();


        String exchangeName = "test_qos_exchange";
        String routingKey = "test_qos_routingKey.save";

        // ·5、发送消息
        String msg = "hello mq send Qos message";
        for (int i = 0; i < 5; i++) {
            channel.basicPublish(exchangeName, routingKey, true, null, msg.getBytes());
        }
    }
}
