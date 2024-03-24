package yhy;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.sql.*;


public class BlockChain extends Agent {

    private String current_date;

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
                }
            } else {

            }

        }
        @Override
        public boolean done() {
            return false;
        }

    }

    private class OrderRequest extends Behaviour {
        @Override
        public void action() {

        }

        @Override
        public boolean done() {
            return false;
        }
    }

    private class Dayahead extends Behaviour {
        @Override
        public void action() {

        }

        @Override
        public boolean done() {
            return false;
        }
    }

    private class initial extends OneShotBehaviour {
        @Override
        public void action() {

        }
    }

    private boolean isDayaheadMessage(ACLMessage msg) {
        String content = msg.getContent();
        return content != null && content.contains("Dayahed");
    }

    private boolean isOrderRequestMessage(ACLMessage msg) {
        String content = msg.getContent();
        return content != null && content.contains("OrderRequest");
    }



    // 清除当前Agent
    protected void takeDown() {
        System.out.println("BlockChain-agent "+getAID().getName()+" terminating.");
    }
}
