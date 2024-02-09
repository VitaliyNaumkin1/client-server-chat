package ru.naumkin.february.chat.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryUserService implements UserService {
    class User {

        private String login;
        private String password;
        private String name;
        private UserRole role;


        public User(String login, String password, String userName, UserRole role) {
            this.login = login;
            this.password = password;
            this.name = userName;
            this.role = role;
        }


    }


    List<User> users;

    public InMemoryUserService() {
        this.users = new ArrayList<>(Arrays.asList(
                new User("login1", "pas1", "name1", UserRole.ADMIN),
                new User("login2", "pas2", "name2", UserRole.USER),
                new User("login3", "pas3", "name3", UserRole.USER)
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
    public void createNewUser(String login, String password, String userName, UserRole role) {
        users.add(new User(login, password, userName, role));
    }

    @Override
    public void deleteUser(String name) {
        for (User u : users) {
            if (u.name.equals(name)) {
                users.remove(u);
            }
        }
    }

    @Override
    public UserRole getUserRole(String name) {
        for (User u : users) {
            if (u.name.equals(name)) {
                return u.role;
            }
        }
        return null;
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
