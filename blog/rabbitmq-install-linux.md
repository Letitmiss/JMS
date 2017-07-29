###  [首 页](https://github.com/Letitmiss/JMS/blob/master/README.md)
# RabbitMQ安装Linux

## 安装步骤 

### 安装Erlang
1. 虚拟机环境是CentOS7.9，执行`yum install gcc glibc-devel make ncurses-devel openssl-devel xmlto` 安装依赖
2. 安装erlang,地址http://www.erlang.org/downloads. 我选择的是otp_src_18.3.tar.gz。
* 在指定下载目录下执行`wget http://erlang.org/download/otp_src_18.3.tar.gz`获取下载包
  ```
  [root@gaocong rabbitmq]# tar -zxvf otp_src_18.3.tar.gz
  [root@gaocong rabbitmq]# cd otp_src_18.3
  [root@gaocong otp_src_18.3]# ./configure --prefix=/opt/erlang
  ````
  然后会出现以下信息：
  ````
  *********************************************************************
  **********************  APPLICATIONS DISABLED  **********************
  *********************************************************************
  odbc           : ODBC library - link check failed

  只需要关注 APPLICATIONS DISABLED的提示信息
  安装  执行 yum install unixODBC unixODBC-devel 解决
  [root@gaocong otp_src_18.3]# make && make install

  [root@gaocong opt]# cd /opt/erlang/bin/
  [root@gaocong bin]# ./erl
  Erlang/OTP 18 [erts-7.3] [source] [async-threads:10] [hipe] [kernel-poll:false]

  Eshell V7.3  (abort with ^G)
  1> 
 
* 在配置Erlang环境变量,vi /etc/profile文件，增加下面的环境变量
    ````
    #set erlang environment 
    export PATH=$PATH:/opt/erlang/bin
    source  /etc/profile 使得文件生效
    
### 安装RaabitMQ
1. 安装rabbitmq，下载地址http://www.rabbitmq.com/releases/rabbitmq-server/v3.6.10/  按照
    一步步操作
    ````
    wget http://www.rabbitmq.com/releases/rabbitmq-server/v3.6.10/rabbitmq-server-generic-unix-3.6.10.tar.xz
    [root@gaocong rabbitmq]# xz -d rabbitmq-server-generic-unix-3.6.10.tar.xz
    [root@gaocong rabbitmq]# tar -xvf rabbitmq-server-generic-unix-3.6.10.tar 
    [root@gaocong rabbitmq]# mv rabbitmq_server-3.6.10/ rabbitmq
    [root@gaocong rabbitmq]# vim /etc/profile
        #set rabbitmq environment
        export PATH=$PATH:/gaocong/rabbitmq/rabbitmq/sbin 配置自己的解压目录
    [root@gaocong rabbitmq]# source /etc/profile
    [root@gaocong sbin]# ./rabbitmq-server -detached
       Warning: PID file not written; -detached was passed.
    [root@gaocong sbin]# ./rabbitmqctl status
    Status of node rabbit@gaocong
        [{pid,1169},
        {running_applications,
        [{rabbit,"RabbitMQ","3.6.10"},
        {mnesia,"MNESIA  CXC 138 12","4.13.3"},
        {ranch,"Socket acceptor pool for TCP protocols.","1.3.0"},
              .....
        {run_queue,0},
        {uptime,49},
        {kernel,{net_ticktime,60}}]
    [root@gaocong sbin]# ps -ef | grep rabbitmq
         ````
2. 登录web界面查看
  ````
   [root@gaocong sbin]# ./rabbitmq-plugins enable rabbitmq_management
    The following plugins have been enabled:
    amqp_client
    cowlib
    cowboy
    rabbitmq_web_dispatch
    rabbitmq_management_agent
    rabbitmq_management

    Applying plugin configuration to rabbit@gaocong... started 6 plugins.
   ````
    配置linux 端口 15672 网页管理  5672 AMQP端口, 此时远程可以打开界面，但是guest/guest 也是不能登录的
    
   参考windows安装的时候，添加用户的方式，添加新用户，赋予管理员权限就可以登录了
   ![login](https://github.com/Letitmiss/JMS/blob/master/img/activemq-2.jpg)  
### RabbitMQ的管理 

* rabbitmq通过rabbitmqctl命令管理 abbitmq常用命令
    * `add_user       <UserName> <Password>`
    * `delete_user    <UserName>`
    * `change_password <UserName> <NewPassword>`
    * `list_users`
    * `add_vhost    <VHostPath>`
    * `delete_vhost <VHostPath>`
    * `list_vhostsset_permissions  [-p <VHostPath>] <UserName> <Regexp> <Regexp> <Regexp>`
    * `clear_permissions [-p <VHostPath>] <UserName>`
    * `list_permissions  [-p <VHostPath>]`
    * `list_user_permissions <UserName>`
    * `list_queues    [-p <VHostPath>] [<QueueInfoItem> ...]`
    * `list_exchanges [-p <VHostPath>] [<ExchangeInfoItem> ...]`
    * `list_bindings  [-p <VHostPath>]`
    * `list_connections [<ConnectionInfoItem> ...]`
   
