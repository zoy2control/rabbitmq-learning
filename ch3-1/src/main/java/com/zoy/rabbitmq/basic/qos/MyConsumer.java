package com.zoy.rabbitmq.basic.qos;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
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

    /**
     * ·实现 .handleDelivery()，消费者处理函数
     *
     * @param consumerTag
     * @param envelope
     * @param properties
     * @param body
     * @throws IOException
     */
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.out.println("===================自定义消费者：user def==================");
        System.out.println("consumerTag: " + consumerTag);
        System.out.println("envelop: " + envelope);
        System.out.println("properties: " + properties);
        System.out.println("body: " + new String(body));

        // ·手工签收。multiple表示是否批量签收，取决于消费端接受多少条数据（限流），非1即批量
        channel.basicAck(envelope.getDeliveryTag(), false);
    }
}
