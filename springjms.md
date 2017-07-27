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
4. 创建AppProducer.java调用producer的sendMessage方法发送消息到指定的activemq目标
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
