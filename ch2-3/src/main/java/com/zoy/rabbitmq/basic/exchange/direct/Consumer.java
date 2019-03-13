package com.zoy.rabbitmq.basic.exchange.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by zoypong on 2018/10/24.
 */
public class Consumer {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        // ·是否超时重连
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(3000);

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "test_direct_exchange";
        String exchangeType = "direct";
        String queueName = "test_direct_queue";
        String routingKey = "test.direct";

        // ·声明 交换机和 队列，并将他们做 绑定
        channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);

        // ·声明 consumer
        QueueingConsumer consumer = new QueueingConsumer(channel);
        // ·设置 channel
        channel.basicConsume(queueName, true, consumer);

        // ·获取消息
        while (true) {
            Delivery delivery = consumer.nextDelivery();
            String recvMsg = new String(delivery.getBody());
            System.out.println("消费者收到消息：" + recvMsg);
        }
    }
}
