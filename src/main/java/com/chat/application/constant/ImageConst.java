package com.chat.application.constant;

public enum ImageConst {
    ME("我","/META-INF/resources/avatars/me.png","me.png"),
    LOGIN("login", "/META-INF/resources/images/login.png","login.png"),
    CHATROOM1("1号", "/META-INF/resources/avatars/ChatRoom1.png","ChatRoom1.png"),
    CHATROOM2("2号", "/META-INF/resources/avatars/ChatRoom2.png","ChatRoom2.png");
    private final String name;
    private final String path;

    private final String fileName;

    ImageConst(String name, String path, String fileName){
        this.name = name;
        this.path = path;
        this.fileName = fileName;
    }

    public String getName(){
        return name;
    }

    public String getPath(){
        return path;
    }

    public String getFileName(){
        return fileName;
    }

}
