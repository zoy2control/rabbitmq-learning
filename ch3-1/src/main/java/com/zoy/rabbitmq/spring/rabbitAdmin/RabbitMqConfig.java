package com.zoy.rabbitmq.spring.rabbitAdmin;

import com.zoy.rabbitmq.spring.util.adapter.MessageDelegate;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * ·RabbitMQ配置类
 * ·其实可以看到，原始的 创建ConnectionFactory、Connection、Channel、声明交换机队列等都以 Bean的
 * ·形式封装，并注入
 * Created by zoypong on 2019/1/26.
 */
@Configuration // ·配置Bean
@ComponentScan({"com.zoy.rabbitmq.spring.rabbitAdmin.*"}) // ·要扫描的包路径
public class RabbitMqConfig {

    /**
     * ·将 ConnectFactory注入到 Bean容器中
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        // ·这里设置一些 连接参数
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setAddresses("127.0.0.1:5672");
        cachingConnectionFactory.setUsername("guest");
        cachingConnectionFactory.setPassword("guest");
        cachingConnectionFactory.setVirtualHost("/");
        return cachingConnectionFactory;// ·注入到Bean容器中
    }

    /**
     * ·将 RabbitAdmin注入到 Bean容器中。
     * ·注意，@Bean(name="")可以自定义名称，没写name默认以方法名为 beanName
     * ·所以这里 rabbitAdmin需要的入参 ConnectionFactory从容器中取，就需要这个 入参名称与上面的 Bean名称一样
     * ·否则就找不到这个 Bean
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        /* ·注意：区别原始的 通过ConnectionFactory创建 Connection，再通过 Connection创建 Channel*/
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);// ·Spring容器加载的时候，一定要把这个 Bean加载上
        return rabbitAdmin;// ·注入到 Bean容器中
    }

    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     HeadersExchange ：通过添加属性key-value匹配
     DirectExchange:按照routingkey分发到指定队列
     TopicExchange:多关键字匹配
     */
    @Bean
    public TopicExchange exchange001() {
        return new TopicExchange("topic001", true, false);
    }

    @Bean
    public Queue queue001() {
        return new Queue("queue001", true); //队列持久
    }

    @Bean
    public Binding binding001() {
        /* ·BindingBuilder.bind(queue.to(exchange).with(routingKey))*/
        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
    }

    @Bean
    public TopicExchange exchange002() {
        return new TopicExchange("topic002", true, false);
    }

    @Bean
    public Queue queue002() {
        return new Queue("queue002", true); //队列持久
    }

    @Bean
    public Binding binding002() {
        return BindingBuilder.bind(queue002()).to(exchange002()).with("rabbit.*");
    }

    @Bean
    public Queue queue003() {
        return new Queue("queue003", true); //队列持久
    }

    /* ·一个 exchange绑定 两个queue，至于路由到 哪个queue，根据 routingKey指定*/
    @Bean
    public Binding binding003() {
        return BindingBuilder.bind(queue003()).to(exchange001()).with("mq.*");
    }

    @Bean
    public Queue queue_image() {
        return new Queue("image_queue", true); //队列持久
    }

    @Bean
    public Queue queue_pdf() {
        return new Queue("pdf_queue", true); //队列持久
    }







    /**
     * ·实例化 消息模板。RabbitTemplate
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }











    /**
     * ·SimpleMessageListenerContainer，（消费端）消息监听容器类
     * @param connectionFactory
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) {

        // ·自定义 SimpleMessageListenerContainer。消息监听容器类（消费端）
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(queue001(), queue002(), queue003(), queue_image(), queue_pdf()); // ·监听多个队列
        container.setConcurrentConsumers(1);// ·当前 消费者数量
        container.setMaxConcurrentConsumers(5);
        container.setDefaultRequeueRejected(false);// ·是否 重回队列
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);// ·签收模式：自动签收
        container.setExposeListenerChannel(true);
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {// ·自定义消费端标签策略
            @Override
            public String createConsumerTag(String queue) {
                return queue + "_" + UUID.randomUUID().toString();
            }
        });

//        // ·eq1：
//        // ·消息监听器
//        container.setMessageListener(new ChannelAwareMessageListener() {// ·具体的消息监听：有消息过来，就会通过 Listener进行监听
//            // ·监听消息就会走到 onMessage()方法。实现 消息监听
//            @Override
//            public void onMessage(Message message, Channel channel) throws Exception {
//                String msg = new String(message.getBody());
//                System.err.println("----------消费者: " + msg);
//            }
//        });

        // ·eq2： 适配器方式。重写 .handleMessage()
        // ·也可以添加一个转换器: 从字节数组转换为String
        // ·这个类似于前面的 自定义消费者
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        container.setMessageListener(adapter);

        // ·eq3:可以自己指定 自定义消费者的其中一个方法的名字: consumeMessage
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        adapter.setMessageConverter(new TextMessageConverter());
//        container.setMessageListener(adapter);

        return container;


        /**
         * 2 适配器方式: 我们的队列名称 和 方法名称 也可以进行一一的匹配
         *
         MessageListenerAdapter adapter = new MessageListenerAdapter(new adapter());
         adapter.setMessageConverter(new TextMessageConverter());
         Map<String, String> queueOrTagToMethodName = new HashMap<>();
         queueOrTagToMethodName.put("queue001", "method1");
         queueOrTagToMethodName.put("queue002", "method2");
         adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
         container.setMessageListener(adapter);
         */

        // 1.1 支持json格式的转换器
        /**
         MessageListenerAdapter adapter = new MessageListenerAdapter(new adapter());
         adapter.setDefaultListenerMethod("consumeMessage");

         Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
         adapter.setMessageConverter(jackson2JsonMessageConverter);

         container.setMessageListener(adapter);
         */



        // 1.2 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象转换
        /**
         MessageListenerAdapter adapter = new MessageListenerAdapter(new adapter());
         adapter.setDefaultListenerMethod("consumeMessage");

         Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();

         DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
         jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);

         adapter.setMessageConverter(jackson2JsonMessageConverter);
         container.setMessageListener(adapter);
         */


        //1.3 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象多映射转换
        /**
         MessageListenerAdapter adapter = new MessageListenerAdapter(new adapter());
         adapter.setDefaultListenerMethod("consumeMessage");
         Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
         DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();

         Map<String, Class<?>> idClassMapping = new HashMap<String, Class<?>>();
         idClassMapping.put("order", com.bfxy.spring.entity.Order.class);
         idClassMapping.put("packaged", com.bfxy.spring.entity.Packaged.class);

         javaTypeMapper.setIdClassMapping(idClassMapping);

         jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
         adapter.setMessageConverter(jackson2JsonMessageConverter);
         container.setMessageListener(adapter);
         */

        //1.4 ext convert
//
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new adapter());
//        adapter.setDefaultListenerMethod("consumeMessage");
//
//        //全局的转换器:
//        ContentTypeDelegatingMessageConverter convert = new ContentTypeDelegatingMessageConverter();
//
//        TextMessageConverter textConvert = new TextMessageConverter();
//        convert.addDelegate("text", textConvert);
//        convert.addDelegate("html/text", textConvert);
//        convert.addDelegate("xml/text", textConvert);
//        convert.addDelegate("text/plain", textConvert);
//
//        Jackson2JsonMessageConverter jsonConvert = new Jackson2JsonMessageConverter();
//        convert.addDelegate("json", jsonConvert);
//        convert.addDelegate("application/json", jsonConvert);
//
//        ImageMessageConverter imageConverter = new ImageMessageConverter();
//        convert.addDelegate("image/png", imageConverter);
//        convert.addDelegate("image", imageConverter);
//
//        PDFMessageConverter pdfConverter = new PDFMessageConverter();
//        convert.addDelegate("application/pdf", pdfConverter);

//
//        adapter.setMessageConverter(convert);
//        container.setMessageListener(adapter);

    }
}