package com.example.heonsu.login;

/**
 * Created by Heonsu on 2015-10-25.
 */
public class User {

    private int u_id;
    private String email;
    private String name;

    public int getU_id() {
        return u_id;
    }

    public void setU_id(int u_id) {
        this.u_id = u_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
