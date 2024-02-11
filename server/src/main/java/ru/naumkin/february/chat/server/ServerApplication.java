package ru.naumkin.february.chat.server;

public class ServerApplication {
    public static void main(String[] args) {
        Server server = new Server(8090);
        server.start();
    }
}