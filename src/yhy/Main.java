package yhy;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

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
            Object[] merchant = getObjects();
            AgentController blockChainAgent = container.createNewAgent("BlockChainAgent", BlockChain.class.getName(), merchant);
            // 创建 BuyerAgent
            AgentController buyerAgent = container.createNewAgent("BuyerAgent", Buyer.class.getName(), null);

            blockChainAgent.start();
            buyerAgent.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object[] getObjects() {
        Seller seller1 = new Seller(1, 14.0);
        Seller seller2 = new Seller(2, 13.0);
        Seller seller3 = new Seller(3, 12.0);
        Seller seller4 = new Seller(4, 11.0);
        Seller seller5 = new Seller(5, 10.0);
        Seller seller6 = new Seller(6, 9.0);
        Seller seller7 = new Seller(7, 8.0);
        Seller seller8 = new Seller(8, 7.0);
        Seller seller9 = new Seller(9, 6.0);
        Seller seller10 = new Seller(10, 5.0);

        // 创建 Merchant
        Object[] merchant = {seller1, seller2, seller3, seller4, seller5, seller6, seller7, seller8, seller9, seller10};
        return merchant;
    }
}
