package org.familysearch.notification.acceptance;


import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.familysearch.cmp.qa.AbstractAcceptanceTest;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class HealthChecksAT extends AbstractAcceptanceTest {

  public HealthChecksAT() {
    super();
  }

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

  @Test(groups={"production"}, priority=0, timeOut=600000)
  public void testHeartbeat()  throws Exception {
    WebResource resource = resourceForPath("/healthcheck/heartbeat");
    ClientResponse response = resource.get(ClientResponse.class);
    assertThat(response, is(notNullValue()));
    assertThat(response.getStatus(), is(ClientResponse.Status.OK.getStatusCode()));
  }

  @Test(groups={"production"}, priority=0, timeOut=600000)
  public void testVitals() throws Exception {
    WebResource resource = resourceForPath("/healthcheck/vital");
    ClientResponse response = resource.get(ClientResponse.class);
    assertThat(response, is(notNullValue()));
    assertThat(response.getStatus(), is(ClientResponse.Status.OK.getStatusCode()));
  }

}
