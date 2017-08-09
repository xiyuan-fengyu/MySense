# MySense  

在跟着[elasticsearch官网中文文档](https://www.elastic.co/guide/cn/elasticsearch/guide/current/index.html)学习的过程中，sense无法安装成功，所以就自行开发了这个项目用于方便测试  

在idea中tomcat的配置如下：  
![tomcat_config_0](github/tomcat_config_0.png)  
![tomcat_config_1](github/tomcat_config_1.png)  

部署运行成功后，在浏览器中打开如下地址：
```
http://localhost:5601/app/sense
或者
http://localhost:5601/app/sense/?load_from=https://www.elastic.co/guide/cn/elasticsearch/guide/current/snippets/010_Intro/30_Get.json
```
可以看到运行效果