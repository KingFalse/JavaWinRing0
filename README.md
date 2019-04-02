# JavaWinRing0

> 此项目使用JNA调用WinRing0实现驱动层级键盘模拟，可用于国内网银控件自动输入密码。
> 建议在虚拟机中使用/调试，反正生产环境也是云端虚拟机。我自己开发是win7阿里云部署是server 2012 R2
> 欢迎交流：https://www.kagura.me

启动如果报错`java.lang.UnsatisfiedLinkError: Unable to load library 'WinRing0x64':`请将lib目录下的四个文件拷贝到jdk或者jre的bin目录下！！！

启动如果报错`java.lang.UnsatisfiedLinkError: Unable to load library 'WinRing0x64':`请将lib目录下的四个文件拷贝到jdk或者jre的bin目录下！！！

启动如果报错`java.lang.UnsatisfiedLinkError: Unable to load library 'WinRing0x64':`请将lib目录下的四个文件拷贝到jdk或者jre的bin目录下！！！
# RedAlert
* 请使用Administrator管理员账号启动程序
* 建议在虚拟机中使用/调试，反正生产环境也是云端虚拟机。我自己开发是win7阿里云部署是server 2012 R2
* 云端生产环境部署时建议将操作系统换成英语，避免一些输入法之类的问题
# 简单使用
* mvn package
* java -jar JavaWinRing0.jar
* http://127.0.0.1:8080/press?keys=kagura
