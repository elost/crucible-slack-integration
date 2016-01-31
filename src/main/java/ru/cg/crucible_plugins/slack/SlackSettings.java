package ru.cg.crucible_plugins.slack;

public class SlackSettings {
  private String webhooks;
  private String users;

  public SlackSettings(String webhooks, String users) {
    this.webhooks = webhooks;
    this.users = users;
  }

  public SlackSettings() {
  }

  public String getWebhooks() {
    return this.webhooks;
  }

  public String getUsers() {
    return this.users;
  }

  public void setWebhooks(String webhooks) {
    this.webhooks = webhooks;
  }

  public void setUsers(String users) {
    this.users = users;
  }
}
