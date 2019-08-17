package com.pro_crafting.tools.githubjwtIntegration.jwt.rest;

import com.pro_crafting.tools.githubjwtIntegration.jwt.rest.model.AuthenticationImportData;
import com.pro_crafting.tools.githubjwtIntegration.jwt.service.JWTService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("jwts")
@RequestScoped
public class JWTResource {
    @Inject
    JWTService service;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String createAuthorizationToken(AuthenticationImportData authenticationImportData) throws Exception {
        return service.create(authenticationImportData.getUserName(), authenticationImportData.getPersonalAccessToken());
    }
}
