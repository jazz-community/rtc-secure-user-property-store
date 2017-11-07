package com.siemens.bt.jazz.services.PersonalTokenService.builder;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ibm.team.repository.common.IContributor;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.util.JazzLog;
import com.ibm.team.repository.service.TeamRawService;
import com.siemens.bt.jazz.services.PersonalTokenService.internal.BuildSecretsHelper;
import com.siemens.bt.jazz.services.PersonalTokenService.internal.BuildSecretsWriter;
import com.siemens.bt.jazz.services.PersonalTokenService.jazz.AdvancedProperties;
import com.siemens.bt.jazz.services.PersonalTokenService.jazz.User;
import com.siemens.bt.jazz.services.PersonalTokenService.security.Crypto;
import com.siemens.bt.jazz.services.base.rest.AbstractRestService;
import com.siemens.bt.jazz.services.base.rest.RestRequest;
import com.siemens.bt.jazz.services.base.utils.RequestReader;
import org.apache.commons.logging.Log;
import org.apache.http.auth.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

public final class TokenWriterService extends AbstractRestService {
    private static final JazzLog logger = JazzLog.getLog(TokenWriterService.class);

    public TokenWriterService(Log log, HttpServletRequest request, HttpServletResponse response, RestRequest restRequest, TeamRawService parentService) {
        super(log, request, response, restRequest, parentService);
    }

    public void execute() throws IOException, URISyntaxException, AuthenticationException {
        IContributor user = User.getCurrentContributor(parentService);
        JsonObject tokenStoreRequestData = RequestReader.readAsJson(request);
        if(user == null) {
            response.setStatus(401);
            return;
        }
        JsonPrimitive remoteKeyPrimitive = tokenStoreRequestData.getAsJsonPrimitive("key");
        JsonPrimitive tokenPrimitive = tokenStoreRequestData.getAsJsonPrimitive("token");
        if(remoteKeyPrimitive != null && tokenPrimitive != null) {
            String token = tokenPrimitive.getAsString();
            String scope = remoteKeyPrimitive.getAsString();
            String hashedKey = BuildSecretsHelper.generateKey(user.getUserId(), scope);
            String privateKey = AdvancedProperties.getPrivateKey();
            try {
                String encryptedToken = Crypto.encrypt(token, privateKey);
                BuildSecretsWriter.storeSecret(parentService, user, hashedKey, encryptedToken);
                logger.info("[PTS] Token created by '" + user.getUserId() + "'");
                response.setStatus(201);
            } catch (GeneralSecurityException ex) {
                logger.error("[PTS] Token Service encryption failed for user '" + user.getUserId() + "'. Error message: " + ex.getMessage());
                response.setStatus(500);
            } catch (UnsupportedEncodingException ex) {
                logger.error("[PTS] Token Service encryption failed due to encoding issues for user '" + user.getUserId() + "'. Error message: " + ex.getMessage());
                response.setStatus(500);
            } catch (TeamRepositoryException ex) {
                logger.error("[PTS] Problem writing to Build Secrets Store for user '" + user.getUserId() + "'. Error message: " + ex.getMessage());
                response.setStatus(500);
            }
        } else {
            response.setStatus(400);
        }
    }
}
