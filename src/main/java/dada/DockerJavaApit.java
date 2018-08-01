package dada;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;

import java.io.File;
import java.util.Arrays;
import java.util.List;


public class DockerJavaApit {
    public static void main(String[] args) throws  Exception{
        String nameImage = "alekseysavitskiy/hbase";//"dajobe/hbase";

        String contId = dockerWithDockerFile();
//String contId = startContainerWithImageName(nameImage);
        //startHbaseContaner();
        // HbaseDocker.createHbaseTable();
         //killAndRemoveContainer(contId);
//40809,35179


    }


    private static void startHbaseContaner() throws  Exception{

        String  hostBuildDir = new File("./data/").getCanonicalPath();

        File buildLogDirzoo1 = new File(hostBuildDir, "zoo1/data/");
        File buildLogDirzoo2 = new File(hostBuildDir, "zoo1/data/datalog");

        if (!buildLogDirzoo1.exists() && !buildLogDirzoo2.exists() && !buildLogDirzoo1.mkdirs() && !buildLogDirzoo2.mkdirs()) {
            throw new IllegalStateException("fail to create buildLogDir:" + buildLogDirzoo2);
        }


        DockerClientConfig dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .build();
        DockerClient dockerClient = DockerClientBuilder.getInstance(dockerConfig).build();
        System.out.println("Docker Client built");
        String network = "io18";
        dockerClient.createNetworkCmd().withName(network).exec();
        //zookeeper:3.4.10
        String zooImage = "zookeeper:3.4.10";
        ExposedPort expPort2181 = ExposedPort.tcp(2181);
        Ports.Binding bindPOrt2181 = Ports.Binding.bindPort(2181);
        PortBinding zooPb = new PortBinding(bindPOrt2181, expPort2181);
        Ports portszoo = new Ports();
        portszoo.add(zooPb);


        PullImageCmd zooImgCmd = dockerClient.pullImageCmd(zooImage);
        PullImageResultCallback callback = zooImgCmd.exec(new PullImageResultCallback());
        callback.awaitSuccess();
        Volume v1 = new Volume("/data");
        Volume v2 = new Volume("/datalog");
        CreateContainerResponse response = dockerClient.createContainerCmd(zooImage)
                .withName("zoo1")
                .withPortBindings(portszoo)
                .withHostName("zoo1")
                .withPortBindings(portszoo)
                .withExposedPorts(expPort2181)
                .withEnv(Arrays.asList("ZOO_MY_ID: 1", "ZOO_PORT: 2181", "ZOO_SERVERS: server.1=zoo1:2888:3888", " ZOO_MAX_CLIENT_CNXNS: 0"))
                .withBinds(new Bind(buildLogDirzoo1.getCanonicalPath(), v1),new Bind(buildLogDirzoo2.getCanonicalPath(),v2))
                .withNetworkMode(network)
                .withAliases("zoo1", "zookeeper")
                .exec();


        String contId = response.getId();
        String[] warnings = response.getWarnings();
        dockerClient.startContainerCmd(contId).exec();
        InspectContainerResponse insResp = dockerClient.inspectContainerCmd(contId).exec();
        String status = insResp.getState().getStatus();
        System.out.println(" contID : " + contId + " : Status " + status + "");


        //alekseysavitskiy/hbase
/*        - 16010:16010
                - 16000:16000*/

        File buildLogDirHbase = new File(hostBuildDir, "hbase/data/");

        if (!buildLogDirHbase.exists() && !buildLogDirHbase.mkdirs()) {
            throw new IllegalStateException("fail to create buildLogDir:" + buildLogDirzoo2);
        }


        String hbaseImage = "alekseysavitskiy/hbase";
        ExposedPort expPort16010 = ExposedPort.tcp(16010);
        Ports.Binding bindPrt16010 = Ports.Binding.bindPort(16010);
        PortBinding hbasePB16010 = new PortBinding(bindPrt16010, expPort16010);

        ExposedPort expPort16000 = ExposedPort.tcp(16000);
        Ports.Binding bindPrt16000 = Ports.Binding.bindPort(16000);
        PortBinding hbasePB16000 = new PortBinding(bindPrt16000, expPort16000);

        //Ports  portszoo = new Ports();
        portszoo = new Ports();
        portszoo.add(hbasePB16010, hbasePB16000);


        PullImageCmd hbaseImgCmd = dockerClient.pullImageCmd(hbaseImage);
        PullImageResultCallback callbackhbase = hbaseImgCmd.exec(new PullImageResultCallback());
        callbackhbase.awaitSuccess();



/*        "ZK_QUORUM=localhost"
        "HBASE_ROOTDIR=file:///home/hbase"
        "HBASE_CLUSTER_DISTRIBUTED=true"*/
        CreateContainerResponse responsehbase = dockerClient.createContainerCmd(hbaseImage)
                .withName("hbase-master")
                .withHostName("hbase-master")
                .withCmd("/usr/local/hbase-1.4.4/bin/hbase master start")
                .withPortBindings(portszoo)
                .withExposedPorts(expPort16010, expPort16000)
                .withEnv("ZK_QUORUM=zoo1", "HBASE_ROOTDIR=file:///home/hbase", "HBASE_CLUSTER_DISTRIBUTED=true")
                .withBinds(new Bind(buildLogDirHbase.getCanonicalPath(), new Volume("/home/hbase")))
                .withNetworkMode(network)
                .exec();


        contId = responsehbase.getId();
        warnings = response.getWarnings();
        dockerClient.startContainerCmd(contId).exec();
        insResp = dockerClient.inspectContainerCmd(contId).exec();
        status = insResp.getState().getStatus();
        System.out.println(" contID : " + contId + " : Status " + status + "");

        // alekseysavitskiy/hbase


        ExposedPort expPort16020 = ExposedPort.tcp(16020);
        Ports.Binding bindPrt16020 = Ports.Binding.bindPort(16020);
        PortBinding hbasePB16020 = new PortBinding(bindPrt16020, expPort16020);

        ExposedPort expPort16030 = ExposedPort.tcp(16030);
        Ports.Binding bindPrt16030 = Ports.Binding.bindPort(16030);
        PortBinding hbasePB16030 = new PortBinding(bindPrt16030, expPort16030);

        portszoo = new Ports();
        portszoo.add(hbasePB16020, hbasePB16030);

        zooImgCmd = dockerClient.pullImageCmd(hbaseImage);
        callback = zooImgCmd.exec(new PullImageResultCallback());
        callback.awaitSuccess();


        response = dockerClient.createContainerCmd(hbaseImage)
                .withName("hbase-region")
                      .withHostName("hbase-region")
                .withCmd("/usr/local/hbase-1.4.4/bin/hbase regionserver start")
                .withPortBindings(portszoo)
                .withExposedPorts(expPort16030, expPort16020)
                .withEnv("ZK_QUORUM=zoo1", "HBASE_ROOTDIR=file:///home/hbase", "HBASE_CLUSTER_DISTRIBUTED=true")
                .withBinds(new Bind(buildLogDirHbase.getCanonicalPath(), new Volume("/home/hbase")))
                 //.withVolumes(Arrays.asList(new Volume("/home/hbase")))
                .withNetworkMode(network)
                .exec();

        contId = response.getId();
        warnings = response.getWarnings();
        dockerClient.startContainerCmd(contId).exec();
        insResp = dockerClient.inspectContainerCmd(contId).exec();
        status = insResp.getState().getStatus();
        System.out.println(" contID : " + contId + " : Status " + status + "");

        List<Network> listNw = dockerClient.listNetworksCmd().exec();
        listNw.forEach(i -> System.out.println(i));
    }

