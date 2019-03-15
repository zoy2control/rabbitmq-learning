package com.zoy.rabbitmq.basic.qos;

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

        String exchangeName = "test_qos_exchange";
        String routingKey = "test_qos_routingKey.*";
        String queueName = "test_qos_queue";

        // ·4、声明 交换机和 队列，并进行 绑定设置
        channel.exchangeDeclare(exchangeName, "topic", true);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);

        // ·5、消费端限流，第一件事情就是将 autoAck设置为 false。表示 不进行自动签收
        // ·prefetchCount，表示一次接受多少条消息
        channel.basicQos(0, 1, false);

        // ·6、创建 自定义消费者。autoAck为 false
        channel.basicConsume(queueName, false, new MyConsumer(channel));

    }
}
