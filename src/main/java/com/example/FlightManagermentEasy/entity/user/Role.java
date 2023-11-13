package com.example.FlightManagermentEasy.entity.user;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<AccountRole> accountRoleList;

    public Role(long id, String name, List<AccountRole> accountRoleList) {
        this.id = id;
        this.name = name;
        this.accountRoleList = accountRoleList;
    }

    public Role(String name) {
        this.name = name;
    }

    public Role() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AccountRole> getAccountRoleList() {
        return accountRoleList;
    }

    public void setAccountRoleList(List<AccountRole> accountRoleList) {
        this.accountRoleList = accountRoleList;
    }
}
