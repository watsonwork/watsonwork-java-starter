## Watsonwork-Java-Starter
This is an app for Java which demonstrates the basic integration flows with Workspaces.
You can see this app:

- Post a message to a space using its own credentials
- Use oauth to gain authorization to perform actions on behalf of another user
- React to Webhooks, in this case by posting a message saying what type of webhook it has seen

## Configuration

### Run locally 

1. Visit the [app creation page](https://workspace.ibm.com/developer/apps) and login.
2. Put whatever name you like in the **App name** field. Add an oauth redirect url with the value 
`https://localhost:8443/oauthCallback` and register the app.
3. In this popup, grab the values for **Id** and **App Secret** since you'll need them later.
4. Now it's time to clone the repo, so open a terminal and run `git clone https://github.com/watsonwork/watsonwork-java-starter.git && cd watsonwork-java-starter`
5. Open the **config.yml** file, change appId to the Id from above, and change appSecret to your secret from above. 
6. In **config.yml**, uncomment the following lines:

    ```
    #        - type: https
    #          port: 8443
    #          keyStorePath: skeleton.keystore
    #          keyStorePassword: skeleton
    #          validateCerts: false
    ```
    Additionally, if you are on macos, uncomment these lines and replace JAVA_HOME with your java home
    ```
    ## trustStorePath is only necessary on macos, replace JAVA_HOME with your own java home
    ##          trustStorePath: JAVA_HOME/jre/lib/security/cacerts
    ```
7. On linux/macos, run the command `./gradlew run`. On windows run `gradlew run`
8. Visit https://localhost:8443
9. Click on the button to observe the oauth flow and authorize the app.

### Run on Bluemix
1. Follow steps 1-5 in [Run Locally](#run-locally)
2. Sign up for a free trial on [Bluemix](https://console.ng.bluemix.net) and create an app.
3. Download and install the [Bluemix cli](http://clis.ng.bluemix.net/ui/home.html) as well as the [Cloud Foundry cli](https://github.com/cloudfoundry/cli/releases)
4. Run `bluemix login -a https://api.ng.bluemix.net`, and enter your email and password when prompted
5. Run `./gradlew clean build` on linux/mac or `gradlew clean build` on windows. This will generate a file called `manifest.yml` in the root folder of your app, containing some default settings to be used when uploading to bluemix.
6. Run `cf push my-app-name`, where `my-app-name` is what you'd like to call your app on bluemix. 
**Tip:** Make sure the name you want to use is not taken on Bluemix already, since it must be unique.
**Tip:** Because of the`manifest.yml` file, if you just type `cf push`, the name of your project folder will be used as the app name on bluemix. To make use of this you need to change the name of the project folder from `watsonwork-java-starter` to `my-app-name` and run the command in step 5 again.
7. When it's finished pushing to bluemix, visit your app's url.

### Set up a Webhook
This can only be done if you have the app set up on a public address, you can use bluemix for this, see [Run on Bluemix](#run-on-bluemix), or port forwarding if you control the wifi router.

1. Go back to the [app creation page](https://workspace.ibm.com/developer/apps) and edit your app.
2. Click on **Add an outbound webhook** and give it a name. The callback url should be your app's public base url followed by `/webhook`, for example `https://watsonwork-java-starter.mybluemix.net/webhook`. Tick the `message-created` event. Do not tick the enable checbox yet, because your app needs to know the webhook secret in order to respond to a verification request from the `Workspaces` server.
3. Click the save button and take note of the **webhook secret**.
4. Go back to your **config.yml** in the local project, and fill in the value for **webhookSecret**.
5. Push to bluemix by following steps 4-6 in [Run on Bluemix](#run-on-bluemix)
6. When the app is finished pushing to bluemix, go back to the [app creation page](https://workspace.ibm.com/developer/apps) one last time, edit your app and enable the webhook. Click save. Your app will now receive webhook events of the types you asked for.
7. Go to workspaces and select the space you want to add the app to. Click the dropdown at the top of the page where its name is, and then click the **Apps** section. You will see your app here.
8. Hover over your app and click **Add to space**.
9. Post a message in your space and watch your app respond.
