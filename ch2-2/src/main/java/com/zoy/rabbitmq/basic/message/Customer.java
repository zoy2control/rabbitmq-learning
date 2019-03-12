package com.zoy.rabbitmq.basic.message;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by zoypong on 2018/10/27.
 */
public class Customer {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        // ·1、创建一个ConnectionFactory，并配置
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        // ·2、通过 连接工厂创建 Connection
        Connection connection = connectionFactory.newConnection();
        // ·3、通过 Connection创建 Channel
        Channel channel = connection.createChannel();

        // ·4、声明一个队列
        String queueName = "test001";
        channel.queueDeclare(queueName, true, false, false,null);
        // ·5、创建消费者（区别 producer，这里不是直接在 channel里操作）
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
        // ·6、消费者监听队列
        channel.basicConsume(queueName, true, queueingConsumer);

        // ·7、获取消息
        while (true) {
            Delivery delivery = queueingConsumer.nextDelivery();
            String msgOfGetting = new String(delivery.getBody());
            System.out.println("消费端接受的消息：" + msgOfGetting);

            Map<String, Object> headers = delivery.getProperties().getHeaders();
            System.out.println("myHeader is : " + headers.get("header1"));
        }
    }
}
