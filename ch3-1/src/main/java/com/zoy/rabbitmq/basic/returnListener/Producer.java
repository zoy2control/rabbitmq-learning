package com.zoy.rabbitmq.basic.returnListener;

import com.rabbitmq.client.*;

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

        // ·4、重点来啦：指定我们的 消息回收模式
        // ·如果从 生产端发送到 MQ Broker的消息不能路由，那么 该消息将被 生产端“回收”
        channel.addReturnListener(new ReturnListener() {

            // ·回收之后的消息用 handleReturn()处理，我们这里只是简单的 控制台打印
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange,
                                     String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("replyCode : " + replyCode);
                System.out.println("replyText : " + replyText);
                System.out.println("exchange : " + exchange);
                System.out.println("routingKey : " + routingKey);
                System.out.println("properties : " + properties);
                System.out.println("body : " + new String(body));
            }
        });

        String exchangeName = "test_returnListener_exchange";
        String routingKeyCorrectly = "test_returnListener_routingKey.save";// ·正确的路由键
        String routingKeyInCorrectly = "test_routingKey.save";// ·不能路由的 路由键


        // ·5、发送消息
        // ·对于 第三个参数 mandatory，设置为 true，如果 发送的该消息不能路由到指定队列，那么 该消息也不会被
        // ·删除，如果 生产端设置了 returnListener()，那么 消息将会被 生产端的 returnListener监听到
        String msg = "hello mq send returnListener message";
        String incorrectMsg = "Incorrect returnListener message";
        channel.basicPublish(exchangeName, routingKeyCorrectly,null, msg.getBytes());// ·正常可以路由的消息
        channel.basicPublish(exchangeName, routingKeyInCorrectly, true, null, incorrectMsg.getBytes());// ·异常不可路由的消息，待回收
    }
}
