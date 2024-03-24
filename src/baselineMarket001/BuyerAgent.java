package baselineMarket001;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.text.DecimalFormat;
import java.util.Random;

public class BuyerAgent extends Agent {
    private int requestCount = 0; // 记录发送购买请求的次数
    private String[] ids; // 用于表示不同商家

    protected void setup() {
        System.out.println("BuyerAgent" + getAID().getName() + " is ready."); // 打印出买家Agent的名字
        addBehaviour(new sendPurchaseRequest()); // 发送购买请求
        addBehaviour(new PurchaseResponseHandler());
    }

    private class sendPurchaseRequest extends CyclicBehaviour {
        public void action() {
            if (requestCount < 100) {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(getAID("MerchantAgent"));
                // 生成一个随机价格，模拟买家出价
                double price = generateRandomNumber(5.00, 15.00); // 在规定范围内生成一个随机价格
                String priceString = formatDecimal(price); // 将随机数转换为字符串并保留两位小数
                msg.setContent(priceString);
                send(msg);
                System.out.println("BuyerAgent" + getAID().getName() + " sent a request to MerchantAgent. (Count: " + (requestCount + 1) + ")");
                requestCount++;
            } else {
                // 发送完100次购买请求后停止
                System.out.println("BuyerAgent" + getAID().getName() + " has sent 100 purchase requests. Stopping...");
                doDelete(); // 停止Agent
            }
        }
    }

    private class PurchaseResponseHandler extends CyclicBehaviour {
        public void action() {
            MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.AGREE); // 匹配消息模板
            ACLMessage msg = myAgent.receive(template); // 接收消息
            if (msg != null) {
                System.out.println("Purchase successful: " + msg.getContent());
            } else {
                System.out.println("Purchase failed.");
                block();
            }
        }
    }

    /*下面是一些辅助方法，用于生成随机数和格式化小数。*/
    // 生成指定范围内的随机数（带两位小数）
    private static double generateRandomNumber(double min, double max) {
        Random rand = new Random();
        return min + (max - min) * rand.nextDouble();
    }
    // 格式化保留两位小数
    private static String formatDecimal(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(number);
    }
}
