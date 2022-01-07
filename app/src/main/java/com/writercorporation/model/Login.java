package com.writercorporation.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by amol.tate on 3/16/2016.
 */
public class Login {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String username;
    @DatabaseField
    private String password;
    @DatabaseField
    private String loginTime;
    @DatabaseField
    private String isHousekeeping;

    public Login() {
    }

    public Login(String username, String password, String loginTime, String isHousekeeping) {
        this.username = username;
        this.password = password;
        this.loginTime = loginTime;
        this.isHousekeeping = isHousekeeping;

    }
    public String getIsHousekeeping() {
        return isHousekeeping;
    }

    public void setIsHousekeeping(String isHousekeeping) {
        this.isHousekeeping = isHousekeeping;
    }
    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * @return
     */
    public Login setUsername(String username) {
        this.username = username;
        return this;
    }


    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     * @return
     */
    public Login setPassword(String password) {
        this.password = password;
        return this;
    }
}
