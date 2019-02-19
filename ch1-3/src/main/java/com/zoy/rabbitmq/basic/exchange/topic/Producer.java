package com.zoy.rabbitmq.basic.exchange.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by zoypong on 2018/10/25.
 */
public class Producer {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "test_topic_exchange";
        String routingKey1 = "test.01";
        String routingKey2 = "test.02";
        String routingKey3 = "test.03.04";

        String msg = "RabbitMQ 4 Direct Exchange Producer Message";
        channel.basicPublish(exchangeName, routingKey1, null,msg.getBytes());
        channel.basicPublish(exchangeName, routingKey2, null,msg.getBytes());
        channel.basicPublish(exchangeName, routingKey3, null,msg.getBytes());

        channel.close();
        connection.close();
    }
}
