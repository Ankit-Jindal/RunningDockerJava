import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DockerJavaApit {
    public static void main(String[] args) {
        String nameImage = "alekseysavitskiy/hbase-dev"; //"docker-hbase-standalone:1.2.6";//"alekseysavitskiy/hbase";//"dajobe/hbase";

        //String contId = dockerWithDockerFile();
        String contId = startContainerWithImageName(nameImage);
        //startHbaseContaner();
       // HbaseDocker.createHbaseTable();
       // killAndRemoveContainer(contId);
//40809,35179


    }

    private static String dockerWithDockerFile() {
        //String dockerDir = "/Users/ankitkumar/Documents/dockerTest/hbase.docker";  //docker-hbase-standalone
       // String dockerDir = "/Users/ankitkumar/Documents/dockerTest/standalone-hbase";
        String dockerDir = "/Users/ankitkumar/Documents/dockerTest/harisekhon-habse";
       // String dockerDir = "/Users/ankitkumar/Documents/dockerTest/docker-hbase-standalone";
        //String dockerDir = "/Users/ankitkumar/Documents/dockerTest/docker-pinpoint/pinpoint-hbase";

        //2181:2181 -p 60000:60000 -p 60010:60010 -p 60020:60020 -p 60030:60030


        ExposedPort expzoo = ExposedPort.tcp(2181);
        Ports.Binding hoszoo = Ports.Binding.bindPort(2181);

        ExposedPort exphbase60000 = ExposedPort.tcp(60000);
        Ports.Binding pohbase60000 = Ports.Binding.bindPort(60000);

        ExposedPort exphbase16010 = ExposedPort.tcp(16010);
        Ports.Binding pohbase16010 = Ports.Binding.bindPort(16010);

        ExposedPort exphbase60020 = ExposedPort.tcp(60020);
        Ports.Binding pohbase60020 = Ports.Binding.bindPort(60020);

        ExposedPort exphbase60030 = ExposedPort.tcp(60030);
        Ports.Binding pohbase60030 = Ports.Binding.bindPort(60030);


        List<ExposedPort> exposedPorts = new ArrayList<ExposedPort>();
        Ports ports = new Ports();

        exposedPorts.add(expzoo);
        exposedPorts.add(exphbase60000);
        exposedPorts.add(exphbase16010);
        exposedPorts.add(exphbase60020);
        exposedPorts.add(exphbase60030);


        PortBinding portBindingZoo = new PortBinding(hoszoo, expzoo);
        PortBinding portBindingHbase00 = new PortBinding(pohbase60000, exphbase60000);
        PortBinding portBindingHbase10 = new PortBinding(pohbase16010, exphbase16010);
        PortBinding portBindingHbase20 = new PortBinding(pohbase60020, exphbase60020);
        PortBinding portBindingHbase30 = new PortBinding(pohbase60030, exphbase60030);



        ports.add(portBindingZoo, portBindingHbase00, portBindingHbase10, portBindingHbase20, portBindingHbase30);


        DockerClientConfig dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .build();

        DockerClient dockerClient = DockerClientBuilder.getInstance(dockerConfig).build();

        BuildImageCmd buildImageCmd = dockerClient.buildImageCmd().withDockerfile(new File(dockerDir + "/Dockerfile"));
        BuildImageResultCallback callback = buildImageCmd.exec(new BuildImageResultCallback());

        System.out.println("Building image from Docker File ...");
        String imageId = callback.awaitImageId();

        System.out.println("Build succesfull with imageID : " + imageId);

        CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(imageId)
                .withName("hbase-standalone")
                .withHostName("localhost")
                //.withNetworkMode("host")
                .withPublishAllPorts(true)
                .withExposedPorts(exposedPorts)
                .withPortBindings(ports);


        CreateContainerResponse createContainerResponse = createContainerCmd.exec();
        String contId = createContainerResponse.getId();
        StartContainerCmd startContainerCmd = dockerClient.startContainerCmd(contId);
        startContainerCmd.exec();

/*        dockerClient.*/
       // dockerClient.execCreateCmd(contId).withCmd("").

        InspectContainerResponse resp = dockerClient.inspectContainerCmd(contId).exec();

        String status = resp.getState().getStatus();
        System.out.println("Image : " + imageId + " : contID : " + contId + " : Status " + status + "");

        return contId;

    }


    private static String startContainerWithImageName(String nameImage) {
        DockerClientConfig dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .build();


        ExposedPort expzoo = ExposedPort.tcp(2181);
        Ports.Binding hoszoo = Ports.Binding.bindPort(2181);

        ExposedPort exphbase = ExposedPort.tcp(16010);
        Ports.Binding pohbase = Ports.Binding.bindPort(16010);

        ExposedPort exphbase16000 = ExposedPort.tcp(16000);
        Ports.Binding pohbase16000 = Ports.Binding.bindPort(16000);

        ExposedPort exphbase16201 = ExposedPort.tcp(16201);
        Ports.Binding pohbase16201 = Ports.Binding.bindPort(16201);

        ExposedPort exphbase16301 = ExposedPort.tcp(16301);
        Ports.Binding pohbase16301 = Ports.Binding.bindPort(16301);

        PortBinding portBindingZoo = new PortBinding(hoszoo, expzoo);
        PortBinding portBindingHbase = new PortBinding(pohbase, exphbase);
        PortBinding portBinfHB6000 = new PortBinding(pohbase16000, exphbase16000);
        PortBinding portBinfHB16201 = new PortBinding(pohbase16201, exphbase16201);
        PortBinding portBinfHB16301 = new PortBinding(pohbase16301, exphbase16301);


        Ports ports = new Ports();

        ports.add(portBindingHbase, portBindingZoo,portBinfHB6000 ,portBinfHB16201 ,portBinfHB16301);

        DockerClient dockerClient = DockerClientBuilder.getInstance(dockerConfig).build();

        System.out.println("Docker Client built");

        PullImageCmd imgCmd = dockerClient.pullImageCmd(nameImage);//.withTag("latest");

        PullImageResultCallback callback = imgCmd.exec(new PullImageResultCallback());

        callback.awaitSuccess();


        CreateContainerResponse response = dockerClient.createContainerCmd(nameImage)
                .withPortBindings(ports)
                .withHostName("localhost")
                .withExposedPorts(expzoo, exphbase, exphbase16000, exphbase16201,exphbase16301 )
                .exec();


        String contId = response.getId();
        String[] warnings = response.getWarnings();


        dockerClient.startContainerCmd(contId).exec();


        InspectContainerResponse insResp = dockerClient.inspectContainerCmd(contId).exec();
        String status = insResp.getState().getStatus();

        System.out.println("Image : " + nameImage + " : contID : " + contId + " : Status " + status + "");

        return contId;

    }

    private static void startHbaseContaner(){

        DockerClientConfig dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .build();
        DockerClient dockerClient = DockerClientBuilder.getInstance(dockerConfig).build();
        System.out.println("Docker Client built");

        //zookeeper:3.4.10
        String zooImage = "zookeeper:3.4.10";
        ExposedPort expPort2181 = ExposedPort.tcp(2181);
        Ports.Binding bindPOrt2181 = Ports.Binding.bindPort(2181);
        PortBinding zooPb = new PortBinding(bindPOrt2181,expPort2181);
        Ports portszoo = new Ports();
        portszoo.add(zooPb);

        List<String> listzooEnv = new ArrayList<String>();
        listzooEnv.add("ZOO_MY_ID: 1");
        listzooEnv.add("ZOO_PORT: 2181");
        listzooEnv.add("ZOO_SERVERS: server.1=zoo1:2888:3888");
        listzooEnv.add("ZOO_MAX_CLIENT_CNXNS: 0");


        PullImageCmd zooImgCmd = dockerClient.pullImageCmd(zooImage);
        PullImageResultCallback callback = zooImgCmd.exec(new PullImageResultCallback());
        callback.awaitSuccess();
        CreateContainerResponse response = dockerClient.createContainerCmd(zooImage)
                .withEnv()
                .withPortBindings(portszoo)
                .withName("zookeeper")
                .withHostName("zoo1")
                .withPortBindings(portszoo)
                .withExposedPorts(expPort2181)
                .exec();
        String contId = response.getId();
        String[] warnings = response.getWarnings();
        dockerClient.startContainerCmd(contId).exec();
        InspectContainerResponse insResp = dockerClient.inspectContainerCmd(contId).exec();
        String status = insResp.getState().getStatus();
        System.out.println( " contID : " + contId + " : Status " + status + "");



        //alekseysavitskiy/hbase
/*        - 16010:16010
                - 16000:16000*/
        String hbaseImage = "alekseysavitskiy/hbase";
        ExposedPort expPort16010 = ExposedPort.tcp(16010);
        Ports.Binding bindPrt16010 = Ports.Binding.bindPort(16010);
        PortBinding hbasePB16010 = new PortBinding(bindPrt16010,expPort16010);

        ExposedPort expPort16000 = ExposedPort.tcp(16000);
        Ports.Binding bindPrt16000 = Ports.Binding.bindPort(16000);
        PortBinding hbasePB16000 = new PortBinding(bindPrt16000,expPort16000);

        portszoo = new Ports();
        portszoo.add(hbasePB16010,hbasePB16000);


        PullImageCmd hbaseImgCmd = dockerClient.pullImageCmd(hbaseImage);
        PullImageResultCallback callbackhbase = hbaseImgCmd.exec(new PullImageResultCallback());
        callbackhbase.awaitSuccess();


/*        "ZK_QUORUM=localhost"
        "HBASE_ROOTDIR=file:///home/hbase"
        "HBASE_CLUSTER_DISTRIBUTED=true"*/
        CreateContainerResponse responsehbase = dockerClient.createContainerCmd(hbaseImage)
                .withPortBindings(portszoo)
                .withName("hbase-master")
                .withHostName("hbase-master")
                .withCmd("/usr/local/hbase-1.4.4/bin/hbase master start ")
                .withPortBindings(portszoo)
                .withExposedPorts(expPort16000,expPort16010)
                .withEnv("ZK_QUORUM=zoo1","HBASE_ROOTDIR=file:///home/hbase","HBASE_CLUSTER_DISTRIBUTED=true")
                .withVolumes(new Volume("./data/hbase/data:/home/hbase"))
                .exec();
        contId = responsehbase.getId();
        warnings = response.getWarnings();


        dockerClient.startContainerCmd(contId).exec();
        insResp = dockerClient.inspectContainerCmd(contId).exec();
        status = insResp.getState().getStatus();
        System.out.println( " contID : " + contId + " : Status " + status + "");


        // alekseysavitskiy/hbase
/*        - 16020:16020
                - 16030:16030*/

        ExposedPort expPort16020 = ExposedPort.tcp(16020);
        Ports.Binding bindPrt16020 = Ports.Binding.bindPort(16020);
        PortBinding hbasePB16020 = new PortBinding(bindPrt16020,expPort16020);

        ExposedPort expPort16030 = ExposedPort.tcp(16030);
        Ports.Binding bindPrt16030 = Ports.Binding.bindPort(16030);
        PortBinding hbasePB16030 = new PortBinding(bindPrt16030,expPort16030);

        portszoo = new Ports();
        portszoo.add(hbasePB16020,hbasePB16030);

        zooImgCmd = dockerClient.pullImageCmd(hbaseImage);
        callback = zooImgCmd.exec(new PullImageResultCallback());
        callback.awaitSuccess();


        response = dockerClient.createContainerCmd(hbaseImage)
                .withPortBindings(portszoo)
                //.withHostName("localhost")
                .withExposedPorts(expPort16030,expPort16020)
                .withCmd("hbase regionserver start")
                .withEnv("ZK_QUORUM=zoo1","HBASE_ROOTDIR=file:///home/hbase","HBASE_CLUSTER_DISTRIBUTED=true")
                .withVolumes(new Volume("./data/hbase/data:/home/hbase"))
                .exec();
        contId = response.getId();
        warnings = response.getWarnings();
        dockerClient.startContainerCmd(contId).exec();
        insResp = dockerClient.inspectContainerCmd(contId).exec();
        status = insResp.getState().getStatus();
        System.out.println( " contID : " + contId + " : Status " + status + "");

    }

    private static void killAndRemoveContainer(String contId) {
        DockerClientConfig dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .build();
        DockerClient dockerClient = DockerClientBuilder.getInstance(dockerConfig).build();

        dockerClient.killContainerCmd(contId).exec();
        InspectContainerResponse insResp1 = dockerClient.inspectContainerCmd(contId).exec();
        String status1 = insResp1.getState().getStatus();

        System.out.println(" : contID : " + contId + " : Status " + status1 + "");
        dockerClient.removeContainerCmd(contId).exec();
    }
}
