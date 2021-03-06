package com.fmzh.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Receiver_Ticket {
    /**
     * 获取消息队列名称
     */
    private final static String QUEUE_NAME = "mywork";
    /**
     * 异步接收
     * @throws Exception
     */
    public static void asyncRec() throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
//        //消费者也需要定义队列 有可能消费者先于生产者启动 （参数2 true表示持久化 false表示非持久化）
//        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //修改定义队列代码（参数2 true表示持久化 false表示非持久化）
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        //定义回调抓取消息
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");

                //参数2 true表示确认该队列所有消息  false只确认当前消息 每个消息都有一个消息标记
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        // 修改Rec类 （第二个参数改为false 就是手动确认）
        channel.basicConsume(QUEUE_NAME, false, consumer);
//        // 修改Rec类 （第二个参数改为true 就是自动确认）
//        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

    /**
     * 同步接收 消费者定时去抓取
     * 该种方式已过期
     * @throws Exception
     */
    public static void asyncFalseRec() throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, true, consumer);
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println(" [x] Received '" + message + "'");
        }

    }

    public static void main(String[] args) throws Exception {
        asyncRec();
    }
}
