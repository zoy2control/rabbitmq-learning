package com.zoy.rabbitmq.basic.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by zoypong on 2018/11/13.
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

        // ·4、重点来啦：指定我们的消息 投递模式，消息的确认模式
        channel.confirmSelect();

        String exchangeName = "test_confirm_exchange";
        String routingKey = "test_confirm_routingKey.save";

        // ·5、发送消息
        String msg = "hello mq send CH3S3confirm message";
        channel.basicPublish(exchangeName, routingKey,null, msg.getBytes());

        // ·6、添加一个确认监听
        channel.addConfirmListener(new ConfirmListener() {
            /**
             * ·收到 ack的时候，执行这个 函数
             * @param deliveryTag
             * @param multiple
             * @throws IOException
             */
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("------------ack!!!-------------");
            }

            /**
             * ·没有收到 ack的时候，执行这个 函数
             * @param deliveryTag
             * @param multiple
             * @throws IOException
             */
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("------------no ack!!!-------------");
            }
        });
    }
}
