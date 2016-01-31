package ru.cg.crucible_plugins.slack;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.gson.Gson;

public class SlackServlet extends HttpServlet {
  private final TemplateRenderer templateRenderer;
  private final SlackSettingsStore store;
  private final SlackSender slackSender;

  public SlackServlet(TemplateRenderer templateRenderer, SlackSettingsStore store, SlackSender slackSender) {
    this.templateRenderer = templateRenderer;
    this.store = store;
    this.slackSender = slackSender;
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    request.setAttribute("decorator", "fisheye.userprofile.tab");
    response.setContentType("text/html");
    HashMap params = new HashMap();
    SlackSettings settings = this.store.getSettings();
    if (settings == null) {
      settings = new SlackSettings("", "");
    }

    params.put("slackSettings", settings);
    this.templateRenderer.render("templates/config.vm", params, response.getWriter());
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String webhooks = request.getParameter("webhooks");
    String users = request.getParameter("users");
    SlackSettings settings = new SlackSettings(webhooks, users);
    if (users != null && !users.isEmpty()) {
      (new Gson()).fromJson(users, UsersMapping.class);
    }

    this.store.setSettings(settings);
    SlackMessage slackMessage = new SlackMessage();
    slackMessage.setUsername("Crucible");
    slackMessage.setText("Configuration updated");
    this.slackSender.sendMessage(slackMessage);
    response.sendRedirect("./slack-servlet");
  }
}
