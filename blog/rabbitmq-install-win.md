### [首页](https://github.com/Letitmiss/JMS/blob/master/README.md)

## RabbitMQ安装(Windows)

### 下载安装包
  
  [官网下载资源](http://www.rabbitmq.com/releases/)
  
1. [下载Erlangotp_win64_19.3.exe](http://erlang.org/download/otp_win64_19.3.exe)  
2. [下载RabbitMQ3.6.10.exe](http://www.rabbitmq.com/releases/rabbitmq-server/v3.6.10/rabbitmq-server-3.6.10.exe)
3. [下载rabbitq-client-bin-3.6.4.zip](http://www.rabbitmq.com/releases/rabbitmq-java-client/v3.6.4/rabbitmq-java-client-bin-3.6.4.zip)

### 安装步骤

1. 先安装Erlang,点击EXE执行即可，安装完要配置ERLANG_HOME环境变量 ，配置`ERLANG_HOME/bin`到系统路径PATH，cmd输入erl查看版本信息
2. 安装RabbitMQ，点击EXE执行，配置环境变量RABBITMQ_SERVER，配置`RABBITMQ_SERVER/sbin`到系统路径PATH
3. 同步Erlang Cookie，.erlang.cookie是erlang实现分布式的必要文件，erlang分布式的每个节点上要保持相同的.erlang.cookie文件，同时保证文件的权限是400,windows安装要保证windows系统和用户.erlang.cookie相同，具体操作复制`C:\Windows\.erlang.cookie`替`C:\Users\Administrator\.erlang.cookie`;
4. rabbitmq的两种启动方式
*  **以应用方式启动**
    * `rabbitmq-server -detached` 后台启动
    * `rabbitmq-server` 直接启动，如果你关闭窗口或者需要在改窗口使用其他命令时应用就会停止
    * `rabbitmqctl status` 查看是否启动Ok
    * `abbitmqctl stop`   关闭 <br/>
  显示如下为启动成功
  
  ````
  C:\Users\Administrator>rabbitmqctl status
Status of node 'rabbit@PC-20170618CMDN'
[{pid,5280},
 {running_applications,
     [{os_mon,"CPO  CXC 138 46","2.4.2"},
      {amqp_client,"RabbitMQ AMQP Client","3.6.10"},
      {rabbit_common,
          "Modules shared by rabbitmq-server and rabbitmq-erlang-client",
          "3.6.10"},
      {xmerl,"XML parser","1.3.13"},
      {mnesia,"MNESIA  CXC 138 12","4.14.3"},
      {cowboy,"Small, fast, modular HTTP server.","1.0.4"},
      {cowlib,"Support library for manipulating Web protocols.","1.0.2"},
      {ranch,"Socket acceptor pool for TCP protocols.","1.3.0"},
      {ssl,"Erlang/OTP SSL application","8.1.1"},
      {public_key,"Public key infrastructure","1.4"},
      {crypto,"CRYPTO","3.7.3"},
      {compiler,"ERTS  CXC 138 10","7.0.4"},
      {syntax_tools,"Syntax tools","2.1.1"},
      {asn1,"The Erlang ASN1 compiler version 4.0.4","4.0.4"},
      {inets,"INETS  CXC 138 49","6.3.6"},
      {sasl,"SASL  CXC 138 11","3.0.3"},
      {stdlib,"ERTS  CXC 138 10","3.3"},
      {kernel,"ERTS  CXC 138 10","5.2"}]},
 {os,{win32,nt}},
 {erlang_version,
     "Erlang/OTP 19 [erts-8.3] [64-bit] [smp:4:4] [async-threads:64]\n"},
 {memory,
     [{total,59749936},
      {connection_readers,0},
      {connection_writers,0},
      {connection_channels,0},
      {connection_other,2736},
      {queue_procs,0},
      {queue_slave_procs,0},
      {plugins,8520},
      {other_proc,25498368},
      {mnesia,58192},
      {metrics,0},
      {mgmt_db,0},
      {msg_index,0},
      {other_ets,2290976},
      {binary,240656},
      {code,22106807},
      {atom,891849},
      {other_system,8651832}]},
 {alarms,[]},
 {listeners,[]},
 {processes,[{limit,1048576},{used,100}]},
 {run_queue,0},
 {uptime,20},
 {kernel,{net_ticktime,60}}]
 ````

*  **以服务方式启动**
    * `rabbitmq-service install` 安装服务,安装完之后在任务管理器中服务一栏能看到RabbtiMq
    * `rabbitmq-service start` 开始服务
    * `abbitmq-service stop`  停止服务
    * `rabbitmq-service enable` 使服务有效
    * `rabbitmq-service disable` 使服务无效,当rabbitmq-service install之后默认服务是enable的，如果这时设置服务为disable的话，
         rabbitmq-service start就会报错。
    * `rabbitmq-service help` 帮助
    * `abbitmqctl stop`   关闭
 5. Rabbitmq管理插件启动
 
  * `rabbitmq-plugins enable rabbitmq_management` 启动
  * `rabbitmq-plugins disable rabbitmq_management` 关闭
  
6. 服务启动可以查看rabbitmq的web界面
     
     ![Rabbimq-eb](https://github.com/Letitmiss/JMS/blob/master/img/rabbitmq-1.jpg)
   默认的用户名和密码是guest
   
7. 账户管理
* 查看账户列表，只有guest

  ````
  C:\Users\Administrator>rabbitmqctl.bat list_users
  Listing users
  guest   [administrator]
  ````
  
* 新增一个rabbitmq的账，密码为rabbitmq123

    ````
    C:\Users\Administrator>rabbitmqctl.bat add_user rabbitmq rabbitmq123
    Creating user "rabbitmq"

    C:\Users\Administrator>rabbitmqctl.bat list_users
    Listing users
    rabbitmq        []
    guest   [administrator]
    ````

* 给rabbitmq用户设置管理员权限
  ````
  C:\Users\Administrator>rabbitmqctl.bat set_user_tags rabbitmq administrator
  Setting tags for user "rabbitmq" to [administrator]

  C:\Users\Administrator>rabbitmqctl.bat set_permissions -p / rabbitmq ".*" ".*" ".*"
  Setting permissions for user "rabbitmq" in vhost "/"

  C:\Users\Administrator>rabbitmqctl.bat list_users
  Listing users
  rabbitmq        [administrator]
  guest   [administrator]
  ````
 * 使用rabbitmq/rabbitmq123 登录web界面OK

### FAQ
1. 电脑系统中的其他应用程序已经安装了erlang的旧版本，需要卸载
2. 前面要求配置的环境变量没有配置完整
3. erlang cookie 没有同步






