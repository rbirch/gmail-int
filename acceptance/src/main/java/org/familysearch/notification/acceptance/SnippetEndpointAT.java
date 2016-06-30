package org.familysearch.notification.acceptance;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.familysearch.cmp.qa.AbstractAcceptanceTest;
import org.familysearch.notification.acceptance.config.WebConfig;
import org.familysearch.notification.acceptance.util.TestHelper;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.*;

public class SnippetEndpointAT extends AbstractAcceptanceTest {

  TestHelper testHelper = new TestHelper();
  private String sessionId;
  // beta userId for test user..  DON'T CHANGE this user, it's the only one in beta with gmail email address!
  private String UMS_SERVICE_ACCOUNT_USER_ID = "cis.user.MM9V-4TQH";

  @BeforeClass(alwaysRun = true)
  @Parameters({"environment"})
  public void setup(@Optional WebConfig.Environment environment) {
   WebConfig webConfig = new WebConfig(environment);

    sessionId = webConfig.serviceAccountManager().getAuthenticatedServiceAccountSession();
    log.info("sessionId=" + sessionId);
  }

  @Test(groups={"unsafe"}, timeOut=60000)
  public void testSendSnippetNoAuthentication() throws Exception {
    WebResource resource = resourceForPath("/api/snippets");

    Date date = new Date();
    String snippetId = UUID.randomUUID().toString();
    String snippet = testHelper.createJsonUmhMessageSnippet(snippetId, date.getTime(), "ums-test", "acc1", "RUFUS", "ums.fs1@gmail.com",
      "EN", null, "rufus was here");

    ClientResponse response = getResponse(resource, Method.POST, null, snippet);
    assertResponse(response, Response.Status.UNAUTHORIZED.getStatusCode());
  }

  //send a snippet with locale=ES for triggered send
  @Test(groups={"unsafe"}, timeOut=1000 * 60 * 21)
  public void testTriggeredSendSnippetWithUserFieldsES() throws Exception {
    WebResource resource = resourceForPath("/api/snippets");

    Date date = new Date();
    String snippetId = UUID.randomUUID().toString();
    String acceptanceToken = UUID.randomUUID().toString();
    String snippet = testHelper.createJsonUmhMessageSnippet(snippetId, date.getTime(), "ums-test", "acc1", "RUFUS", "ums.fs1@gmail.com",
      "ES", null, String.format("{ \"acceptanceToken\" : \"%s\" }", acceptanceToken));

    ClientResponse response = getResponse(resource, Method.POST, sessionId, snippet);
    log.info("Sent snippet with id: " + snippetId);
    assertResponse(response, Response.Status.OK.getStatusCode());
    testHelper.verifyEmailReceived(acceptanceToken, snippetId);
  }

  //send a snippet with locale=EN for triggered send
  @Test(groups={"unsafe"}, timeOut=1000 * 60 * 21)
  public void testTriggeredSendSnippetWithUserFieldsEN() throws Exception {
    WebResource resource = resourceForPath("/api/snippets");

    Date date = new Date();
    String snippetId = UUID.randomUUID().toString();
    String acceptanceToken = UUID.randomUUID().toString();
    String snippet = testHelper.createJsonUmhMessageSnippet(snippetId, date.getTime(), "ums-test", "acc1", "RUFUS", "ums.fs1@gmail.com",
      "EN", null, String.format("{ \"acceptanceToken\" : \"%s\" }", acceptanceToken));

    ClientResponse response = getResponse(resource, Method.POST, sessionId, snippet);
    log.info("Sent snippet with id: " + snippetId);
    assertResponse(response, Response.Status.OK.getStatusCode());
    testHelper.verifyEmailReceived(acceptanceToken, snippetId);
  }


  @Test(groups={"unsafe"}, timeOut=1000 * 60 * 21)
  public void testTriggeredSendSnippetWithCisUserId() throws Exception {
    WebResource resource = resourceForPath("/api/snippets");

    Date date = new Date();
    String snippetId = UUID.randomUUID().toString();
    String acceptanceToken = UUID.randomUUID().toString();
    String snippet = testHelper.createJsonUmhMessageSnippet(snippetId, date.getTime(), "ums-test", "acc1", null, null,
      null, UMS_SERVICE_ACCOUNT_USER_ID, String.format("{ \"acceptanceToken\":\"%s\" }", acceptanceToken));

    ClientResponse response = getResponse(resource, Method.POST, sessionId, snippet);
    log.info("Sent snippet with id: " + snippetId);
    assertResponse(response, Response.Status.OK.getStatusCode());
    testHelper.verifyEmailReceived(acceptanceToken, snippetId);
  }

  /*
   (recipientName & recipientEmail & recipientLocale) OR recipientCisUserId are mutually exclusive
   */

  @Override
  protected String getBasePathWithRouting() {
    return "/int-user-messaging-handler";
  }

  @Override
  protected String getBasePathDirect() {
    return "";
  }

  @Override
  protected String getBlueprintServiceNameOfTargetService() {
    return "app";
  }
}

