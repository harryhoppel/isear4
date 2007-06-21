import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 14.04.2007
 * Time: 16:07:48
 * To change this template use File | Settings | File Templates.
 */
public class InterfaceDiscoverer {
    public static void main(String[] args) {
        try {
            Enumeration<NetworkInterface> interfaces =  NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface nextNetworkInterface = interfaces.nextElement();
                System.out.println("nextNetworkInterface.getDisplayName() = " + nextNetworkInterface.getName());
            }
        } catch (SocketException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
