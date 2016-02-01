package ru.cg.crucible_plugins.slack;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.atlassian.crucible.event.AllReviewersCompletedEvent;
import com.atlassian.crucible.event.CommentCreatedEvent;
import com.atlassian.crucible.event.ReviewEvent;
import com.atlassian.crucible.event.ReviewStateChangedEvent;
import com.atlassian.crucible.spi.PermId;
import com.atlassian.crucible.spi.data.*;
import com.atlassian.crucible.spi.data.ReviewData.State;
import com.atlassian.crucible.spi.services.ReviewService;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;

public class ReviewListener {
  private final ReviewService reviewService;
  private final SlackSettingsStore slackSettingsStore;
  private final SlackSender slackSender;

  public ReviewListener(ReviewService reviewService, SlackSettingsStore slackSettingsStore, SlackSender slackSender, EventPublisher eventPublisher) {
    this.reviewService = reviewService;
    this.slackSettingsStore = slackSettingsStore;
    this.slackSender = slackSender;
    eventPublisher.register(this);
  }

  @EventListener
  public void reviewCreated(ReviewStateChangedEvent reviewCreatedEvent) {
    if (reviewCreatedEvent.getNewState() == State.Review) {
      DetailedReviewData details = this.reviewService.getReviewDetails(reviewCreatedEvent.getReviewId());
      Set<ReviewerData> reviewer = details.getReviewers().reviewer;
      Set<UserData> all = new HashSet<UserData>();
      all.add(details.getModerator());
      all.addAll(reviewer);
      this.sendReviewStarted(all, reviewCreatedEvent);
    }

  }

  @EventListener
  public void allReviewerCompletedEvent(AllReviewersCompletedEvent allReviewersCompletedEvent) {
    PermId id = allReviewersCompletedEvent.getReviewId();
    DetailedReviewData reviewData = this.reviewService.getReviewDetails(id);
    String reviewAuthor = this.slackSettingsStore.findSlackUserName(reviewData.getAuthor().getUserName());
    String moderator = this.slackSettingsStore.findSlackUserName(reviewData.getModerator().getUserName());

    SlackMessage slackMessage = new SlackMessage();
    slackMessage.setText("Review completed " + SlackMessage.url("http://crucible.center.cg/cru/" + id.getId(), id.getId()));
    this.slackSender.sendMessage(slackMessage);

    if (reviewAuthor != null) {
      slackMessage.setChannel("@" + reviewAuthor);
      this.slackSender.sendMessage(slackMessage);
    }

    if (moderator != null) {
      slackMessage.setChannel("@" + moderator);
      this.slackSender.sendMessage(slackMessage);
    }
  }

  @EventListener
  public void newCommentAdded(CommentCreatedEvent commentCreatedEvent) {
    ReviewData reviewData = this.reviewService.getReview(commentCreatedEvent.getReviewId());
    CommentData commentData = this.reviewService.getComment(commentCreatedEvent.getCommentId());
    Set reviewers = this.reviewService.getAllReviewers(commentCreatedEvent.getReviewId());
    HashSet users = new HashSet();
    users.add(reviewData.getAuthor());
    Iterator var6 = reviewers.iterator();

    while (var6.hasNext()) {
      ReviewerData userData = (ReviewerData) var6.next();
      users.add(userData);
    }

    var6 = users.iterator();

    while (var6.hasNext()) {
      UserData userData1 = (UserData) var6.next();
      if (!userData1.equals(commentData.getUser())) {
        SlackMessage slackMessage = this.createNewComment(commentData.getPermaIdAsString(), commentData.getUser(), userData1, commentData.getMessage());
        if (slackMessage != null) {
          this.slackSender.sendMessage(slackMessage);
        }
      }
    }

  }

  private void sendReviewStarted(Set<UserData> reviewersData, ReviewEvent reviewEvent) {
    ReviewData reviewData = this.reviewService.getReview(reviewEvent.getReviewId());
    String reviewAuthor = this.slackSettingsStore.findSlackUserName(reviewData.getAuthor().getUserName());
    if (reviewAuthor != null) {
      HashSet reviewers = new HashSet();
      Iterator id = reviewersData.iterator();

      while (id.hasNext()) {
        ReviewerData text = (ReviewerData) id.next();
        String slackMessage = this.slackSettingsStore.findSlackUserName(text.getUserName());
        if (slackMessage != null) {
          reviewers.add(slackMessage);
        }
      }

      String id1 = reviewData.getPermaId().getId();
      String text1 = "@" + reviewAuthor + " started review " + SlackMessage.url("http://crucible.center.cg/cru/" + id1, id1) + ", reviewers: " + String.join(",", reviewers);
      SlackMessage slackMessage1 = new SlackMessage();
      slackMessage1.setText(text1);
      this.slackSender.sendMessage(slackMessage1);
      Iterator var9 = reviewers.iterator();

      while (var9.hasNext()) {
        String reviewer = (String) var9.next();
        SlackMessage personalMessage = new SlackMessage();
        personalMessage.setText("Review " + SlackMessage.url("http://crucible.center.cg/cru/" + id1, id1));
        personalMessage.setChannel("@" + reviewer);
        this.slackSender.sendMessage(personalMessage);
      }

    }
  }

  private SlackMessage createNewComment(String commentId, UserData from, UserData to, String message) {
    String fromSlack = this.slackSettingsStore.findSlackUserName(from.getUserName());
    String toSlack = this.slackSettingsStore.findSlackUserName(to.getUserName());
    if (fromSlack != null && toSlack != null) {
      String text = "@" + fromSlack + " : " + message + " " + SlackMessage.url("http://crucible.center.cg/cru/" + commentId, commentId);
      SlackMessage slackMessage = new SlackMessage();
      slackMessage.setText(text);
      slackMessage.setChannel("@" + toSlack);
      return slackMessage;
    }
    else {
      return null;
    }
  }
}
