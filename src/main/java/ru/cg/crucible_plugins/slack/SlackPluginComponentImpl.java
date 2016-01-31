package ru.cg.crucible_plugins.slack;

import com.atlassian.sal.api.ApplicationProperties;

public class SlackPluginComponentImpl implements SlackPluginComponent {
  private final ApplicationProperties applicationProperties;

  public SlackPluginComponentImpl(ApplicationProperties applicationProperties) {
    this.applicationProperties = applicationProperties;
  }

  public String getName() {
    return null != this.applicationProperties ? "myComponent:" + this.applicationProperties.getDisplayName() : "myComponent";
  }
}
