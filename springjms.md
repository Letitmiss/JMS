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

