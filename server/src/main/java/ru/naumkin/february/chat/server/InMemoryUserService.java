package ru.naumkin.february.chat.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryUserService implements UserService {
    class User {

        private String login;
        private String password;
        private String name;


        public User(String login, String password, String userName) {
            this.login = login;
            this.password = password;
            this.name = userName;
        }


    }


    List<User> users;

    public InMemoryUserService() {
        this.users = new ArrayList<>(Arrays.asList(
                new User("login1", "pas1", "name1"),
                new User("login2", "pas2", "name2"),
                new User("login3", "pas3", "name3")
        ));
    }


    @Override
    public String getUserNameByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user.name;
            }
        }
        return null;
    }

    @Override
    public void createNewUser(String login, String password, String userName) {
        users.add(new User(login, password, userName));
    }


    @Override
    public boolean isLoginAlreadyExist(String login) {
        for (User u : users) {
            if (u.login.equals(login)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUserNameAlreadyExist(String name) {
        for (User u : users) {
            if (u.login.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
