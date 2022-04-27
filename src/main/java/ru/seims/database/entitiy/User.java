package ru.seims.database.entitiy;

import com.auth0.jwt.interfaces.DecodedJWT;

public class User {
    private String id;
    private String roleId;
    private String login;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String paramString;
    private String locale;

    public User(String id, String roleId, String login, String firstName, String lastName, String patronymic, String paramString) {
        this.id = id;
        this.roleId = roleId;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.paramString = paramString;
        this.locale = paramString.split(";")[0];
    }

    public User(DecodedJWT token) {
        this.login = token.getClaim("login").asString();
        this.id = token.getClaim("userId").asString();
        this.roleId = token.getClaim("role").asString();
        this.firstName = token.getClaim("fname").asString();
        this.lastName = token.getClaim("lname").asString();
        this.patronymic = token.getClaim("pname").asString();
        this.paramString = token.getClaim("locale").asString();
    }

    public String getFullName() {
        return String.format("%s %s %s", firstName, lastName, patronymic);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getParamString() {
        return paramString;
    }

    public void setParamString(String paramString) {
        this.paramString = paramString;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }
}
