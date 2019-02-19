package com.zoy.rabbitmq.basic.exchange.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by zoypong on 2018/10/24.
 */
public class Pruducer {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "test_direct_exchange";
        String routingKey = "test.direct";

        String msg = "RabbitMQ 4 Direct Exchange Producer Message";
        channel.basicPublish(exchangeName, routingKey, null,msg.getBytes());

        channel.close();
        connection.close();
    }
}
