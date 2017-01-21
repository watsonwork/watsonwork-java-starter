package app.workspace;

import java.util.logging.Level;
import java.util.logging.Logger;

import app.workspace.auth.AuthManager;
import app.workspace.model.Message;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkspaceClient {
    private static final Logger LOGGER = Logger.getLogger(WorkspaceClient.class.getName());
    private final WorkspaceService workspaceService;
    private final AuthManager authManager;

    public WorkspaceClient(WorkspaceService workspaceService, AuthManager authManager) {
        this.workspaceService = workspaceService;
        this.authManager = authManager;
    }

    public void createMessageAsApp(String spaceId, Message message) {
        Call<Message> call = workspaceService.createMessage(authManager.getAppAuth(), spaceId, message);

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                LOGGER.log(Level.INFO, "Message successfully posted to Inbound Webhook.");
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                LOGGER.log(Level.SEVERE, "Posting message to Inbound Webhook failed.", t);
            }
        });
    }
}
