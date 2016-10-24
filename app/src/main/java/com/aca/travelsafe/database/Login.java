package com.aca.travelsafe.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Marsel on 6/4/2016.
 */
@Table(database = DBMaster.class)
public class Login extends BaseModel {

    public static String LOGOUT = "false";
    public static String LOGIN = "true";

    @Column
    @PrimaryKey
    public String
            UserId;

    @Column
    public String
            UserPass,
            LoginStatus;

    public static String getUserID() {
        Login login = new Select().from(Login.class).querySingle();

        if (login == null) {
            return "";
        }
        return login.UserId;
    }

    public static boolean isLogin() {
        Login login = new Select().from(Login.class).querySingle();

        if (login == null)
            return false;

        boolean isLogin = Boolean.parseBoolean(login.LoginStatus);
        return isLogin;
    }

}
