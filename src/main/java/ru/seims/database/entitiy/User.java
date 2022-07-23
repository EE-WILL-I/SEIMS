package ru.seims.database.entitiy;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @Column(name = "roleid")
    private String roleId;
    private String login;
    private String passwd;
    @Column(name = "fname")
    private String firstName;
    @Column(name = "lname")
    private String lastName;
    @Column(name = "pname")
    private String patronymic;
    @Column(name = "params")
    private String paramString;

    protected User() {}

    public User(String id, String roleId, String login, String firstName, String lastName, String patronymic, String paramString) {
        this.id = id;
        this.roleId = roleId;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.paramString = paramString;
    }

    public User(DecodedJWT token) {
        this.login = token.getClaim("login").asString();
        this.id = token.getClaim("userId").asString();
        this.roleId = token.getClaim("role").asString();
        this.firstName = token.getClaim("fname").asString();
        this.lastName = token.getClaim("lname").asString();
        this.patronymic = token.getClaim("pname").asString();
        this.paramString = token.getClaim("params").asString();
    }

    public String getFullName() {
        return String.format("%s %s %s", firstName, lastName, patronymic);
    }

    public String getLocale() {
        return paramString.split(";")[0];
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

    public void setPasswd(String password) {
        this.passwd = password;
    }
    public String getPasswd() {
        return passwd;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", roleId='" + roleId + '\'' +
                ", login='" + login + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        return passwd;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
