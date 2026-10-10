package ru.mirea.pethotel.core.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "owners")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @Column(name = "fullName", nullable = false)
    private String fullName;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;


    public Owner(String login, String fullName, String phone) {
        this.login = login;
        this.fullName = fullName;
        this.phone = phone;
    }

    public Owner() {}


    @Override
    public String toString() {
        return "[ ID: " + id + ", login: " + login + ", имя: " + fullName + ", телефон: " + phone + "]";
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
