package app;

import app.resources.WorkspaceResource;
import app.workspace.WorkspaceClient;
import app.workspace.WorkspaceService;
import app.workspace.auth.AuthManager;
import app.workspace.auth.AuthService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class WorkspaceApplication extends Application<WorkspaceConfiguration> {
    public static void main(String[] args) throws Exception {
        new WorkspaceApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<WorkspaceConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/vendor"));
    }

    @Override
    public void run(WorkspaceConfiguration config,
            Environment environment) {
        ObjectMapper mapper = environment.getObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(JacksonConverterFactory.create(mapper)).baseUrl(config.getWorkspaceApiUrl()).build();

        WorkspaceService workspaceService = retrofit.create(WorkspaceService.class);
        AuthService authService = retrofit.create(AuthService.class);

        AuthManager authManager = new AuthManager(authService, config.getAppId(), config.getAppSecret(), config.getWebhookSecret(), config.getWorkspaceApiUrl());
        WorkspaceClient workspaceClient = new WorkspaceClient(workspaceService, authManager);

        //register jax-rs resources
        final WorkspaceResource resource = new WorkspaceResource(workspaceClient, authManager);
        environment.jersey().register(resource);

        // TODO This is the app entry point. You can do what you like starting from here.
        // For example send a graphql request to make a space, and then use workspaceClient to post a message to a space by its id.
    }
}
