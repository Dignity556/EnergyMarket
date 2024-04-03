package yhy;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class test {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";


    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // 连接数据库
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // 执行插入操作
            String sql = "DELETE FROM merchant1";
            stmt = conn.prepareStatement(sql); // 预编译SQL语句
            stmt.executeUpdate(); // 执行SQL语句
//            // 连接数据库
//            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//            // 执行插入操作
//            String sql = "INSERT INTO test (col1, col2) VALUES (?, ?)";
//            stmt = conn.prepareStatement(sql); // 预编译SQL语句
//            stmt.setInt(1, 1);
//            stmt.setString(2, "test1");
//            stmt.executeUpdate(); // 执行SQL语句
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close(); // 关闭Statement
                if (conn != null) conn.close(); // 关闭Connection
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

