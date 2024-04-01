package yhy;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Buyer extends Agent {
    Random rand = new Random();
    private int orderNum = 5; // 记录当天的订单数量，当天订单数量完成，向BlockChainAgent发送第二天信息
    private int orderVolume; // 单笔订单的所需的能源交易量
    private int ID; // 当前买家ID
    private int step = 0;
    private AID blockChainAgent = new AID("BlockChainAgent", AID.ISLOCALNAME);
    private ACLMessage askMessage; // 请求交易名单的信息
    private ACLMessage acceptMessage; // 买家交易完成发送来的信息
    private ACLMessage cfpMessage; // 发送第二天定价的信息
    private ACLMessage agreeMessage; // 接收进行第二天交易的信息


    protected void setup() {
        // Printout a welcome message
        System.out.println("BuyerAgent" + getAID().getName() + " is ready.");
        addBehaviour(new TickerBehaviour(this, 10000) {
            protected void onTick() {
                // 购买过程
                myAgent.addBehaviour(new RequestPerformer());
            }
        } );
    }


    private class RequestPerformer extends Behaviour {
        public void action() {
            switch (step) {
                // 发送购买请求订单
                case 0:
                    // 随机该笔订单需求，买家ID
                    ID = rand.nextInt(10) + 1;
                    orderVolume = rand.nextInt(300) + 200;
                    // 发送请求订单信息
                    askMessage = new ACLMessage(ACLMessage.CFP);
                    askMessage.addReceiver(blockChainAgent);
                    askMessage.addUserDefinedParameter("iD", Integer.toString(ID));
                    askMessage.addUserDefinedParameter("orderVolume", Integer.toString(orderVolume));
                    // 地理坐标，用来根据距离推荐
//                    askMessage.addUserDefinedParameter("coordinate", Integer.toString(coordinate));
                    myAgent.send(askMessage);
                    step = 1;
                    break;
                case 1:
                    acceptMessage = myAgent.receive();
                    if (acceptMessage != null && acceptMessage.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        System.out.println("5：Buyer：订单完成，进行下一笔交易");
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        orderNum -= 1;
                        step = 0;
                    }
                    break;
                case 2:
                    // 日期更新，进行Dayahead定价算法
                    cfpMessage = new ACLMessage(ACLMessage.INFORM);
                    cfpMessage.addReceiver(blockChainAgent);
                    cfpMessage.setContent("Dayahead");
                    myAgent.send(cfpMessage);
                    System.out.println("6：Buyer：发送第二天交易请求。");
                    step = 3;
                case 3:
                    agreeMessage = myAgent.receive();
                    if (agreeMessage != null && agreeMessage.getPerformative() == ACLMessage.AGREE) {
                        System.out.println("8：Buyer：更新完成，可以进行第二天的交易。----------------------------------------------");
                        try {
                            Thread.sleep(50000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        step = 0;
                    }
                    break;
            }
        }

        // action方法执行结束后，自动执行done方法，返回false继续调用action
        public boolean done() {
            // 已完成当天订单的交易量
            if (orderNum == 0) {
                System.out.println("7：Buyer：当天交易结束。");

                // 生成第二天订单的交易笔数
                orderNum = rand.nextInt(40) + 10;
                step = 2;
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return false;
        }
    }
}
