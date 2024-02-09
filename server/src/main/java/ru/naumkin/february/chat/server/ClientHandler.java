package ru.naumkin.february.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                authentication();
                listenUserChatMessage(server);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    private void listenUserChatMessage(Server server) throws IOException {
        while (true) {
            String message = in.readUTF();
            String[] messageElements = message.split(" ", 3);
            if (message.startsWith("/")) {
                if (message.equals("/exit")) {
                    break;
                }
                if (message.startsWith("/w ")) {
                    String receiverUserName = messageElements[1];
                    String privateMessage = messageElements[2];
                    server.sendPrivateMessage(this, receiverUserName, privateMessage);
                }
            } else {
                server.broadcastMessage(userName + ": " + message);
            }
        }
    }


    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            boolean isSucceed = false;
            if (message.startsWith("/auth ")) { //  /auth log1 pas1
                isSucceed = tryToAuthenticate(message);
            } else if (message.startsWith("/register ")) {//  /register log4 pas4 name4
                isSucceed = register(message);
            } else {
                sendMessage("СЕРВЕР: для общения необходимо войти в учетную запись или зарегистрироваться");
            }
            if (isSucceed) {
                break;
            }
        }
    }

    private boolean tryToAuthenticate(String message) {
        String[] messageElements = message.split(" ");
        if (messageElements.length != 3) {
            sendMessage("СЕРВЕР: некорректная форма аутентификации");
            return false;
        }
        String userLogin = messageElements[1];
        String userPassword = messageElements[2];
        String userNameFromDataBase = server.getUserService().getUserNameByLoginAndPassword(userLogin, userPassword);
        if (userNameFromDataBase == null) {
            sendMessage("СЕРВЕР: пользователя с указанным логином/паролем не существует");
            return false;
        }
        if (server.isUserAlreadyExist(userNameFromDataBase)) {
            sendMessage("СЕРВЕР: учётная запись уже занята");
            return false;
        }
        userName = userNameFromDataBase;
        server.subscribe(this);
        sendMessage("/authok " + userName);
        sendMessage("СЕРВЕР: " + userName + " добро пожаловать в чат");
        return true;

    }

    private boolean register(String message) {
        String[] messageElements = message.split(" ");
        if (messageElements.length != 4) {
            sendMessage("СЕРВЕР: некорректная команда аутентификации ");
            return false;
        }
        String login = messageElements[1];
        String password = messageElements[2];
        String registrationUserName = messageElements[3];
        if (server.getUserService().isLoginAlreadyExist(login)) {
            sendMessage("СЕРВЕР: указанный логин уже занят");
            return false;
        }
        if (server.getUserService().isUserNameAlreadyExist(registrationUserName)) {
            sendMessage("СЕРВЕР: указанное имя уже занято");
            return false;
        }

        server.getUserService().createNewUser(login, password, registrationUserName);
        userName = registrationUserName;
        sendMessage("/authok " + userName);
        sendMessage("СЕРВЕР: " + userName + ", вы успешно прошли регистрацию добро пожаловать в чат");
        server.subscribe(this);
        return true;
    }

}
