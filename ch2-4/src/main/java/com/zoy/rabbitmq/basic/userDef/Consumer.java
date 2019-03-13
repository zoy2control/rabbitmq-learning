package com.zoy.rabbitmq.basic.userDef;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by zoypong on 2019/03/13.
 */
public class Consumer {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        // ·1、创建 ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        // ·2、获取 Connection
        Connection connection = connectionFactory.newConnection();

        // ·3、通过 Connection创建一个新的 Channel
        Channel channel = connection.createChannel();

        String exchangeName = "test_userDef_exchange";
        String routingKey = "test_userDef_routingKey.*";
        String queueName = "test_userDef_queue";

        // ·4、声明 交换机和 队列，并进行 绑定设置
        channel.exchangeDeclare(exchangeName, "topic", true);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);

        // ·5、创建 自定义消费者
        channel.basicConsume(queueName, true, new MyConsumer(channel));

    }
}
