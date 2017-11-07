package com.siemens.bt.jazz.services.PersonalTokenService.builder;

import com.google.gson.JsonObject;
import com.ibm.team.repository.common.IContributor;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.util.JazzLog;
import com.ibm.team.repository.service.TeamRawService;
import com.siemens.bt.jazz.services.PersonalTokenService.internal.BuildSecretsHelper;
import com.siemens.bt.jazz.services.PersonalTokenService.internal.BuildSecretsReader;
import com.siemens.bt.jazz.services.PersonalTokenService.jazz.AdvancedProperties;
import com.siemens.bt.jazz.services.PersonalTokenService.jazz.User;
import com.siemens.bt.jazz.services.PersonalTokenService.security.Crypto;
import com.siemens.bt.jazz.services.base.rest.AbstractRestService;
import com.siemens.bt.jazz.services.base.rest.RestRequest;
import org.apache.commons.logging.Log;
import org.apache.http.auth.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

public class TokenReaderService extends AbstractRestService {
    private static final JazzLog logger = JazzLog.getLog(TokenReaderService.class);

    public TokenReaderService(Log log, HttpServletRequest request, HttpServletResponse response, RestRequest restRequest, TeamRawService parentService) {
        super(log, request, response, restRequest, parentService);
    }

    public void execute() throws IOException, URISyntaxException, AuthenticationException {
        IContributor user = User.getCurrentContributor(parentService);
        String key = restRequest.getParameterValue("key");
        if(user == null) {
            response.setStatus(401);
            return;
        }
        String hashedKey = BuildSecretsHelper.generateKey(user.getUserId(), key);
        String privateKey = AdvancedProperties.getPrivateKey();
        String securedValue = null;
        String userToken = null;
        try {
            securedValue = BuildSecretsReader.getSecretByContributor(parentService, user, hashedKey);
            userToken = Crypto.decrypt(securedValue, privateKey);
        } catch (GeneralSecurityException ex) {
            logger.error("[PTS] Token Service decryption failed for user '" + user.getUserId() + "'. Error message: " + ex.getMessage());
            response.setStatus(500);
        } catch (UnsupportedEncodingException ex) {
            logger.error("[PTS] Token Service decryption failed due to encoding issues for user '" + user.getUserId() + "'. Error message: " + ex.getMessage());
            response.setStatus(500);
        } catch (TeamRepositoryException ex) {
            logger.error("[PTS] Problem reading Build Secrets Store for user '" + user.getUserId() + "'. Error message: " + ex.getMessage());
            response.setStatus(500);
        }

        if(userToken != null && userToken.length() > 0 && securedValue != null) {
            JsonObject formattedResponse = new JsonObject();
            formattedResponse.addProperty("token", userToken);
            response.getWriter().write(formattedResponse.toString());
            response.setStatus(200);
            logger.info("[PTS] Token read for '" + user.getUserId() + "'");
        } else {
            response.setStatus(404);
        }
    }
}
