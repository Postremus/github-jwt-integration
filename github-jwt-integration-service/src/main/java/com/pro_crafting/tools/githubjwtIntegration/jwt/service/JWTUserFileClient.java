package com.pro_crafting.tools.githubjwtIntegration.jwt.service;

import com.pro_crafting.tools.githubjwtIntegration.jwt.service.model.JWTUser;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

public interface JWTUserFileClient {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String download();
}
