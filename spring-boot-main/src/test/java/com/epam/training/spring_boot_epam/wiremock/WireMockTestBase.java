package com.epam.training.spring_boot_epam.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public abstract class WireMockTestBase {

    protected static WireMockServer wireMockServer;

    @BeforeAll
    static void setupWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8761));
        wireMockServer.start();

        configureFor("localhost", 8761);

        stubFor(get(urlPathMatching("/eureka/apps"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"applications\": {\"versions__delta\": \"1\", \"apps__hashcode\": \"\", \"application\": [{\"name\": \"TRAINING-WORKLOAD-SERVICE\", \"instance\": [{\"hostName\": \"localhost\", \"port\": {\"$\": 8081, \"@enabled\": \"true\"}, \"app\": \"TRAINING-WORKLOAD-SERVICE\", \"status\": \"UP\"}]}]}}")));

        stubFor(get(urlPathMatching("/eureka/apps/TRAINING-WORKLOAD-SERVICE"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"application\": {\"name\": \"TRAINING-WORKLOAD-SERVICE\", \"instance\": [{\"hostName\": \"localhost\", \"port\": {\"$\": 8081, \"@enabled\": \"true\"}, \"app\": \"TRAINING-WORKLOAD-SERVICE\", \"status\": \"UP\"}]}}")));
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }
}