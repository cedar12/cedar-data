package cn.cedar;

import cn.cedar.data.JdbcUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcExtension extends JdbcUtil {

    @Override
    public Connection getConnection() {
        System.out.println("不使用jdbc连接。。。");
        try {
            if(isClosed()){
                connection=DriverManager.getConnection(getUrl(),getUser(),getPassword());
            }
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
