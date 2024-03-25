package yhy;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import javax.swing.*;
import java.util.Random;


public class Buyer extends Agent {
    private int orderNum; // 记录当天的订单数量，当天订单数量完成，向BlockChainAgent发送第二天信息

    protected void setup() {
        // Printout a welcome message
        System.out.println("BuyerAgent" + getAID().getName() + " is ready.");

        addBehaviour(new TickerBehaviour(this, 10000) {
            protected void onTick() {
                // 发送
                myAgent.addBehaviour(new RequestPerformer());
            }
        } );


    }

    private class RequestPerformer extends Behaviour {
        private AID blockChainAgent;
        private ACLMessage cfpMessage;
        private ACLMessage askMessage;
        private AID sellerAgent;
        private int energyAmount = 0;
        private int step = 0;
        public void action() {
            switch (step) {
                // 发送购买请求订单
                case 0:
                    // 生成交易笔数
                    randAmount(40, 10);
                    blockChainAgent = new AID("BlockChainAgent", AID.ISLOCALNAME);
                    askMessage = new ACLMessage(ACLMessage.REQUEST);
                    askMessage.addReceiver(blockChainAgent);
                    askMessage.setContent("OrderRequest");
                    myAgent.send(askMessage);
                    step = 1;
                    break;
                case 1:
                    // 收集从BlockChainAgent发出的Seller名单
                    System.out.println("收到Seller名单");

                    // 这里若是需要SellerAgent确认，需要在前面收集所有在DF上注册为seller服务的Agent列表，并向指定Seller购买请求
                    System.out.println("选定商家并修改数据库，完成交易");
                    energyAmount -= 1;
                    step = 0;
                    break;
                case 2:
                    // 日期更新，进行Dayahead定价算法
                    blockChainAgent = new AID("BlockChainAgent", AID.ISLOCALNAME);
                    step = 0;
            }
        }

        // action方法执行结束后，自动执行done方法，返回false继续调用action
        public boolean done() {
            // 已完成当天订单的交易量
            if (energyAmount == 0) {
                step = 2;
            }
            return false;
        }

        // 随机生成当天交易笔数
        public void randAmount(int rangeLeft, int rangeRight) {
            Random rand = new Random();
            energyAmount = rand.nextInt(rangeLeft) + rangeRight - rangeLeft;
        }
    }
}