    private static String dockerWithDockerFile() {
        String dockerDir = "/Users/ankitkumar/Documents/dockerTest/HBASE_DOCKER_S2";//"/Users/ankitkumar/Documents/dockerTest/hbase.docker";

        //2181:2181 -p 60000:60000 -p 60010:60010 -p 60020:60020 -p 60030:60030

        ExposedPort expzoo = ExposedPort.tcp(2181);
        Ports.Binding hoszoo = Ports.Binding.bindPort(2181);

        ExposedPort exphbase60000 = ExposedPort.tcp(16000);
        Ports.Binding pohbase60000 = Ports.Binding.bindPort(16000);

        ExposedPort exphbase16010 = ExposedPort.tcp(16010);
        Ports.Binding pohbase16010 = Ports.Binding.bindPort(16010);

        ExposedPort exphbase16201 = ExposedPort.tcp(16201);
        Ports.Binding pohbase16201 = Ports.Binding.bindPort(16201);

        ExposedPort exphbase16301 = ExposedPort.tcp(16301);
        Ports.Binding pohbase16301 = Ports.Binding.bindPort(16301);


        PortBinding portBindingZoo = new PortBinding(hoszoo, expzoo);
        PortBinding portBindingHbase00 = new PortBinding(pohbase60000, exphbase60000);
        PortBinding portBindingHbase10 = new PortBinding(pohbase16010, exphbase16010);
        PortBinding portBindingHbase20 = new PortBinding(pohbase16201, exphbase16201);
        PortBinding portBindingHbase30 = new PortBinding(pohbase16301, exphbase16301);


        Ports ports = new Ports();
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
                .withHostName("localhost")
                .withPortBindings(ports)
                .withExposedPorts(expzoo, exphbase60000, exphbase16010, exphbase16201, exphbase16301);
        CreateContainerResponse createContainerResponse = createContainerCmd.exec();
        String contId = createContainerResponse.getId();
        StartContainerCmd startContainerCmd = dockerClient.startContainerCmd(contId);
        startContainerCmd.exec();

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

        PortBinding portBindingZoo = new PortBinding(hoszoo, expzoo);
        PortBinding portBindingHbase = new PortBinding(pohbase, exphbase);

        Ports ports = new Ports();

        ports.add(portBindingHbase, portBindingZoo);

        DockerClient dockerClient = DockerClientBuilder.getInstance(dockerConfig).build();

        System.out.println("Docker Client built");

        PullImageCmd imgCmd = dockerClient.pullImageCmd(nameImage).withTag("latest");

        PullImageResultCallback callback = imgCmd.exec(new PullImageResultCallback());

        callback.awaitSuccess();


        CreateContainerResponse response = dockerClient.createContainerCmd(nameImage)
                .withPortBindings(ports)
                .withHostName("localhost")
                .withPortBindings(portBindingZoo)
                .exec();


        String contId = response.getId();
        String[] warnings = response.getWarnings();


        dockerClient.startContainerCmd(contId).exec();


        InspectContainerResponse insResp = dockerClient.inspectContainerCmd(contId).exec();
        String status = insResp.getState().getStatus();

        System.out.println("Image : " + nameImage + " : contID : " + contId + " : Status " + status + "");

        return contId;

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
