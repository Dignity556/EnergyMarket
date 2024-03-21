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

            // 创建 BuyerAgent
            AgentController buyerAgent = container.createNewAgent("BuyerAgent", BuyerAgent.class.getName(), null);
            // 创建 MerchantAgent
            AgentController merchantAgent = container.createNewAgent("MerchantAgent", MerchantAgent.class.getName(), null);
            buyerAgent.start();
            merchantAgent.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
