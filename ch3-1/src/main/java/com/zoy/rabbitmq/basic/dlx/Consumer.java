package com.zoy.rabbitmq.basic.dlx;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * ·死信交换机
 * ·死信流程：消息先到 正常exchange中，可以看到 test_dlx_queue中有DLX属性（从 管控台查看），
 * ·如果 消息没有被 监听到，那么就会把 死信路由到 指定的死信Exchange中，而此处的 dlx.queue与 死信Exchange绑定
 * ·所以 死信会被路由到 dlx.queue中
 */
public class Consumer {
	public static void main(String[] args) throws Exception {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("127.0.0.1");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");

		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();

		// ·这就是一个普通的交换机 和 队列 以及路由
		String exchangeName = "test_dlx_exchange";
		String routingKey = "dlx.#";// ·dlx.开头的所有routingKey，都会被路由
		String queueName = "test_dlx_queue";

		// ·Step1：正常exchange和 正常queue的声明、绑定
		Map<String, Object> queueAgruments = new HashMap<String, Object>();
		queueAgruments.put("x-dead-letter-exchange", "dlx.exchange");
		// ·交换机声明
		// ·这个agruments属性，要设置到声明 队列上，而不是 exchange上
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);
		channel.queueDeclare(queueName, true, false, false, queueAgruments);// ·注意，这里用到 arguements
		channel.queueBind(queueName, exchangeName, routingKey);


		// ·Step2：死信exchange和 死信queue的声明、绑定
		// ·重点：死信Exchange和 死信队列的声明:
		String dlxExchangeName = "dlx.exchange";
		String dlxQueueName = "dlx.queue";
		String dlxRoutingKey = "#";

		channel.exchangeDeclare(dlxExchangeName, "topic", true, false, null);
		channel.queueDeclare(dlxQueueName, true, false, false, null);
		channel.queueBind(dlxQueueName, dlxExchangeName, dlxRoutingKey);


		// ·消费端发送消息
		channel.basicConsume(queueName, true, new MyConsumer(channel));

	}
}
