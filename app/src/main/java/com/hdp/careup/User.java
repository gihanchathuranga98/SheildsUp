package com.hdp.careup;

public class User {
    private String fName;
    private String lName;
    private String displayName;
    private String mobile;
    private int userStat;

    public User() {
    }

    public User(String fName, String lName, String displayName, String mobile, String email) {
        this();
        this.fName = fName;
        this.lName = lName;
        this.displayName = displayName;
        this.mobile = mobile;
        this.email = email;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;

    public int getUserStat() {
        return userStat;
    }

    public void setUserStat(int userStat) {
        this.userStat = userStat;
    }
}
