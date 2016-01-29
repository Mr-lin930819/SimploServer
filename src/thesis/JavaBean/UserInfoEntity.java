package thesis.JavaBean;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by Lin on 2016/1/29.
 */
@Entity
@Table(name = "userstable", schema = "simplo_server", catalog = "")
public class UserInfoEntity {
    private String openAppUserId;
    private String stuNumber;
    private String stuPassword;
    private String storedCookie;
    private Date genDate;
    private String stuName;

    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    
    public String getOpenAppUserId() {
        return openAppUserId;
    }

    public void setOpenAppUserId(String openAppUserId) {
        this.openAppUserId = openAppUserId;
    }

    @Basic
    @Column(name = "stuNumber", nullable = true, length = 13)
    public String getStuNumber() {
        return stuNumber;
    }

    public void setStuNumber(String stuNumber) {
        this.stuNumber = stuNumber;
    }

    @Basic
    @Column(name = "stuPassword", nullable = true, length = 25)
    public String getStuPassword() {
        return stuPassword;
    }

    public void setStuPassword(String stuPassword) {
        this.stuPassword = stuPassword;
    }

    @Basic
    @Column(name = "storedCookie", nullable = true, length = 50)
    public String getStoredCookie() {
        return storedCookie;
    }

    public void setStoredCookie(String storedCookie) {
        this.storedCookie = storedCookie;
    }

    @Basic
    @Column(name = "genDate", nullable = true)
    public Date getGenDate() {
        return genDate;
    }

    public void setGenDate(Date genDate) {
        this.genDate = genDate;
    }

    @Basic
    @Column(name = "stuName", nullable = true, length = 12)
    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInfoEntity that = (UserInfoEntity) o;

        if (openAppUserId != null ? !openAppUserId.equals(that.openAppUserId) : that.openAppUserId != null)
            return false;
        if (stuNumber != null ? !stuNumber.equals(that.stuNumber) : that.stuNumber != null) return false;
        if (stuPassword != null ? !stuPassword.equals(that.stuPassword) : that.stuPassword != null) return false;
        if (storedCookie != null ? !storedCookie.equals(that.storedCookie) : that.storedCookie != null) return false;
        if (genDate != null ? !genDate.equals(that.genDate) : that.genDate != null) return false;
        if (stuName != null ? !stuName.equals(that.stuName) : that.stuName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = openAppUserId != null ? openAppUserId.hashCode() : 0;
        result = 31 * result + (stuNumber != null ? stuNumber.hashCode() : 0);
        result = 31 * result + (stuPassword != null ? stuPassword.hashCode() : 0);
        result = 31 * result + (storedCookie != null ? storedCookie.hashCode() : 0);
        result = 31 * result + (genDate != null ? genDate.hashCode() : 0);
        result = 31 * result + (stuName != null ? stuName.hashCode() : 0);
        return result;
    }
}
