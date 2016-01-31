package ru.cg.crucible_plugins.slack;

import java.io.IOException;
import java.util.Objects;

import com.google.gson.Gson;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

public class SlackSenderImpl implements SlackSender {
  private final SlackSettingsStore slackSettingsStore;

  public SlackSenderImpl(SlackSettingsStore slackSettingsStore) {
    this.slackSettingsStore = slackSettingsStore;
  }

  public void sendMessage(SlackMessage slackMessage) {
    Objects.requireNonNull(slackMessage);
    String json = (new Gson()).toJson(slackMessage);
    SlackSettings settings = this.slackSettingsStore.getSettings();
    String webhooks = settings != null ? settings.getWebhooks() : null;
    if (webhooks != null && !webhooks.isEmpty()) {
      HttpClient httpClient = new HttpClient();
      PostMethod post = new PostMethod(webhooks);
      post.addParameter("payload", json);

      try {
        int e = httpClient.executeMethod(post);
        if (e != 200) {
          System.err.print("Error send slack message to " + webhooks + " text " + json);
        }
      }
      catch (IOException var8) {
        var8.printStackTrace();
      }
    }

  }
}
