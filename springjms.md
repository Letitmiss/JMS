# Spring Jms

## Spring集成JMS链接AtIveMQ提供

### ConnectionFactory用于管理连接的连接工厂
* Spring为我们提供的连接池，减少资源消耗和等待时间
* Jms每次发消息都会重新创建连接、会话和producor
* Spring中提供了SingleConnectionFactory和CachingConnectionFactory两种工厂，CachingConnectionFactory继承SingleConnectionFactory，在保证单个连接的基础上，新增了缓存会话等功能
### JmsTemplate用于发送和接收消息的模板类
* spring提供的，需要spring容器中注册这个类，就可以使用JmsTemplate方便操作jms
* JmsTemplate类是线程安全的，可在整个应用范围内使用 
### MessageListerner消息监听器
* 消息监听器，需要实现一个onMessage放发,这个方法的入参就是接受到的Message消息



## Spring集成Jms演示

### 1.项目搭建

1. 创建maven项目Jms-spring，导入pom依赖
      
  * 导入spring-context
  * 导入spring-core
  * 导入junit
  * 导入spring-jms
  * 导入spring-test
  * 导入activemq-core 排除spring-context
 ### 2.创建queue的生产者   
1. 创建包com.jms.producer，创建生产者接口ProducerService
````
package com.jms.producer;

public interface ProducerService {
	
	/**
	 * send message
	 */
	void sendMessage(String message);
}
````
2. 创建Producer.xml的配置文件

````
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:context="http://www.springframework.org/schema/context" 
       xmlns:aop="http://www.springframework.org/schema/aop"   
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="
   		http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
        http://www.springframework.org/schema/context
  		http://www.springframework.org/schema/context/spring-context-4.0.xsd
        http://www.springframework.org/schema/aop 
 		http://www.springframework.org/schema/aop/spring-aop-4.0.xsd
 		http://www.springframework.org/schema/tx 
 		http://www.springframework.org/schema/tx/spring-tx-4.0.xsd">
 		
 	<!-- 开启注解扫描 -->	
 	<context:annotation-config />
 	
 	<!-- activeMQ提供目标连接 -->
 	<bean id="targetConnectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">
 		<property name="brokerURL" value="tcp://10.253.177.16:61616"></property>
 	</bean>
 	
 	<!-- spring提供管理链接工厂 -->
 	<bean id="connectionFactory"  class="org.springframework.jms.connection.SingleConnectionFactory">
		<property name="targetConnectionFactory" ref="targetConnectionFactory"></property>
 	</bean>
 	
 	<!--队列目的地   P2P-->
 	<bean id="queueDestination" class="org.apache.activemq.command.ActiveMQQueue">
 		<constructor-arg value="queue"></constructor-arg>
 	</bean>
 	<!-- spring 的发送消息模板类 -->
 	<bean id="jmsTemplate" class="org.springframework.jms.core.JmsTemplate">
 		<property name="connectionFactory" ref="connectionFactory"></property>
 	</bean>
 	
 	<bean id="producerServiceImpl" class="com.jms.producer.ProducerServiceImpl"></bean>
 		
</beans>
````
3. 实现ProducerService接口ProducerServiceImpl
````
package com.jms.producer;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class ProducerServiceImpl implements ProducerService {
	
	//自动注入
	@Autowired
	private JmsTemplate jmsTemplate;
	
	//资源注入，可以注入多个消息目的地
	@Resource(name="queueDestination")
	private Destination destination;
	
	@Override
	public void sendMessage(String message) {
		//使用JmsTemplate发送消息
		jmsTemplate.send(destination,new MessageCreator() {
			//需要创建一个消息，
			@Override
			public Message createMessage(Session session) throws JMSException {
				//创建message
				TextMessage textMessage = session.createTextMessage(message);				
				return textMessage;
			}
		});	
		//log
		System.out.println("发送消息" + message);			
	}
}
````
4. 创建AppProducer调用producerservice的sendMessage方法发送消息到指定的activemq目标
````
package com.jms.producer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 *  启动spring的producer.xml加载配置bean,
 *  项目中交给容器加载，这里测试手动加载配置
 */

public class AppProducer {
    public static void main(String[] args) {	
	ApplicationContext context =new ClassPathXmlApplicationContext("classpath:producer.xml");
	ProducerService service = context.getBean(ProducerService.class);
	for (int i = 0; i < 100; i++) {
		service.sendMessage("test spring-jms queue" + i);
		}		
	}
}
````
5. 测试queue模式生产者，发送消息到ActimeMQ
* 首先确保activemq的页面可以访问，执行Appproducer的main方法，查看queues页签的Number Of Pending Messages 是不是100，队列名称是否增加了queue 
### 创建queue消费者

1. 创建包com.jsm.consumer,创建消费者消息监听器CounsumerMessageListener
````
package com.jms.consumer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Queue监听接收到的消息
 */
public class ConsumuerMessageListener implements MessageListener{
	@Override
	public void onMessage(Message message) {
		//已经收到的消息
		TextMessage textMessage = (TextMessage)message;
		//log
		try {
			System.out.println("收到消息  ："  + textMessage.getText());
		} catch (JMSException e) {		
			e.printStackTrace();
		}
	}
}
````
2. 配置Consumer.xml,
* 消费者和生产者都有相同配置，将公共的配置抽取一个公共jms-common.xml,在配置文件中加入
  
  	````	
 	<!-- 开启注解扫描 -->	
 	<context:annotation-config />
 	
 	<!-- activeMQ提供目标连接 -->
 	<bean id="targetConnectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">
 		<property name="brokerURL" value="tcp://10.253.177.16:61616"></property>
 	</bean>
 	
 	<!-- spring提供管理链接工厂 -->
 	<bean id="connectionFactory"  class="org.springframework.jms.connection.SingleConnectionFactory">
		<property name="targetConnectionFactory" ref="targetConnectionFactory"></property>
 	</bean>
 	
 	<!--队列目的地   P2P-->
 	<bean id="queueDestination" class="org.apache.activemq.command.ActiveMQQueue">
 		<constructor-arg value="queue"></constructor-arg>
 	</bean>
  	````
* consumer.xml
	````
 	<import resource="jms-common.xml"/>
 	
 	<!-- 自定义的消息监听器 -->
 	<bean id="consumuerMessageListener" class="com.jms.consumer.ConsumuerMessageListener"></bean>
 	
 	<!-- spring提供的消息监听容器 -->
 	<bean id="jmsContainer" class="org.springframework.jms.listener.DefaultMessageListenerContainer">
 		<!-- spring连接工厂 -->
 		<property name="connectionFactory" ref="connectionFactory"></property>
 		<!-- 监听消息目的地 -->
 		<property name="destination" ref="queueDestination"></property>
 		<!-- 注入自定义的消息监听 -->
 		<property name="messageListener" ref="consumuerMessageListener"></property> 	
 	</bean> 	
  	````
3. 创建消费者，APPCOnsumer 消费消息
````
package com.jms.consumer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppConsumer {
	
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:consumer.xml");
		
		//此处只是需要加载spring的配置文件即可，spring启动监听器线程，一直监听消息，此处不要关闭context这个spring的
		//上下文对象，因为监听器是异步的，关闭context会造成spring注册对象的销毁，可能收到消息不完整
		
	}

}
```` 
4. 测试接收消息 
* 确保activemq可以访问，同时启动消费者和生产者，查看activemq查看消息生产和消费
* 启动多个消费者，查看多个消费者平均分配了消息，而且每个消息只能被消费一次
	
