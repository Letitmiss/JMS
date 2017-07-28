### [首页](https://github.com/Letitmiss/JMS/blob/master/README.md)

## ActiveMQ的最佳实践

## ActiveMQ集群

### 为什么需要对消息中间件集群？

* 实现**高可用**，排除单点故障引起的服务中断
* 实现**负载均衡**，提高并发量，提高访问效率，为更多客户提供服务

### 集群方式
1. 客户端集群：让多个消费者消费同一个队列， 在主题消息模式下，客户端获得完成的消息，造成消息重复（后续解决）
2. Borker Cluster ： 多个Broker之间同步消息
3. Master Slave : 实现高可用

### 客户端配置
  ActiveMQ**失效转移（failover）**
  * 允许其中一台消息服务器宕机时，客户端在传输层上重新连接到其他消息服务器
  * 语法 `failover:(uri1,...,uriN)?transportOptions`
  * tansportOptions参数说明
  
  1. randomize 默认为true，表示在URI列表中选择URI连接时是否采用随机策略<br />
  2. initialReconnectDelay 默认是10，单位为毫秒，表示第一次尝试重连之间的等待时间<br/>
  3. maxReconnectDelay默认是30000，单位为毫秒，最长重连时间间隔 <br/>

### BrokerCluster集群配置

   ![BrokerCluster](https://github.com/Letitmiss/JMS/blob/master/img/brokercluster.jpg)

1. 多台消息服务器之间同步消息 

2. 网络连接器NetworkConnector <br/>

    网络连接器主要用于配置ActiveMQ服务器与服务器之间的网络通讯方式，用于服务器透传消息,分为静态连接器和动态连接器
    
3. 静态连接器： 通过IP地址配置连接,不方便扩展
  
  ````
  <networkConnectors>
    <networkConnector uri="static:(tcp://127.0.0.1:61617,tcp://127.0.0.1:61617)"
  </networkConnectors>
  ````

4. 动态连接器 ：配置网路连接器和传输连接器
  
  ````
   <networkConnectors>
      <networkConnector uri="multicast://default"/>
    </networkConnectors>

  <transportConnectors>
    <transportConnector uri="tcp://localhost:0" discoveryUri="multicast://default" />
  </transportConnectors>
  ````
 ## Master/Slave 集群配置
 
### 三种ActiveMQ Master Slave集群方案
#### 1.share nothing storage master/slave （已经过时，5.8+ 被移除）
#### 2 Shared storage master/slave 基于共享存储

   ![基于共享存储](https://github.com/Letitmiss/JMS/blob/master/img/master-slave1.jpg)
   
  1.采用是持久化数据，持久化可以是数据库，也可以是文件系统 <br />
  2.A启动获得资源排他锁,成为Master，如果A挂了，B就会立即获得资源排他锁，成为master <br />
  3.客户端采用了实效转移，将请求由A消息服务器转移到B执行，达到高可以用 <br />
  
#### 3.Prelicated LevelDB Store 基于复制的LevelDB Stroe 

  ![基于复制的LevelDB Stroe](https://github.com/Letitmiss/JMS/blob/master/img/master-slave2.jpg)
  
  1. zk选举A为Master，A对外提供服务，发消息到zk，zk将消息同步给B和C节点<br />
  2. 通过zookeeper选主集群，保证zk的稳定性,所以zk也必须为集群部署 <br />
  2. 如果A挂机，zk自动高可用，会选举出新的master，达到高可用
  
## 两种集群比较
![compare](https://github.com/Letitmiss/JMS/blob/master/img/compare.jpg)

结合可以既可以实现高可用，又可以实现高并发

## 三台服务器的完美解决方案
  
### 三台服务器集群分析 
  
  ![3cluster](https://github.com/Letitmiss/JMS/blob/master/img/3cluster.jpg)
  
  1.需要按照A-B—C顺序启动服务器，A没有设置持久化，通过BC完成，B先启动获得资源排他锁 <br/>
  2.ABC三台服务器之间消息同步，可以实现负载均衡，达到高并发 <br/>
  3.现在B服务器是master，如果B宕机，C会立刻成为master，达到高可用，如果B恢复了，现在C是master，B就是slave <br/>
  4.如果A对外服务宕机了，实效转移就会消费B的消息，如果A恢复之后B的消息也会同步到A，A的消息可以对外服务 <br/>
  5.c目前是slave，宕机对现在的集群无影响,三台服务器任意一台服务器宕机，都可以保证对外服务  <br/>
  6.这个方案应该立即恢复服务器，如果两台服务同时宕机，就会出现问题 
  
### 三台服务器集群实战
  
1. 配置方案
 
    ![config](https://github.com/Letitmiss/JMS/blob/master/img/clusterconfig.jpg)
    
    A节点只是作为消费者，如果A有消息，还没有被BC消费就挂了，BC就不能消费到A的消费了，造成消息丢失
    
    不同服务器，需要设置共享文件，使用分布式文件系统
 
2. 创建文件夹，复制activemq-a，activemq-b，activemq-c
  
    ````
    mkdir activemqclu
    cp -r apache-activemq-5.15.0 activemqclu/activemq-a
    cp -r apache-activemq-5.15.0 activemqclu/activemq-b
    cp -r apache-activemq-5.15.0 activemqclu/activemq-c
    mkdir sharedb
    ````

3. 配置activemq-a
           
* `vim activemq.xml`修改<transportConnectors>节点，除了openwire的<transportConnector>都注释了,增加静态网络链接
  
  ````
    <networkConnectors>
      <networkConnector name="local_network" uri="static:(tcp://127.0.0.1:61617,tcp://127.0.0.1:61618)"  />
    </networkConnectors>
    ````
    
* `vim jetty.xml` 端口采用默认8161
    
    ````
    <bean id="jettyPort" class="org.apache.activemq.web.WebConsolePort" init-method="start">
         <!-- the default port number for the web console -->
        <property name="host" value="0.0.0.0"/>
        <property name="port" value="8161"/>
      </bean>
     ````
 
4. 配置activemq-b
    
* 注释其他链接， 设置opwire的对外端口为61617
* 添加链接

    ````
    <networkConnectors>
              <networkConnector name="network_a" uri="static:(tcp://127.0.0.1:61616)"  />
    </networkConnectors>
    ````
       
* 设置共享文件
     ````
    <persistenceAdapter>
        <kahaDB directory="/home/source/source/activemqclu/sharedb"/>
    </persistenceAdapter>
    ````
* 配置 jetty端口为8162
      
5. 配置activemq-c

* 注释其他链接， 设置opwire的对外端口为61618
* 添加链接

    ````
    <networkConnectors>
              <networkConnector name="network_a" uri="static:(tcp://127.0.0.1:61616)"  />
    </networkConnectors>
    ````
       
* 设置共享文件
     ````
    <persistenceAdapter>
        <kahaDB directory="/home/source/source/activemqclu/sharedb"/>
    </persistenceAdapter>
    ````
* 配置 jetty端口为8163
    
6. adc顺序启动并测试
 
    `ps ef | grep activemq` 查看启动了三个进程 <br />
    `netstat -anp | grep 61616` ，`netstat -anp | grep 61617`  `netstat -anp | grep 61618` 查看对外服务端口 <br/>
     发现61618没有对外提供服务，因为c为slave，是启动状态但是没有对外提供服务 <br/>
     
     集群如果B关闭了，C就自动对外提供服务,`./activemq-b/bin/activemq stop`  <br/>
     关闭B查看`netstat -anp | grep 61618` 就有对外提供的服务了，为master 
    
    lfg1000707999:/home/source/source/activemqclu # netstat -anp | grep 61618
    tcp        0      0 :::61618                :::*                    LISTEN      17773/java          
    tcp        0      0 127.0.0.1:61618         127.0.0.1:52472         ESTABLISHED 17773/java          
    tcp        0      0 127.0.0.1:52472         127.0.0.1:61618         ESTABLISHED 16381/java
    
   恢复B服务，`./activemq-b/bin/activemq start`，此时查看进程是启动，但是61617没有对外提供服务，因为B此时是slave
   
### 三台服务器集群代码测试
    
1. 项目准备

  采用ActiveMQ入门的项目代码，修改url

2. queue消息模型,修改AppProducer生产者，集群中B和C为生产者，配置失效转移
````
//private static final String url="tcp://10.253.177.16:61616";
private static final String url="failover:(tcp://10.253.177.16:61617,tcp://10.253.177.16:61618)?randomize=true";
private static final String queueName="queue-cluster-test";
````
3. queue消息模型，修改AppConsumer消费者，集群中A、B、C为生产者，配置失效转移
````
//private static final String url="tcp://10.253.177.16:61616";
	private static final String url="failover:(tcp://10.253.177.16:61616,tcp://10.253.177.16:61617,tcp://10.253.177.16:61618)?randomize=true";
	private static final String queueName="queue-cluster-test";
````
4.测试集群queue模型 

* 查看web，现在在B为slave不对外提供服务，8162端口访问界面拒绝，C为master，C和A的界面可以访问
* 启动生产消息线程，查看C有100个消息等待，A上没有消息等待（A只能消费），启动消费者线程，页面查看消息的消费；
* 启动生产线程，查看C有100个消息在等待，此时还没有被消费，后台关闭C服务`./activemq-c/bin/activemq stop`，然后启动消费者线程，消费服务依然能够消费  	消息，集群高可用测试OK；

