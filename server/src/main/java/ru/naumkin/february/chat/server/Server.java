package ru.naumkin.february.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private UserService userService;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public UserService getUserService() {
        return userService;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Сервер запущен на порту %d. Ожидание подключения клиентов\n", port);
            userService = new PostgresUserService();
            System.out.println("Запущен сервис для работы с пользователями");
            while (true) {
                Socket socket = serverSocket.accept();
                try {
                    new ClientHandler(this, socket);
                } catch (IOException e) {
                    System.out.println("Не удалось подключить клиента");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void kickUser(ClientHandler clientHandler, String userNameForKick, String kickersName) {
        unsubscribe(clientHandler);
        broadcastMessage("ADMIN - " + kickersName + " кикнул пользователя " + userNameForKick + " из чата");
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(message);
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        broadcastMessage("Подключился новый клиент " + clientHandler.getUserName());
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMessage("Отключился клиент " + clientHandler.getUserName());
    }

    public synchronized boolean isUserAlreadyExist(String userName) {
        for (ClientHandler c : clients) {
            if (c.getUserName().equals(userName)) {
                return true;
            }
        }
        return false;
    }

    public synchronized ClientHandler getClientHandlerByName(String name) {
        for (ClientHandler c : clients) {
            if (c.getUserName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public synchronized void sendPrivateMessage(ClientHandler sender, String receiverUserName, String message) {
        ClientHandler receiver = null;
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getUserName().equals(receiverUserName)) {
                receiver = clients.get(i);
            }
        }
        if (receiver == null) {
            sender.sendMessage("!!! ---> Не возможно отправить сообщение пользователю: " + receiverUserName + " - такого пользователя нету в чате");
            return;
        }
        sender.sendMessage("ЛИЧНОЕ СООБЩЕНИЕ ПОЛЬЗОВАТЕЛЮ " + sender.getUserName() + " : " + message);
        receiver.sendMessage("ЛИЧНОЕ СООБЩЕНИЕ ОТ ПОЛЬЗОВАТЕЛЯ " + sender.getUserName() + " : " + message);
    }
}
