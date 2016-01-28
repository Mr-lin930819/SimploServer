package thesis.JavaBean;

public class UserInfo {
    private String openAppUserId;
    private String stuNumber;
    private String stuPassword;
    private String storedCookie;

    public String getGenDate() {
        return genDate;
    }

    public void setGenDate(String genDate) {
        this.genDate = genDate;
    }


    public String getStuNumber() {
        return stuNumber;
    }

    public void setStuNumber(String stuNumber) {
        this.stuNumber = stuNumber;
    }

    public String getStuPassword() {
        return stuPassword;
    }

    public void setStuPassword(String stuPassword) {
        this.stuPassword = stuPassword;
    }

    public String getOpenAppUserId() {
        return openAppUserId;
    }

    public void setOpenAppUserId(String openAppUserId) {
        this.openAppUserId = openAppUserId;
    }

    public String getStoredCookie() {
        return storedCookie;
    }

    public void setStoredCookie(String storedCookie) {
        this.storedCookie = storedCookie;
    }

    private String genDate;
}
