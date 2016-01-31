package ru.cg.crucible_plugins.slack;

public interface SlackSettingsStore {
  SlackSettings getSettings();

  void setSettings(SlackSettings var1);

  String findSlackUserName(String var1);
}
