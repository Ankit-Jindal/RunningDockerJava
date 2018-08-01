package dada;

/*import com.amihaiemil.docker.Container;
import com.amihaiemil.docker.Containers;
import com.amihaiemil.docker.Image;
import com.amihaiemil.docker.LocalDocker;*/
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
    private static String zookeeperQuorum = "127.0.0.1";
    private static String zookeeperClientPort = "2181";

public static void main(String[] args) throws IOException {
    try {
        System.out.println();
        createHbaseTable1();
       insertData();
    } catch (Exception e) {
        e.printStackTrace();
    }

}
    private static void  createHbaseTable1() throws IOException {

        // Instantiating configuration class
        Configuration con = HBaseConfiguration.create(defaultConfig());

        // Instantiating HbaseAdmin class
        HBaseAdmin admin = new HBaseAdmin(con);

        // Instantiating table descriptor class
        HTableDescriptor tableDescriptor = new
                HTableDescriptor(TableName.valueOf("emp212"));

        // Adding column families to table descriptor
        tableDescriptor.addFamily(new HColumnDescriptor("personal"));
        tableDescriptor.addFamily(new HColumnDescriptor("professional"));

        // Execute the table through admin
        admin.createTable(tableDescriptor);
        System.out.println(" Table created ");

    }

private static void insertData() throws IOException {
    // Instantiating Configuration class
    Configuration config = HBaseConfiguration.create(defaultConfig());
    // Instantiating HTable class
    HTable hTable = new HTable(config, "emp212");
    // Instantiating Put class
    // accepts a row name.

    Put p = new Put(Bytes.toBytes("row1"));

    // adding values using add() method
    // accepts column family name, qualifier/row name ,value
    p.add(Bytes.toBytes("personal"),
            Bytes.toBytes("name"),Bytes.toBytes("raju"));

    p.add(Bytes.toBytes("personal"),
            Bytes.toBytes("city"),Bytes.toBytes("hyderabad"));

    p.add(Bytes.toBytes("professional"),Bytes.toBytes("designation"),
            Bytes.toBytes("manager"));

    p.add(Bytes.toBytes("professional"),Bytes.toBytes("salary"),
            Bytes.toBytes("50000"));

    // Saving the put Instance to the HTable.
    hTable.put(p);
    System.out.println("data inserted");

    // closing HTable
    hTable.close();
}

    public static  void createHbaseTable() throws InterruptedException {
        final Configuration conf = defaultConfig();
        Connection hbaseCon = null;
        try {
            hbaseCon = ConnectionFactory.createConnection();
            System.out.println(hbaseCon.getConfiguration().get("hbase.regionserver.port"));
            System.out.println(hbaseCon.getConfiguration().get("hbase.rootdir"));
            System.out.println(hbaseCon.getConfiguration().get("hbase.zookeeper.property.dataDir"));
            System.out.println(hbaseCon.getConfiguration().get("hbase.master.port"));
            Admin admin =  hbaseCon.getAdmin();

            // Instantiating table descriptor class
            HTableDescriptor tableDescriptor = new
                    HTableDescriptor(TableName.valueOf("EMPLOYEE"));

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


    private static Configuration defaultConfig(){
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
        conf.set(HConstants.HBASE_CLIENT_RETRIES_NUMBER,  Integer.toString(4));
        return conf;
    }



    /**
     * @param table
     * @param hbaseRecord
     * @return
     */
    public static boolean putDataInTable(Connection hbaseConn, String table, HbaseRecord hbaseRecord) throws IOException {

        boolean isSuccess = true;
        try {
            Put put = createPut(hbaseRecord);
            Table htable = hbaseConn.getTable(TableName.valueOf(table));
            htable.put(put);
            Thread.sleep(3000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }


    private static Put createPut(HbaseRecord hbaseRecord) {
        Put put = new Put(Bytes.toBytes(hbaseRecord.getRowKey()));
        List<ColumnValueDetails> columnValues = hbaseRecord.getColumnValueDetails();
        for (ColumnValueDetails columnValue : columnValues) {
            ColumnDetails columnDetail = columnValue.getColumnDetails();
            put.add(Bytes.toBytes(columnDetail.getColFamily()), Bytes.toBytes(columnDetail.getColQualifier()),
                    Bytes.toBytes(columnValue.getColumnValue()));
        }
        return put;
    }

    /**
     * @param rowKey
     * @param columnfamily
     * @param columnQualifier
     * @param value
     * @return
     */
    public static HbaseRecord prepareHbaseRecodToInsert(String rowKey, String columnfamily, String columnQualifier, String value) {
        ColumnDetails columnDetails = new ColumnDetails();
        columnDetails.setColFamily(columnfamily);
        columnDetails.setColQualifier(columnQualifier);
        ColumnValueDetails columnValueDetails = new ColumnValueDetails();
        columnValueDetails.setColumnDetails(columnDetails);
        columnValueDetails.setColumnValue(value);
        HbaseRecord hbaseRecord = new HbaseRecord();
        hbaseRecord.setColumnValueDetails(Arrays.asList(columnValueDetails));
        hbaseRecord.setRowKey(rowKey);
        return hbaseRecord;
    }

}
