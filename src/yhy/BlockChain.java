package yhy;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;


public class BlockChain extends Agent {


    private List<Seller> sellers = new ArrayList<>();
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";


    // BlockChain执行框架
    protected void setup() {
        // Printout a welcome message
        System.out.println("Hallo! BlockChain-agent "+getAID().getName()+" is ready.");
        double price = 200;

        // 得到所有卖家的基本信息
        Object[] args = getArguments();
        sellers = (List<Seller>) args[0];

        // Dayahead —— Realtime —— Reputation
        addBehaviour(new OrderRequest());
        addBehaviour(new Dayahead());

    }

    // 处理买家的订单请求，综合信誉值地理位置等因素，选定卖家进行交易
    private class OrderRequest extends CyclicBehaviour {
        private int buyerID;
        private int orderVolume;
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                ACLMessage reply = msg.createReply();
                if (OrderProcess(msg)) {
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                } else {
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                }
                myAgent.send(reply);
                System.out.println("3：BlockChain：完成交易");
            } else {
                block();
            }
        }

        private boolean OrderProcess(ACLMessage msg) {
            // 按信誉值排序
            sellers.sort(Comparator.comparingDouble(Seller::getReputation).reversed());
            // 调用该seller的realtime方法，记录交易
            sellers.get(pick_one_seller()).orderProcess(msg.getUserDefinedParameter("iD"), msg.getUserDefinedParameter("orderVolume"));
            return true;
        }
        // 选定交易卖家
        private int pick_one_seller() {
            // 选出前三个卖家 0 1 2
            int[] nums = new int[5];
            nums[0] = 0;
            nums[1] = 1;
            nums[2] = 2;
            // 随机选后两个
            Random random = new Random();
            int fourth = random.nextInt(7) + 3;
            int fiveth;
            do {
                fiveth = random.nextInt(7) + 3;
            } while (fiveth == fourth);
            nums[3] = fourth;
            nums[4] = fiveth;
            // 随机选择一个数字返回
            return nums[random.nextInt(5)];
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
                double[] vl = {0.75, 0.5, 0.25, 0};
                // 遍历所有卖家，调用dayahead方法
                Random random = new Random();
                for (Seller seller : sellers) {
                    seller.dayAhead(vl[random.nextInt(4)]); // 随机违规系数
                }
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.AGREE);
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
