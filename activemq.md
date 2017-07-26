# ActiveMQ

## ActiveMQ安装(Windows)

1. 下载安装包 [ActiveMQ下载地址](http://activemq.apache.org/download.html) 下载windows对应版本，需要有java环境
2. 解压到指定的目录下，在/bin根据windows系统32位还是64位选择对应文件夹,有两种启动方式
* 以管理员权限执行 apache-activemq-5.15.0\bin\win64\activemq.bat , 会出现windows的dos窗格，可以访问web界面，但是在运行过程中，这dos界面不能关闭，关闭就表示activemq关闭了
* 以管理员权限执行 apache-activemq-5.15.0\bin\win64\InstallService.bat ，安装了一个windows服务，执行之后只是添加了服务，还需要手动启动     ActimeMQ服务，之后就可以访问界面
3. 访问主页
   本地部署访问地址是http://localhost:8161 默认用户名和密码是admin/admin

## ActiveMQ安装(Linux)

1. 下载安装包  [ActiveMQ下载地址](http://activemq.apache.org/download.html) 下载linux对应版本，获取下载链接，使用wget命令下载，需要有java环境
2. 解压 tar -zxvf apache-activemq-5.15.0-bin.tar.gz
3. 在/bin 执行 ./activemq start ，出现pid进程好 ，ps -ef |grep activemq 查看进程,查看状态./activemq status  
4. 访问页面查看 http://@{ip}:8161/  用户名和密码 admin/admin

## 队列消息的展示

### 创建项目
1. 创建maven项目，JMS—Test，导入相关依赖，POM文件

    ` <dependencies>
     <dependency>
      <groupId>org.apache.activemq</groupId>
      <artifactId>activemq-all</artifactId>
      <version>5.15.0</version>
     </dependency>
   </dependencies> `
 2. 创建包com.jms.queue,添加类AppProducer


	````
	package com.jms.queue;
	import javax.jms.Connection;
	import javax.jms.Destination;
	import javax.jms.JMSException;
	import javax.jms.MessageProducer;
	import javax.jms.Session;
	import javax.jms.TextMessage;

	import org.apache.activemq.ActiveMQConnectionFactory;

	public class AppProducer {
	
	private static final String url="tcp://10.253.177.16:61616";
	private static final String queueName="queue-test";
	
	public static void main(String[] args) throws JMSException {
		//1.创建连接工程ConnectionFactory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		
		//2. 创建Connection
		Connection connection = connectionFactory.createConnection();
		
		//3.启动连接
		connection.start();
		
		//4.创建会话
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		//5.创建目的地
		 //Queue createQueue = session.createQueue(queueName);
		Destination destination = session.createQueue(queueName);
		
		//6.创建生产者
		MessageProducer producer = session.createProducer(destination);
		
		//7.发送消息
		for (int i = 0; i < 100; i++) {
			//1.创建消息
			TextMessage textMessage = session.createTextMessage("test queue message"+ i);		
			//2.发送消息
			producer.send(textMessage);
			//log
			System.out.println("发送消息 ：" + textMessage.getText());
			
		}		
		//8.关闭连接
		session.close();
		
	}

	}


