package org.familysearch.notification.acceptance.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import org.apache.commons.lang.time.StopWatch;
import org.familysearch.cmp.qa.util.MessagingDecrypter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;

/**
 * Created by rob birch on 9/25/2015.
 */
public class UmsGmailChecker {
  private static final Logger LOG = LoggerFactory.getLogger(UmsGmailChecker.class);
  private Gmail gmail;
  private String gmailAccount = "ums.fs1@gmail.com";
  private final String PROXY = "inetproxy.wh.ldsglobal.net";
  private final String GMAIL_ACCESS_KEY_FILE = "gmailAccessKey.encrypted";
  private final int PROXY_PORT = 80;
  private final long MAX_WAIT_TIME = 1000 * 60 * 14; // 14 minutes (in milliseconds) max to wait for messages - EC times out at 15 minutes
  private final long SLEEP_TIME_BETWEEN_CHECKS = 1000 * 4; // 4 seconds between each poll while waiting for a message to arrive
  private final long MILLIS_BETWEEN_LOG_ENTRIES = 1000 * 30; // 30 seconds between logging updates when polling for a message

  public UmsGmailChecker() {
    HttpTransport transport = new NetHttpTransport.Builder()
      .setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY, PROXY_PORT)))
      .build();
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

    GoogleCredential credential = null;
    try {
      byte[] appCredentials = new MessagingDecrypter("U2MS-Encrypt-Key").decrypt(GMAIL_ACCESS_KEY_FILE);
      credential = GoogleCredential.fromStream(new ByteArrayInputStream(appCredentials));
    } catch(Exception ex) { throw new RuntimeException(ex); }

    credential = new GoogleCredential.Builder()
      .setTransport(transport)
      .setJsonFactory(jsonFactory)
      .setServiceAccountId(credential.getServiceAccountId())
      .setServiceAccountPrivateKey(credential.getServiceAccountPrivateKey())
      .setServiceAccountScopes(Collections.singleton(GmailScopes.GMAIL_READONLY))
      .setServiceAccountUser(gmailAccount)
      .build();

    this.gmail = new Gmail.Builder(transport, jsonFactory, credential)
      .setApplicationName("GMailChecker")
      .build();
  }

  public String getGmailAccount() {
    return gmailAccount;
  }

  public Gmail getGmail() {
    return this.gmail;
  }

  /*
   * returns the message id's of gmail messages that match the query
   * times out after MAX_WAIT_TIME if messages aren't found by then
   */
  public Object[] getMessageIds(String query) {
    long timeOfLastLogEntry = 0L;
    ListMessagesResponse response = null;
    Gmail.Users.Messages.List request = null;
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    LOG.info(String.format("Checking for GMail message that matches query=\"%s\"", query));

    try {
      request = gmail.users().messages().list("me");
      request.setQ(query);
    }
    catch(IOException ignore){}

    do {
      try {
        response = request.execute();

        if(response.getMessages() != null && response.getMessages().size() > 0) {
          break;
        }
      }
      catch(Exception ignore) {
        LOG.warn("Attempt to check for GMail message failed with exception: " + ignore.toString());
      }

      try {
        Thread.sleep(SLEEP_TIME_BETWEEN_CHECKS);
      }
      catch (Exception ignore) {
        LOG.warn("Exception occurred while sleeping between GMail checks: " + ignore.toString());
      }

      if ((stopWatch.getTime() - timeOfLastLogEntry) > MILLIS_BETWEEN_LOG_ENTRIES) {
        long time = stopWatch.getTime();
        LOG.info(String.format(
            "Waiting for GMail message that matches query=\"%s\", elapsed time is: %s", query, getElapsedTimeFromMillis(time)));
        timeOfLastLogEntry = time;
      }
    }
    while (stopWatch.getTime() < MAX_WAIT_TIME);
    stopWatch.stop();

    LOG.info(String.format(
        "Found GMail message that matches query=\"%s\", elapsed time is: %s", query, getElapsedTimeFromMillis(stopWatch.getTime())));

    try {
      return response.getMessages().toArray();
    }
    catch(NullPointerException npe) {
      return new Object[0];
    }
  }

  private static String getElapsedTimeFromMillis(long time) {
    return String.format("%d minutes and %d seconds", time / 1000 / 60, (time / 1000) % 60);
  }

  public static void main(String[] args) {
    try {
      Object[] response = new UmsGmailChecker().getMessageIds("dc0379a7-5185-4f74-8c88-42af1b9ed02a");
      int x = 0;
    }catch(Exception ex) {
      ex.printStackTrace();
    }
  }
}
