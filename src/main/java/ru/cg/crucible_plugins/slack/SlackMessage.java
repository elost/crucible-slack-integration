package ru.cg.crucible_plugins.slack;

import java.text.MessageFormat;

public class SlackMessage {
  private String text;
  private String channel;
  private String username;
  private String icon_url;

  public SlackMessage() {
  }

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getChannel() {
    return this.channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getIcon_url() {
    return this.icon_url;
  }

  public void setIcon_url(String icon_url) {
    this.icon_url = icon_url;
  }

  public static String url(String url, String text) {
    return MessageFormat.format("<{0}|{1}>", new Object[] {url, text});
  }
}
