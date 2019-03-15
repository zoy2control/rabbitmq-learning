package com.zoy.rabbitmq.basic.requeue;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * ·自定义消费者
 * Created by zoypong on 2019/03/13.
 */
public class MyConsumer extends DefaultConsumer {

    private Channel channel;

    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     */
    public MyConsumer(Channel channel) {
        super(channel);
        this.channel = channel;
    }

    // ·实现 .handleDelivery()，消费者处理函数
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.out.println("===================消费者Nack==================");
        System.out.println("consumerTag: " + consumerTag);
        System.out.println("envelop: " + envelope);
        System.out.println("properties: " + properties);
        System.out.println("body: " + new String(body));

        // ·暂停3s，查看效果更明显
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /* ·ack和 Nack都要有处理的程序*/
        // ·这里 第一条消息会被 拒收，然后 第一条消息重回队列
        if ((Integer)properties.getHeaders().get("num") == 0) {// ·Nack
            // ·multiple，是否 批量处理；requeue，是否 重回队列
            channel.basicNack(envelope.getDeliveryTag(), false, true);
        } else {// ·ack
            // ·手工签收。multiple表示是否批量签收，取决于消费端接受多少条数据（限流），非1即批量
            channel.basicAck(envelope.getDeliveryTag(), false);
        }

    }
}
