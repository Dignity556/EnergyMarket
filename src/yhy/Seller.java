package yhy;

public class Seller {
    private int ID; // 卖家ID
    private int date = 0; // 当前日期
    private int violation = 0; // 违规系数
    private double dayahead; // dayahead价格
    private double cost; // 商家成本
    private double stock_size = 1000; // 商家存量，初始为1000 （个人一个月200度）
    private int average_price = 0; // 平均单笔成交价格
    private int transaction_count = 0; // 历史成交数量
    private int yesterday_count = 0; // 前一天成交数量
    private double reputation = 0; // 商家信誉值

    public Seller(int ID, double reputation) {
        this.ID = ID;
        this.reputation = reputation;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getViolation() {
        return violation;
    }

    public void setViolation(int violation) {
        this.violation = violation;
    }

    public double getDayahead() {
        return dayahead;
    }

    public void setDayahead(double dayahead) {
        this.dayahead = dayahead;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getStock_size() {
        return stock_size;
    }

    public void setStock_size(double stock_size) {
        this.stock_size = stock_size;
    }

    public int getAverage_price() {
        return average_price;
    }

    public void setAverage_price(int average_price) {
        this.average_price = average_price;
    }

    public int getTransaction_count() {
        return transaction_count;
    }

    public void setTransaction_count(int transaction_count) {
        this.transaction_count = transaction_count;
    }

    public int getYesterday_count() {
        return yesterday_count;
    }

    public void setYesterday_count(int yesterday_count) {
        this.yesterday_count = yesterday_count;
    }

    public double getReputation() {
        return reputation;
    }

    public void setReputation(double reputation) {
        this.reputation = reputation;
    }
}
