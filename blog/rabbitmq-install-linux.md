###  [首 页](https://github.com/Letitmiss/JMS/blob/master/README.md)
# RabbitMQ安装Linux

## 下载资源

1. 虚拟机环境是CentOS，执行`yum install gcc glibc-devel make ncurses-devel openssl-devel xmlto` 安装依赖
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
    ````
3. 安装rabbitmq，地址 
   

  
   
