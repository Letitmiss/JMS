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
  ActiveMQ**实效转移（failover）**
  * 允许其中一台消息服务器宕机时，客户端在传输层上重新连接到其他消息服务器
  * 语法 `failover:(uri1,...,uriN)?transportOptions`
  * tansportOptions参数说明
  
  1. randomize 默认为true，表示在URI列表中选择URI连接时是否采用随机策略<br />
  2. initialReconnectDelay 默认是10，单位为毫秒，表示第一次尝试重连之间的等待时间<br/>
  3. maxReconnectDelay默认是30000，单位为毫秒，最长重连时间间隔 <br/>

### BrokerCluster集群配置

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
 
1. 三种ActiveMQ Master Slave集群方案
* share nothing storage master/slave （已经过时，5.8+ 被移除）
* Shared storage master/slave 基于共享存储


  示意图
  
  
  1.采用是持久化数据，持久化可以是数据库，也可以是文件系统 <br />
  2.A启动获得资源排他锁,成为Master，如果A挂了，B就会立即获得资源排他锁，成为master <br />
  3.客户端采用了实效转移，将请求由A消息服务器转移到B执行，达到高可以用 <br />
* Prelicated LevelDB Store 基于复制的LevelDB Stroe 
  示意图
  1. 
  通过zookeeper选主集群，保证zk的稳定性
2. 

