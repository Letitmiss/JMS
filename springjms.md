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
    
2. 创建包com.jms.producer，创建生产者接口ProducerService
````
package com.jms.producer;

public interface ProducerService {
	
	/**
	 * send message
	 */
	void sendMessage(String message);
}
````
3. 创建Producer.xml的配置文件

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



