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
            Connection conn=DriverManager.getConnection(getUrl(),getUser(),getPassword());
            setConnection(conn);
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
