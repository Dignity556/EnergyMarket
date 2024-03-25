package yhy;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.sql.*;


public class BlockChain extends Agent {

    private String current_date;
    private AID[] sellerAgents; // Seller列表，用于推荐给Buyer
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";

    protected void setup() {
        // Printout a welcome message
        System.out.println("Hallo! BlockChain-agent "+getAID().getName()+" is ready.");
        // 系统开始时实现一次Dayahed定价
        addBehaviour(new initial());
        // Dayahead —— Realtime —— Reputation
        addBehaviour(new MessageHandler());
    }

    // 等待来自Buyer的信息，根据Buyer消息的类型进行不同的行为
    private class MessageHandler extends Behaviour {
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                switch (msg.getPerformative()) {
                    case ACLMessage.CFP:
                        if (isDayaheadMessage(msg)) {
                            addBehaviour(new Dayahead());
                        }
                        break;
                    case ACLMessage.REQUEST:
                        if (isOrderRequestMessage(msg)) {
                            addBehaviour(new OrderRequest());
                        }
                        break;
                }
            } else {
                block();
            }
        }
        @Override
        public boolean done() {
            return false;
        }

        // 判断 Message 是否为 Dayahead
        private boolean isDayaheadMessage(ACLMessage msg) {
            String content = msg.getContent();
            return content != null && content.contains("Dayahed");
        }

        // 判断 Message 是否为 OrderRequest
        private boolean isOrderRequestMessage(ACLMessage msg) {
            String content = msg.getContent();
            return content != null && content.contains("OrderRequest");
        }
    }


    // 运行信誉值更新算法，以及dayahead算法，修改数据库
    private class Dayahead extends OneShotBehaviour {
        @Override
        public void action() {

        }
    }


    // 处理买家的订单请求，综合信誉值地理位置等因素，收集Seller的名单传递给Buyer
    private class OrderRequest extends OneShotBehaviour {
        @Override
        public void action() {
            System.out.println("收到查询卖家请求，正在查询数据库。。。。。。");
            System.out.println("发送Seller名单");
        }
    }


    // 市场刚开始时，为商家初始化dayahead定价
    private class initial extends OneShotBehaviour {
        @Override
        public void action() {
            System.out.println("初始化Dayahead定价");
        }
    }


    // 清除当前Agent
    protected void takeDown() {
        System.out.println("BlockChain-agent "+getAID().getName()+" terminating.");
    }
}
