package yhy;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Date;

public class Merchant extends Agent {

    private AID buyerAgent;
    private String ID; // 卖家ID
    private int date = 0; // 当前日期
    private int violation = 0; // 违规系数
    private double dayahead; // dayahead价格
    private double cost; // 商家成本
    private double stock_size = 1000; // 商家存量，初始为1000 （个人一个月200度）
    private int average_price = 0; // 平均单笔成交价格
    private int transaction_count = 0; // 历史成交数量
    private int yesterday_count = 0; // 前一天成交数量
    private double reputation = 0; // 商家信誉值


    protected void setup() {
//        initial(); // 如果需要从已有市场中继续交易，则进行初始化方法
        // 打印欢迎信息
        ID = getLocalName();
        System.out.println(ID + " is ready.");

        // 初始化信誉值
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            reputation = (Double) args[0];
        }
        addBehaviour(new PurchaserRequestHandler());
        addBehaviour(new DayaheadUpdateHandler());
    }

    // 处理订单
    private class PurchaserRequestHandler extends CyclicBehaviour {
        private ACLMessage acceptMessage;
        public void action() {
            System.out.println();
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {


                System.out.println("4：Merchant1：收到订单请求，处理交易，发送订单完成消息给买家。");
                buyerAgent = new AID("BuyerAgent", AID.ISLOCALNAME);
                acceptMessage = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                acceptMessage.addReceiver(buyerAgent);
                myAgent.send(acceptMessage);
            } else {
                block();
            }
        }
    }

    // 更新dayahead操作
    private class DayaheadUpdateHandler extends CyclicBehaviour {
        private ACLMessage acceptMessage;
        public void action() {
            System.out.println();
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
            ACLMessage reply = myAgent.receive(mt);
            if (reply != null) {
                // 发送更新完成通知

            } else {
                block();
            }
        }
    }
}
