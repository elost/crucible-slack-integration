package ru.cg.crucible_plugins.slack;

import java.util.ArrayList;
import java.util.List;

public class UsersMapping {
  private List<UsersMapping.UserMapping> users = new ArrayList();

  public UsersMapping() {
  }

  public List<UsersMapping.UserMapping> getUsers() {
    return this.users;
  }

  public void setUsers(List<UsersMapping.UserMapping> users) {
    this.users = users;
  }

  public static class UserMapping {
    private String slack;
    private String jira;

    public UserMapping() {
    }

    public String getSlack() {
      return this.slack;
    }

    public void setSlack(String slack) {
      this.slack = slack;
    }

    public String getJira() {
      return this.jira;
    }

    public void setJira(String jira) {
      this.jira = jira;
    }
  }
}
