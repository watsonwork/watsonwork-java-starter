package app;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class WorkServicesConfiguration extends Configuration {
    @NotEmpty
    private String appId;

    @NotEmpty
    private String appSecret;

    @NotEmpty
    private String workspaceApiUrl;

    private String webhookSecret;

    @JsonProperty
    public String getAppId() {
        return appId;
    }

    @JsonProperty
    public void setAppId(String appId) {
        this.appId = appId;
    }

    @JsonProperty
    public String getAppSecret() {
        return appSecret;
    }

    @JsonProperty
    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    @JsonProperty
    public String getWebhookSecret() {
        return webhookSecret;
    }

    @JsonProperty
    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    @JsonProperty
    public String getWorkspaceApiUrl() {
        return workspaceApiUrl;
    }

    @JsonProperty
    public void setWorkspaceApiUrl(String workspaceApiUrl) {
        this.workspaceApiUrl = workspaceApiUrl;
    }
}
