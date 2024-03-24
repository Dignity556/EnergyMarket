package baselineMarket001;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class MerchantAgent extends Agent {
    private int transactionCount = 0; // 交易次数
    private double cost; // 成本
    private double reputation = Math.random() * 5; // 商家信誉值初始值为一个随机数
    private ArrayList<Product> products; // 商品集合

    // 数据库连接信息 要注意设置时区和禁用SSL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/jadetest?serverTimezone=UTC&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "zhouxiaoxuan2005";

    protected void setup() {
        // 初始化商品集合 模拟买家购买不同的量
        products = new ArrayList<>();
        products.add(new Product("Electricity1", 10.00));
        products.add(new Product("Electricity2", 9.00));
        products.add(new Product("Electricity3", 11.00));
        products.add(new Product("Electricity4", 12.00));
        products.add(new Product("Electricity5", 8.00));
        products.add(new Product("Electricity6", 7.00));

        System.out.println("MerchantAgent " + getAID().getName() + " is ready."); // 代理的本地名称
        addBehaviour(new PurchaserRequestHandler());
    }

    private class PurchaserRequestHandler extends CyclicBehaviour {
        public void action() {
            // 接收来自Buyer的消息
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt); // 接收消息
            if (msg != null) {
                // 处理请求
                ACLMessage reply = msg.createReply();
                Product product = getRandomProduct();
                if (product != null) {
                    double price = product.getPrice();
                    if (price <= Double.parseDouble(msg.getContent())) { // 如果商品价格小于等于买家出价
                        reply.setPerformative(ACLMessage.AGREE); // 同意交易
                        transactionCount++;
                        addRandomReputation(); // reputation增加一个随机数，随机数在0-1之间
                        // 将交易信息存入数据库
                        insertTransactionInfo(transactionCount, Double.parseDouble(msg.getContent()), reputation);
                    } else {
                        reply.setPerformative(ACLMessage.REFUSE);
                        reputation -= 0.1;
                    }
                    reply.setContent("Transaction completed. Transaction Size:" + transactionCount + ". Reputation:" + reputation);
                } else {
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("No products available.");
                }
                myAgent.send(reply);
            } else {
                System.out.println("MerchantAgent " + getAID().getName() + " is waiting for the buyer's request.");
                block();
            }
        }

        // 从商品集合中随机选择一个商品
        private Product getRandomProduct() {
            if (!products.isEmpty()) {
                Random rand = new Random();
                int index = rand.nextInt(products.size());
                return products.get(index);
            }
            return null;
        }
        // 为商家增加一个随机数的信誉值，模拟商家信誉值的变化
        public void addRandomReputation() {
            Random random = new Random();
            double randomValue = random.nextDouble(); // 生成0到1之间的随机数
            reputation += randomValue;
        }
        // 插入交易信息到数据库
        private void insertTransactionInfo(int transactionCount, double price, double reputation) {
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                // 连接数据库
                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                // 执行插入操作
                String sql = "INSERT INTO trans_info (sell_price, reputation,transaction_size) VALUES (?, ?, ?)"; // SQL语句
                stmt = conn.prepareStatement(sql); // 预编译SQL语句
                stmt.setDouble(1, price);
                stmt.setDouble(2, reputation);
                stmt.setInt(3, transactionCount);
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
    }
}
