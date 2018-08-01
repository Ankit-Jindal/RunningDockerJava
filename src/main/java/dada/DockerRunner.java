package dada;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.BuildImageResultCallback;

import java.io.File;

public class DockerRunner {

    public static void main(String arg[]){
        runByDockerfile();
    }
    private static String runByDockerfile() {
        DockerClientConfig dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .build();

        DockerClient dockerClient = DockerClientBuilder.getInstance(dockerConfig).build();

        String imageId = dockerClient.buildImageCmd()
                .withDockerfile(new File("/Users/pcadminio-tahoe.com/Projects/dockerjava/src/main/resources/Dockerfile"))
                .withPull(true)
                .withNoCache(true)
                .exec(new BuildImageResultCallback())
                .awaitImageId();

        System.out.println("imageId>>"+imageId);

        System.out.println("Build succesfull with imageID : " + imageId);

        CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(imageId).withHostName("localhost");
        CreateContainerResponse createContainerResponse = createContainerCmd.exec();
        String contId = createContainerResponse.getId();
        StartContainerCmd startContainerCmd = dockerClient.startContainerCmd(contId);
        startContainerCmd.exec();

        InspectContainerResponse resp = dockerClient.inspectContainerCmd(contId).exec();

        String status = resp.getState().getStatus();
        System.out.println("Image : " + imageId + " : contID : " + contId + " : Status " + status + "");

        return contId;


    }
}
