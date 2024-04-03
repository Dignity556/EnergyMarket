package yhy;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Random;


public class Seller {
    private int ID; // 卖家ID
    private int date = 1; // 当前日期
    private double k = 1; // realtime定价参数
    private int violation = 0; // 违规系数 0.75 0.5 0.1
    private double dayahead_price = 200; // dayahead价格
    private double realtime_price = 0; // realtime交易价格，非预测价格
    private double price = 0; // 预测价格
    private double cost = 100; // 商家成本
    private int distance = 1; // 距离
    private int environment = 1; // 环境因素
    private double heat = 1; // 商家购买热度
    private double stock_size = 1000; // 商家存量，初始为1000 （个人一个月200度）
    private double dayahead_average_price = 0; // 前一天成交均价 P
    private int dayahead_transaction_size = 0; // 前一天的成交量 M
    private double today_total_money = 0; // 当天总交易额
    private int today_trasaction_size = 0; // 当天成交能源量
    private int transaction_size = 0; // 历史总成交能源量    Bavg = transaction_size / transaction_count
    private int transaction_count = 0; // 历史交易总笔数 Txs

    private boolean if_trans = false; // 是否开张
    private double last_price = 0; // 前一天最后一笔成交价
    private double first_price = 0; // 前一天第一笔预测的realtime值


    private double reputation; // 商家信誉值


    private static final String DB_URL = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";

    public Seller(int ID, double reputation) {
        this.ID = ID;
        this.reputation = reputation;
    }

    public void orderProcess(String buyerID, String orderVolume) {
        // 将交易记录插入到数据库中
        int intID = Integer.parseInt(buyerID);
        int intOrderVolume = Integer.parseInt(orderVolume);
        // 计算 realtime 价格
        price = realTime();
        realtime_price = price + (reputation / 100 - 0.1) * price;
        // 更新 k
        update_K();
        // 便于计算热度
        if (!if_trans) {
            first_price = price;
        }
        last_price = realtime_price;

        // 插入数据库
        insertTransTable(intID, intOrderVolume);

        // 更新当天交易量，交易笔数，库存量
        today_trasaction_size += intOrderVolume;
        today_total_money += realtime_price * intOrderVolume;
        transaction_size += intOrderVolume;
        transaction_count += 1;
        stock_size -= intOrderVolume;

    }
    // 数据库操作，交易记录表transaction_csv
    public void insertTransTable(int buyerID, int orderVolume) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // 连接数据库
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // 执行插入操作
            String sql = "INSERT INTO transaction_csv (MerchantID, PurchaserID, TimeStamp, Date, Reputation, Distance, Environment, TransactionSize, dayahead_price, predicted_realtime_price, realtime_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql); // 预编译SQL语句
            stmt.setInt(1, ID);
            stmt.setInt(2, buyerID);
            stmt.setDate(3, new Date(System.currentTimeMillis()));
            stmt.setInt(4, date);
            stmt.setBigDecimal(5, new BigDecimal(reputation));
            stmt.setInt(6, distance);
            stmt.setInt(7, environment);
            stmt.setBigDecimal(8, new BigDecimal(orderVolume));
            stmt.setBigDecimal(9, new BigDecimal(dayahead_price));
            stmt.setBigDecimal(10, new BigDecimal(price));
            stmt.setBigDecimal(11, new BigDecimal(realtime_price));
            stmt.executeUpdate(); // 执行SQL语句
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
    // 计算 realtime 定价
    public double realTime() {
        double result = heat * dayahead_price * k; // * environment * distance
        if (result <= 100) {
            return 100;
        } else if (result >= 300) {
            return 300;
        }
        return result;
    }
    // 更新 k 值
    public void update_K() {
        // 学习率
        double learningRate = 0.01;
        // 计算误差
        double error = realtime_price - price;
        // 计算MSE
        double mse = Math.pow(error, 2) / 2;
        // 计算损失函数对参数的梯度 mse对k的偏导数
        double gradient = mse * heat * dayahead_price ;
        // 更新参数
        k = k - learningRate * gradient;
    }


    // dayahead定价
    public void dayAhead(double vl) {
        // 补充库存
        stock_size += 1000;
        // 更新日期
        date += 1;
        // 更新 heat, 无交易不更新
        if (if_trans) {
            heat = (last_price - first_price) / first_price;
            if_trans = false;
            last_price = 0;
            first_price = 0;
        }
        // 更新 P
        dayahead_average_price = today_total_money / today_trasaction_size;
        today_total_money = 0;
        // 更新 M
        dayahead_transaction_size = today_trasaction_size;
        today_trasaction_size = 0;
        // 更新信誉值
        upDateReputation();
        // 更新 dayahead 定价
        dayahead_price = day_ahead();
        // 插入数据库表
        insertMerchantTable(vl);

    }

    // 计算 realtime 定价
    public double day_ahead() {
        double result = reputation * dayahead_transaction_size * dayahead_average_price / stock_size;

        return result;
    }
    // 更新信誉值
    public void upDateReputation() {
        reputation = (1 - violation) * stock_size ;
    }

    public void insertMerchantTable(double vl) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // 连接数据库
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // 执行插入操作
            String sql = "INSERT INTO merchant" + ID + " (MerchantID, Date, ViolationLevel, MerchantSize, Average_single_transaction, historical_transactions, Reputation, previous_day_transactions, Average_transaction_Price_of_Previous_Day) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql); // 预编译SQL语句
            stmt.setInt(1, ID);
            stmt.setInt(2, date - 1);
            stmt.setBigDecimal(3, new BigDecimal(vl));
            stmt.setBigDecimal(4, new BigDecimal(stock_size));
            stmt.setBigDecimal(5, new BigDecimal(transaction_size / transaction_count));
            stmt.setInt(6, transaction_size);
            stmt.setBigDecimal(7, new BigDecimal(reputation));
            stmt.setInt(8, dayahead_transaction_size);
            stmt.setBigDecimal(9, new BigDecimal(dayahead_average_price));
            stmt.executeUpdate(); // 执行SQL语句
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

    public double getReputation() {
        return reputation;
    }
}
