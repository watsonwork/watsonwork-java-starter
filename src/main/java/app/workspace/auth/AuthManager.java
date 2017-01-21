package app.workspace.auth;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import app.workspace.model.OauthResponse;
import app.workspace.model.TokenResponse;
import retrofit2.Response;

public class AuthManager {

    private static final Logger LOGGER = Logger.getLogger(AuthManager.class.getName());
    private final Map<String, OauthResponse> authMap = new ConcurrentHashMap<>();
    private final String appId;
    private final String appSecret;
    private final String webhookSecret;
    private final String workspaceApiUrl;
    private final AuthService authService;
    private String appToken;
    private Date appTokenExpireTime;

    public AuthManager(AuthService authService, String appId, String appSecret, String webhookSecret, String workspaceApiUrl) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.webhookSecret = webhookSecret;
        this.authService = authService;
        this.workspaceApiUrl = workspaceApiUrl;
    }

    public synchronized String getAppAuth() {
        //if we never got the token or if the token is expired, set it
        if (appTokenExpireTime == null || appTokenExpireTime.before(new Date())) {
            try {
                TokenResponse tokenResponse = authService.authenticateApp(createAppAuthHeader(), "client_credentials").execute().body();
                appTokenExpireTime = getDate(tokenResponse.getExpiresIn());
                appToken = tokenResponse.getAccessToken();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
        }

        return "Bearer " + appToken;
    }

    public OauthResponse exchangeCodeForToken(String code, String grantType, String redirectUri) throws IOException {
        Response<OauthResponse> response = authService.exchangeCodeForToken(createAppAuthHeader(), code, grantType, redirectUri).execute();
        return response.body();
    }

    public OauthResponse getUserOAuthResponse(String userId) {
        if (authMap.containsKey(userId)) {
            return authMap.get(userId);
        } else {
            return null;
        }
    }

    public void addUserAuth(OauthResponse oauthResponse) {
        authMap.put(oauthResponse.getId(), oauthResponse);
    }

    public String getAppId() {
        return appId;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public String getWorkspaceApiUrl() {
        return workspaceApiUrl;
    }

    private Date getDate(Integer secondsFromNow) {
        int millisFromNow = secondsFromNow * 1000;
        return new Date(System.currentTimeMillis() + millisFromNow);
    }

    private String createAppAuthHeader() {
        byte[] authorization = Base64.getEncoder().encode(new String(appId + ":" + appSecret).getBytes());
        return "Basic " + new String(authorization);
    }
}
