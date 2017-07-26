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


