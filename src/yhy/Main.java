package yhy;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // 创建JADE运行时环境
            Runtime runtime = Runtime.instance();

            // 创建一个默认的Profile，并指定主机名和端口
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            profile.setParameter(Profile.MAIN_PORT, "1099");
            // 创建Agent容器
            AgentContainer container = runtime.createMainContainer(profile);

            // 创建 BlockChainAgent
            Object[] merchant = {getSellers()};
            AgentController blockChainAgent = container.createNewAgent("BlockChainAgent", BlockChain.class.getName(), merchant);
            // 创建 BuyerAgent
            AgentController buyerAgent = container.createNewAgent("BuyerAgent", Buyer.class.getName(), null);

            blockChainAgent.start();
            buyerAgent.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Seller> getSellers() {
        List<Seller> sellers = new ArrayList<>();
        sellers.add(new Seller(1, 14.0));
        sellers.add(new Seller(2, 13.0));
        sellers.add(new Seller(3, 12.0));
        sellers.add(new Seller(4, 11.0));
        sellers.add(new Seller(5, 10.0));
        sellers.add(new Seller(6, 9.0));
        sellers.add(new Seller(7, 8.0));
        sellers.add(new Seller(8, 7.0));
        sellers.add(new Seller(9, 6.0));
        sellers.add(new Seller(10, 5.0));
        return sellers;
    }
}
