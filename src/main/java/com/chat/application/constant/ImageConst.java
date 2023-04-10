package com.chat.application.constant;

public enum ImageConst {
    ME("我","/META-INF/resources/avatars/me.png","me.png"),

    LOGIN("login", "/META-INF/resources/images/login.png","login.png"),
    DEFAULTCHATROOM("小王","/META-INF/resources/avatars/defaultChatRoom.png","defaultChatRoom.png"),
    PHILCHATROOM("哲学老师","/META-INF/resources/avatars/philChatRoom.png","philChatRoom.png");
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
