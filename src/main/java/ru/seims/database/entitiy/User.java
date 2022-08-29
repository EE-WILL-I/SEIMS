package ru.seims.database.entitiy;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Long id;
    @Column(name = "roleid")
    private String roleId;
    @Column(name = "login")
    private String username;
    @Column(name = "passwd")
    private String password;
    @Column(name = "fname")
    private String firstName;
    @Column(name = "lname")
    private String lastName;
    @Column(name = "pname")
    private String patronymic;
    @Column(name = "params")
    private String paramString;
    @Column(name = "active")
    private boolean enabled;
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    protected User() {}

    public User(Long id, String roleId, String username, String firstName, String lastName, String patronymic, String paramString, boolean enabled) {
        this.id = id;
        this.roleId = roleId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.paramString = paramString;
        this.enabled = enabled;
    }

    public User(DecodedJWT token) {
        Role role = new Role(token.getClaim("roleId").asInt(), token.getClaim("role").asString());
        this.username = token.getClaim("login").asString();
        this.id = token.getClaim("userId").asLong();
        this.roles = new ArrayList<Role>();
        this.roles.add(role);
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

    public void setUsername(String login) {
        this.username = login;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getEnabled() { return enabled; }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Role getPrimaryAuthority() {
        Role role = null;
        if(roles.iterator().hasNext())
            role = roles.iterator().next();
        return role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", roleId='" + roleId + '\'' +
                ", login='" + username + '\'' +
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
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
