package com.example.samplerest;

import com.example.samplerest.verticle.APIVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class MainApplication {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(APIVerticle.class.getName(), new DeploymentOptions().setInstances(
                Runtime.getRuntime().availableProcessors() / 2
        ));
    }
}
