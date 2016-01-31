package ru.cg.crucible_plugins.slack;

import java.util.Iterator;

import com.atlassian.crucible.spi.services.UserService;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.gson.Gson;

import ru.cg.crucible_plugins.slack.UsersMapping.UserMapping;

public class SlackSettingsStoreImpl implements SlackSettingsStore {
  private static final String KEY = "slackSettings";
  private final UserService userService;
  private final PluginSettingsFactory pluginSettingsFactory;

  public SlackSettingsStoreImpl(UserService userService, PluginSettingsFactory pluginSettingsFactory) {
    this.userService = userService;
    this.pluginSettingsFactory = pluginSettingsFactory;
  }

  public SlackSettings getSettings() {
    String slackSettings = (String) this.pluginSettingsFactory.createGlobalSettings().get("slackSettings");
    return slackSettings != null ? (SlackSettings) (new Gson()).fromJson(slackSettings, SlackSettings.class) : null;
  }

  public void setSettings(SlackSettings settings) {
    String json = null;
    if (settings != null) {
      json = (new Gson()).toJson(settings);
    }

    this.pluginSettingsFactory.createGlobalSettings().put("slackSettings", json);
  }

  public String findSlackUserName(String jiraUserName) {
    SlackSettings settings = this.getSettings();
    if (settings == null) {
      return null;
    }
    else {
      if (settings.getUsers() != null) {
        UsersMapping usersMapping = (UsersMapping) (new Gson()).fromJson(settings.getUsers(), UsersMapping.class);
        Iterator var4 = usersMapping.getUsers().iterator();

        while (var4.hasNext()) {
          UserMapping userMapping = (UserMapping) var4.next();
          if (userMapping.getJira().equals(jiraUserName)) {
            return userMapping.getSlack();
          }
        }
      }

      return null;
    }
  }
}
