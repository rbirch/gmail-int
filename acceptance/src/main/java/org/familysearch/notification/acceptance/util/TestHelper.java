package org.familysearch.notification.acceptance.util;

import org.codehaus.jackson.map.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rbirch on 4/29/2016.
 */
public class TestHelper  {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  private UmsGmailChecker emailChecker;

  public TestHelper() {


    emailChecker = new UmsGmailChecker();
  }

  public String createJsonUmhMessageSnippet(String id, long timeCreated, String snippetType, String version, String recipientName,
                                             String recipientEmail, String recipientLocale, String recipientCisUserId, String properties)  throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> map = new HashMap<>();
    map.put("id", id);
    map.put("timeCreated", timeCreated);
    map.put("type", snippetType);
    map.put("version", version);
    //(recipientName & recipientEmail & recipientLocale) OR recipientCisUserId
    map.put("recipientName", recipientName);
    map.put("recipientEmail", recipientEmail);
    map.put("recipientLocale", recipientLocale);
    map.put("recipientCisUserId", recipientCisUserId);
    map.put("properties", properties);

    return mapper.writeValueAsString(map);
  }

  public void verifyEmailReceived(String partialBodyText, String snippetId) {
    Object[] ids = emailChecker.getMessageIds(partialBodyText);
    if(ids.length > 0) {
      log.info("SUCCESS! The triggered send email was received for snippet - " + snippetId);
    }
    else {
      log.warn("The triggered send email wasn't received within the timeout period for snippet - " + snippetId);
    }
  }
}
