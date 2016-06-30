package org.familysearch.notification.acceptance.config;

import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import org.familysearch.cis.client.api.IdentityManager;
import org.familysearch.cis.client.impl.IdentityManagerImpl;
import org.familysearch.common.cis.client.account.ServiceAccountManagerBase;
import org.familysearch.common.ws.client.FSConfig;

import javax.ws.rs.core.MediaType;

import static com.sun.jersey.api.client.config.ClientConfig.PROPERTY_CONNECT_TIMEOUT;
import static com.sun.jersey.api.client.config.ClientConfig.PROPERTY_READ_TIMEOUT;

/**
 * Created by rbirch on 8/25/2015.
 */
public class WebConfig {
  public enum Environment { INTEGRATION, STAGING, PRODUCTION }

  protected final String USER_NAME = "UserMessagingServiceAccount";
  protected final String DEV_KEY = "47QK-8DK7-H8K1-BKKB-8K14-DF67-JPDL-VBWN";
  protected final String CIS_DEV_PUBLIC_ENDPOINT = "http://integration.familysearch.org/cis-public-api";
  protected final String CIS_STAGING_PUBLIC_ENDPOINT = "http://cis-a.vip.fsglobal.net/cis-public-api";
  protected final int CIS_MAX_CONNECTIONS = 2;
  protected final Integer CIS_READ_TIMEOUT = 1000;
  protected final Integer CIS_CONNECT_TIMEOUT = 1000;

  private String env;

  public WebConfig(Environment environment) {
    env = environment == Environment.INTEGRATION ? CIS_DEV_PUBLIC_ENDPOINT : CIS_STAGING_PUBLIC_ENDPOINT;
  }

  public ServiceAccountManagerBase serviceAccountManager() {
    final ServiceAccountManagerBase serviceAccountManagerBase = new ServiceAccountManagerBase();
    serviceAccountManagerBase.setServiceAccountName(USER_NAME);
    serviceAccountManagerBase.setDeveloperKey(DEV_KEY);
    serviceAccountManagerBase.setKeystoreAlias("user-messaging-service");
    serviceAccountManagerBase.setSasObjectKeyForPrivateKeystore("user-messaging-service-CIS-Keystore");
    serviceAccountManagerBase.setIdentityManager(identityManager());

    return serviceAccountManagerBase;
  }

  public IdentityManager identityManager() {
    final ClientConfig cisClientConfig = new DefaultApacheHttpClientConfig();
    cisClientConfig.getProperties().put(PROPERTY_READ_TIMEOUT, CIS_READ_TIMEOUT);
    cisClientConfig.getProperties().put(PROPERTY_CONNECT_TIMEOUT, CIS_CONNECT_TIMEOUT);
    IdentityManagerImpl identityManager = new IdentityManagerImpl(cisClientConfig,cisFsConfig());
    identityManager.setMediaType(MediaType.APPLICATION_JSON_TYPE);
    return identityManager;
  }

  public FSConfig cisFsConfig() {
    return new UserMessagingFSConfig(env, CIS_MAX_CONNECTIONS);
  }
}
