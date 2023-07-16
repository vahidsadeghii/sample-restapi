package com.example.samplerest.util;

import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class ResponseUtils {
    public static void buildSuccessResponseWithBody(RoutingContext rc, Object response){
        rc.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(response));
    }
}
