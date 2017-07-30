>###  [首 页](https://github.com/Letitmiss/JMS/blob/master/README.md)
# RabbitMQ 入门

## RabbitMQ基本工作流程

    [示意图]

1. virtual host 虚拟主机

      因为RabbitMQ当中，用户只能在虚拟主机的粒度进行权限控制。因此，如果需要禁止A组访问B组的交换机/队列/绑定，必须为A和B分别创建一个虚拟主机。每一个RabbitMQ服务器都有一个默认的虚拟主机
      
2. exchange 交换机

    将收到的消息放到队列中，交换机（Exchange）可以理解成具有路由表的路由程序。每个消息都有一个称为路由键（routing key）的属性，就是一个简单的字符串。交换机当中有一系列的绑定（binding），即路由规则（routes）。
    
3. queue 队列

    队列（Queues）是你的消息（messages）的终点，可以理解成装消息的容器。消息就一直在里面，直到有客户端（也就是消费者，Consumer）连接到这个队列并且将其取走为止。不过，也可以将一个队列配置成这样的：一旦消息进入这个队列，此消息就被删除。队列是消费者或生产者通过程序建立的，不是通过配置文件或者命令行工具。这没什么问题，如果一个消费者试图创建一个已经存在的队列，RabbitMQ会直接忽略这个请求。因此我们可以将消息队列的配置写在应用程序的代码里面。

4. binding 绑定

    路由规则，即绑定（binding），交换机判断将消息放入哪一个队列
    
    
    
 ## 入门程序 
    
  1. 创建Rabbitmq的maven项目，导入POM
  ````
       <dependency>
            <groupId>com.rabbitmq</groupId>
            <artifactId>amqp-client</artifactId>
            <version>3.6.6</version>
        </dependency>
   ````
  2. 创建包com.rabbit.helloworld,创建生产者producer
  
  
  
## rabbitMQ的API
  

 
 
 
 
