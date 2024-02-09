package ru.naumkin.february.chat.server;

public interface UserService {
    public String getUserNameByLoginAndPassword(String login, String password);

    public void createNewUser(String login, String password, String userName);

    public boolean isLoginAlreadyExist(String login);

    public boolean isUserNameAlreadyExist(String name);
}
