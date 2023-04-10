# My App

项目基于Java 17和Open AI's 的API，仅供代码学习使用，禁止商用


## Requirements

你需要注册一个OpenAI的key并放入application-dev.yml -> service.key.openai

项目做了扩展，可以轻易的接入其他的AI，参数为上下文信息
## Running the application

启动项目 -> http://localhost:8080
由于不同的API可能不支持大陆访问，如果要访问可能需要一个海外服务器

## Deploying to Production
生产模式打包 mvnw clean package -Pproduction`
将target目录下打包的mychatbot-1.0-SNAPSHOT.jar部署在服务器上，并创建同级目录/config, 把application.yml和application-dev.yml放入 （可以只用一个）
用java -jar mychatbot-1.0-SNAPSHOT.jar运行程序

##其他
1. 项目中默认限制了上下文长度为3000字，这个自己调节
2. 代码写的比较粗糙，样式的设置尽可能写在css里

##Demo
![image](https://user-images.githubusercontent.com/128681247/230880488-bc3ca29d-d728-4425-9240-1fab43570eac.png)