# My App

项目基于Java 17以及Vaadin框架，调用AI回复需要使用Open AI的API Key，可以去OpenAI的官网获取

该项目做了扩展，可以支持其他的AI接口快速接入，例如该项目中的BaiduResponseServiceImpl

该项目仅供代码学习交流，禁止商用以及其他恶意使用行为

## Requirements

你需要注册一个OpenAI的API key并放入application-dev.yml -> service.key.openai

## Running the application

启动项目 -> http://localhost:8080
有些API供应商不支持大陆访问，如果要访问可能需要一个海外服务器

## Deploying to Production
生产模式打包 mvnw clean package -Pproduction`
将target目录下打包的mychatbot-1.0-SNAPSHOT.jar部署在服务器上，并创建同级目录/config, 把application.yml和application-dev.yml放入 （可以只用一个）
用java -jar mychatbot-1.0-SNAPSHOT.jar运行程序

##其他
1. 项目中默认限制了上下文长度为3000字，这个可以自行调节
2. 代码写的比较粗糙，例如样式的设置可以尽可能地写在css里

##Demo
![image](https://user-images.githubusercontent.com/128681247/230880488-bc3ca29d-d728-4425-9240-1fab43570eac.png)
