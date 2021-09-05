package com.wanhex.anxinpassword;

public class Password {

    private String category;
    private String site;
    private String abbreviatedUserName;
    private String username;
    private String password;
    private String comments;
    private long timeStamp;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getAbbreviatedUserName() {
        return abbreviatedUserName;
    }

    public void setAbbreviatedUserName(String abbreviatedUserName) {
        this.abbreviatedUserName = abbreviatedUserName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getTimeStampStr() {
        return "timeStamp";
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getImageId() {
        return R.mipmap.lock;
    }

    public String getSummary() {
        return "Summary";
    }

}
