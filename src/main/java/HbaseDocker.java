/*import com.amihaiemil.docker.Container;
import com.amihaiemil.docker.Containers;
import com.amihaiemil.docker.Image;
import com.amihaiemil.docker.LocalDocker;*/
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import java.io.IOException;
/*import org.apache.hadoop.hbase.client.HBaseAdmin;

import javax.json.*;
import java.io.File;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;*/

public class HbaseDocker {

    private static String zookeeperParent = "/hbase";
    private static String zookeeperQuorum = "10.111.30.22";
    private static String zookeeperClientPort = "2181";

/*    public static void main(String[] args) throws IOException {
        Image img = null;
        String nameImage = "dajobe/hbase";
        String tag = "latest";
        try {
            img = new LocalDocker(
                    new File("/var/run/docker.sock")
            ).images().pull(nameImage, tag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (final Image parent : img.history()) {
            System.out.println(img);
        }


        LocalDocker docker = new LocalDocker(new File("/var/run/docker.sock"));

        Containers containers = docker.containers();
        Iterator<Container> itr = containers.iterator();
        while (itr.hasNext()) {
            Container container = itr.next();
            System.out.println(container.inspect());
        }

*//*        JsonObject jsonObject =
        //docker.containers().create()*//*

        Container container = docker.containers().create(nameImage);
*//*        JsonObject jsonObject = container.inspect();

        JsonObject hostConfig = jsonObject.getJsonObject("HostConfig");
        JsonValue jo = hostConfig.get("PortBindings");

        System.out.println(jo.toString());


        String pb = "{\"2181/tcp\": [ {\"HostIp\": \"\",\"HostPort\": \"2181\"}]}";
        JsonReader reader = Json.createReader(new StringReader(pb));
        JsonObject pbJson = reader.readObject();
        JsonValue portBindings = hostConfig.replace("PortBindings", pbJson);

        JsonObject jsonConfig = jsonObject.getJsonObject("Config");
        JsonValue exposedPorts = jsonConfig.get("ExposedPorts");

        System.out.println(exposedPorts.toString());

        JsonObject jsonNeyw = jsonObject.getJsonObject("NetworkSettings");
        JsonValue ntwPorts = jsonConfig.get("Ports");
        //System.out.println(ntwPorts.toString());
*//*

        System.out.println(container.inspect());
        container.start();
        System.out.println(container.inspect());
        //createHbaseTable();
        container.kill();
        container.remove();
    }
    */

    public static  void createHbaseTable() {
        final Configuration conf = HBaseConfiguration.create();
        conf.set("zookeeper.recovery.retry", Integer.toString(2));
        conf.set(HConstants.ZK_SESSION_TIMEOUT, Integer.toString(60000));
        conf.set(HConstants.ZOOKEEPER_ZNODE_PARENT, zookeeperParent);
        conf.set(HConstants.ZOOKEEPER_QUORUM, zookeeperQuorum);
        conf.set(HConstants.ZOOKEEPER_CLIENT_PORT, zookeeperClientPort);
        conf.set(HConstants.ZOOKEEPER_RECOVERABLE_WAITTIME, Integer.toString(60000));

        conf.set(HConstants.HBASE_CLIENT_RETRIES_NUMBER, Integer.toString(3));
        conf.set(HConstants.HBASE_CLIENT_PAUSE, Integer.toString(6000));
        //TODO FIX SHOULD BE ~60000 - CONNECTION POOL WITH ON RELEASE FUNCTIONALITY
        conf.set(HConstants.HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, Integer.toString(1800000));
        //TODO FIX SHOULD BE ~60000 - CONNECTION POOL WITH ON RELEASE FUNCTIONALITY
        conf.set(HConstants.HBASE_RPC_TIMEOUT_KEY, Integer.toString(1800000));

        Connection hbaseCon = null;
        try {
            hbaseCon = ConnectionFactory.createConnection(conf);
            Admin admin =  hbaseCon.getAdmin();

            // Instantiating table descriptor class
            HTableDescriptor tableDescriptor = new
                    HTableDescriptor(TableName.valueOf("emp"));

            // Adding column families to table descriptor
            tableDescriptor.addFamily(new HColumnDescriptor("personal"));
            tableDescriptor.addFamily(new HColumnDescriptor("professional"));

            // Execute the table through admin
            System.out.println("Creating hbase table " + tableDescriptor.getTableName());
            admin.createTable(tableDescriptor);
            System.out.println(" Table created ");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
