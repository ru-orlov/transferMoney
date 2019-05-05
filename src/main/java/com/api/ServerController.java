package com.api;

import com.services.ServerService;
import spark.Route;

public class ServerController {

    ServerService serverService;

    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    public Route emptyAnswer(){
        return (request, response) -> serverService.emptyAnswer();
    }
}
