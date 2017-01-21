package app.resources;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import app.workspace.WorkspaceClient;
import app.workspace.auth.AuthManager;
import app.workspace.model.Annotation;
import app.workspace.model.Message;
import app.workspace.model.OauthResponse;
import app.workspace.model.WebhookEvent;
import com.google.common.io.CharStreams;
import retrofit2.http.Body;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class WorkspaceResource {

    @Context
    private UriInfo uriInfo;
    private WorkspaceClient workspaceClient;
    private AuthManager authManager;

    public WorkspaceResource(WorkspaceClient workspaceClient, AuthManager authManager) {
        this.workspaceClient = workspaceClient;
        this.authManager = authManager;
    }

    /**
     * This method produces the home page.
     * <p>
     * If it sees a cookie it recognises it uses that information to say hi to the user.
     * <p>
     * Otherwise it shows a button to start the oauth flow.
     */
    @Produces(MediaType.TEXT_HTML)
    @GET
    public Response homepage(@CookieParam("id") Cookie idCookie) throws IOException {
        if (idCookie == null) {
            return Response.ok(getUnauthenticatedPage()).build();
        } else {
            OauthResponse userOauthDetails = authManager.getUserOAuthResponse(idCookie.getValue());
            if (userOauthDetails == null) {
                //delete cookies, we don't have this person any more
                return Response.ok(getUnauthenticatedPage()).cookie((NewCookie) null).build();
            } else {
                return Response.ok(getAuthenticatedPage(userOauthDetails)).build();
            }
        }
    }


    /**
     * This method is the webhook callback. It will respond depending on the type of message.
     * <p>
     * If the type of message is 'verification', it hashes the response body and sends it back.
     * It doesn't verify the input token because this is a sample app.
     * <p>
     * Put any logic which depends on a webhook event in here.
     *
     * @param outboundToken A token apps should verify, this app doesn't.
     * @param webhookEvent  contains information about the webhook, use this to decide how to react.
     * @return Either a verification response or a 200 OK
     */
    @Path("webhook")
    @POST
    public Response webhookCallback(@HeaderParam("X-OUTBOUND-TOKEN") String outboundToken,
            @Body WebhookEvent webhookEvent) throws NoSuchAlgorithmException, InvalidKeyException {
        if ("verification".equalsIgnoreCase(webhookEvent.getType())) {
            return buildVerificationResponse(webhookEvent);
        }

        //if it wasn't our app that sent it... No point talking to ourselves.
        if (!authManager.getAppId().equals(webhookEvent.getUserId())) {
            //TODO Add code here to respond to a webhook.
            // You will likely want to do different things depending on the type of the webhookEvent.

            // For now just post a message saying we received an event
            workspaceClient.createMessageAsApp(webhookEvent.getSpaceId(),
                    buildMessage("Received event",
                            String.format("Received event with type %s", webhookEvent.getType())));
        }

        return Response.ok().build();
    }

    /**
     * Handles exchanging the oauth code for an access token. Redirects to the homepage when done.
     */
    @Path("oauthCallback")
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    public Response oauthCallback(@QueryParam("code") String code, @QueryParam("state") String state) throws IOException {
        OauthResponse response = authManager.exchangeCodeForToken(code, "authorization_code", getRedirectUri());

        authManager.addUserAuth(response);
        NewCookie idCookie = new NewCookie("id", response.getId());

        //TODO at this point you have access a the user's access token, id and displayName in the response variable.
        // Right now we just redirect to the homepage, add a cookie so we remember them, and say hi. Because it's polite.

        return Response.temporaryRedirect(uriInfo.getBaseUri()).cookie(idCookie).build();
    }


    //utility methods below

    /**
     * Builds a message for use with {@link app.workspace.WorkspaceService#createMessage(String, String, Message)}
     *
     * @param messageTitle the message title
     * @param messageText  the message text
     * @return
     */
    private Message buildMessage(String messageTitle, String messageText) {
        Annotation annotation = new Annotation();
        annotation.setType("generic");
        annotation.setVersion(1.0);
        annotation.setColor("#1DB954");
        annotation.setTitle(messageTitle);
        annotation.setText(messageText);

//        Actor actor = new Actor();
//        actor.setUrl("");
//        actor.setAvatar("");
//        actor.setName("");
//        annotation.setActor(actor);

        Message message = new Message();
        message.setType("appMessage");
        message.setVersion(1.0);
        message.setAnnotations(Collections.singletonList(annotation));

        return message;
    }

    private String getRedirectUri() {
        return uriInfo.getBaseUri().toString() + "oauthCallback";
    }

    private String getUnauthenticatedPage() throws IOException {
        String htmlTemplate = CharStreams.toString(new InputStreamReader(WorkspaceResource.class.getResourceAsStream("/unauthenticated.htm")));

        return htmlTemplate
                .replace("@WORKSPACE_API_URL@", authManager.getWorkspaceApiUrl())
                .replace("@CLIENT_ID@", authManager.getAppId())
                .replace("@REDIRECT_URI@", getRedirectUri())
                .replace("@STATE@", "rdjmnwjnj");
    }

    private String getAuthenticatedPage(OauthResponse oauthResponse) throws IOException {
        String htmlTemplate = CharStreams.toString(new InputStreamReader(WorkspaceResource.class.getResourceAsStream("/authenticated.htm")));

        return htmlTemplate.replace("@DISPLAY_NAME@", oauthResponse.getDisplayName());
    }

    private Response buildVerificationResponse(WebhookEvent webhookEvent) throws InvalidKeyException, NoSuchAlgorithmException {
        String responseBody = String.format("{\"response\": \"%s\"}", webhookEvent.getChallenge());

        String verificationHeader = createVerificationHeader(responseBody);

        return Response.ok(responseBody, MediaType.APPLICATION_JSON_TYPE)
                .header("X-OUTBOUND-TOKEN", verificationHeader)
                .build();
    }

    private String createVerificationHeader(String responseBody) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec keySpec = new SecretKeySpec(authManager.getWebhookSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(keySpec);
        byte[] hashBytes = mac.doFinal(responseBody.getBytes(StandardCharsets.UTF_8));
        String hexedHash = hexDigest(hashBytes);

        return hexedHash;
    }

    /**
     * Convert given bytes to hex
     */
    private String hexDigest(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

}
