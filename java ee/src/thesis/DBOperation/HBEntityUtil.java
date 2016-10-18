package thesis.DBOperation;

import org.hibernate.Session;
import thesis.JavaBean.UserInfoEntity;

/**
 * Created by Lin on 2016/1/29.
 */
public class HBEntityUtil {
    public static UserInfoEntity getUserInfo(String id){
        Session session = HBUtil.getSession();
        UserInfoEntity userInfoEntity = (UserInfoEntity)session
                .get(UserInfoEntity.class, id);
        session.close();
        return userInfoEntity;
    }
}
