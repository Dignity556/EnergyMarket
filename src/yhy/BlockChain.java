package yhy;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BlockChain extends Agent {
    private Map<Integer, double[]> merchant_info = new HashMap<>(); // 卖家列表 ID 信誉值 单价
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";


    // BlockChain执行框架
    protected void setup() {
        // Printout a welcome message
        System.out.println("Hallo! BlockChain-agent "+getAID().getName()+" is ready.");
        double price = 200;
        // 初始化卖家列表
        merchant_info.put(1, new double[]{14.0, price});
        merchant_info.put(2, new double[]{13.0, price});
        merchant_info.put(3, new double[]{12.0, price});
        merchant_info.put(4, new double[]{11.0, price});
        merchant_info.put(5, new double[]{10.0, price});
        merchant_info.put(6, new double[]{9.0, price});
        merchant_info.put(7, new double[]{8.0, price});
        merchant_info.put(8, new double[]{7.0, price});
        merchant_info.put(9, new double[]{6.0, price});
        merchant_info.put(10, new double[]{5.0, price});
        // Dayahead —— Realtime —— Reputation
        addBehaviour(new OrderRequest());
        addBehaviour(new Dayahead());

    }

    // 处理买家的订单请求，综合信誉值地理位置等因素，选定卖家进行交易
    private class OrderRequest extends CyclicBehaviour {
        private ACLMessage informMessage;
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                System.out.println("2：BlockChain：收到查询卖家请求。");
                informMessage = new ACLMessage(ACLMessage.INFORM);
                informMessage.addReceiver(new AID("MerchantAgentX", AID.ISLOCALNAME));
                informMessage.setContent("BuyerID_Volume");
                myAgent.send(informMessage);
                System.out.println("3：BlockChain：发送Buyer-Merchant订单信息给Merchant");
            } else {
                block();
            }
        }
    }


    // 运行信誉值更新算法，以及dayahead算法，修改数据库
    private class Dayahead extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                System.out.println("7：BlockChain：更新信誉值，并计算dayahead定价");
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.AGREE);
                // 对所有卖家发送信息，更新他们的dayahead
                reply.addReceiver(new AID("MerchantAgentX", AID.ISLOCALNAME));
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }

    // 清除当前Agent
    protected void takeDown() {
        System.out.println("BlockChain-agent "+getAID().getName()+" terminating.");
    }
}
