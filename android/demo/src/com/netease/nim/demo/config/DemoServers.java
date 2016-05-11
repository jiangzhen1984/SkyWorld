package com.netease.nim.demo.config;

public class DemoServers {

    //
    // �����б���Ϣ��������ַ
    //
    private static final String API_SERVER_TEST = "https://apptest.netease.im/api/"; // ����
    private static final String API_SERVER = "https://app.netease.im/api/"; // ����

    public static final String apiServer() {
        return ServerConfig.testServer() ? API_SERVER_TEST : API_SERVER;
    }

    public static final String chatRoomAPIServer() {
        return apiServer() + "chatroom/";
    }
}
