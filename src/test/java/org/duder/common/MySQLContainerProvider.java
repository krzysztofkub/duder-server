package org.duder.common;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.util.function.Consumer;

public class MySQLContainerProvider {
    private static GenericContainer container;

    public static GenericContainer getInstance() {
        if (container == null) {
            container = createContainer();
        }
        return container;
    }

    private static GenericContainer createContainer() {
        return new GenericContainer(new ImageFromDockerfile("mysql-duder")
                .withDockerfileFromBuilder(dockerfileBuilder -> {
                    dockerfileBuilder.from("mysql:5.7.22")
                            .env("MYSQL_ROOT_PASSWORD", "test")
                            .env("MYSQL_DATABASE", "duder")
                            .env("MYSQL_USER", "test")
                            .env("MYSQL_PASSWORD", "test")
                            .add("a_schema.sql", "/docker-entrypoint-initdb.d")
                            .add("b_data.sql", "/docker-entrypoint-initdb.d");
                })
                .withFileFromClasspath("a_schema.sql", "db/mysql/schema.sql")
                .withFileFromClasspath("b_data.sql", "db/mysql/data.sql"))
                .withExposedPorts(3306)
                .withCreateContainerCmdModifier(
                        new Consumer<CreateContainerCmd>() {
                            @Override
                            public void accept(CreateContainerCmd createContainerCmd) {
                                createContainerCmd.withPortBindings(new PortBinding(Ports.Binding.bindPort(3306), new ExposedPort(3306)));
                            }
                        }
                )
                .waitingFor(Wait.forListeningPort());
    }
}