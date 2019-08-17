package com.pro_crafting.tools.githubjwtIntegration.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.pro_crafting.tools.githubjwtIntegration.jwt.service.model.JWTUser;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.eclipse.egit.github.core.service.UserService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RequestScoped
public class JWTService {
    @Inject
    @ConfigProperty(name = "token.issuer")
    String issuer;

    @Inject
    @ConfigProperty(name = "token.key.secret")
    String secretKey;

    @Inject
    @ConfigProperty(name = "token.key.expiry.minutes")
    Long expiryMinutes;

    @Inject
    @ConfigProperty(name = "github.organization")
    String organizationName;

    @Inject
    @ConfigProperty(name = "authorization.file.uri")
    URI authorizationFileUri;

    @Inject
    ObjectMapper objectMapper;

    public String create(String userName, String personalAccessToken) throws Exception {
        User user = getUser(userName, personalAccessToken);

        if (user == null) {
            return null;
        }

        List<JWTUser> jwtUsers = getJwtUsers();
        Optional<JWTUser> any = jwtUsers.stream().filter(u -> userName.equals(u.getUserName())).findAny();
        if (!any.isPresent()) {
            return null;
        }

        return createToken(user, any.get());
    }

    private User getUser(String userName, String personalAccessToken) throws IOException {
        GitHubClient client = new GitHubClient();
        client.setCredentials(userName, personalAccessToken);

        UserService user = new UserService(client);

        if (organizationName != null) {
            OrganizationService organizationService = new OrganizationService(client);
            if (!organizationService.isMember(organizationName, user.getUser().getLogin())) {
                return null;
            }
        }

        return user.getUser();
    }

    private List<JWTUser> getJwtUsers() {
        JWTUserFileClient jwtUserFileClient = RestClientBuilder.newBuilder().baseUri(authorizationFileUri).build(JWTUserFileClient.class);
        String fileContent = jwtUserFileClient.download();

        JavaType javaType = objectMapper.getTypeFactory().constructType(new TypeReference<List<JWTUser>>() {
        });

        try {
            return objectMapper.reader().forType(javaType).readValue(fileContent);
        } catch (IOException e) {
            return Lists.newArrayList();
        }
    }

    private String createToken(User user, JWTUser jwtUser) {

        JWTCreator.Builder builder = JWT.create();
        builder.withIssuedAt(new Date());

        builder.withExpiresAt(toDate(LocalDateTime.now().plus(expiryMinutes, ChronoUnit.MINUTES)));
        builder.withIssuer(issuer);

        builder.withSubject(user.getLogin());
        builder.withClaim("company", user.getCompany());
        builder.withClaim("avatar", user.getAvatarUrl());
        builder.withClaim("url", user.getHtmlUrl());
        builder.withArrayClaim("groups", jwtUser.getRoles());
        return builder.sign(Algorithm.HMAC512(secretKey));
    }

    private Date toDate(LocalDateTime dateToConvert) {
        return java.util.Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }
}
