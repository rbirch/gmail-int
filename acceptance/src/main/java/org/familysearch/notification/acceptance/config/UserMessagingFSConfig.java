package org.familysearch.notification.acceptance.config;

import org.familysearch.common.ws.client.FSConfig;
import java.net.URI;

public class UserMessagingFSConfig implements FSConfig {
  private final String contextPath;

  private final int port;
  private final int maxHostConnectionsPerHost;
  private final int  maxTotalConnections;

  private final boolean useAddressRandomizer;
  private final boolean useConnectionPooling;
  private boolean useJdkTruststore;
  private final boolean useSSL;

  private final String host;
  private final String addressRandomizerService;
  private final String addressRandomizerHostClass;
  private final String truststorePath;
  private final String truststorePassword;

  private static final int HTTP_PORT = 80;

  private static final int HTTP_SSL_PORT = 443;

  public UserMessagingFSConfig(final String configUri) {
    this(configUri, 0);
  }

  public UserMessagingFSConfig(final String configUri, final int maxConnections) {
    URI uri = URI.create(configUri);
    String scheme = uri.getScheme();
    this.contextPath = uri.getPath();
    this.useSSL = "https".equals(scheme);
    this.port = uri.getPort() == -1 ? (this.useSSL ? HTTP_SSL_PORT : HTTP_PORT) : uri.getPort();
    this.useAddressRandomizer = false;
    this.host = uri.getHost();
    this.addressRandomizerService = null;
    this.addressRandomizerHostClass = null;
    this.useJdkTruststore = false;
    this.truststorePath = null;
    this.truststorePassword = null;

    if(maxConnections > 0) {
      this.useConnectionPooling = true;
      this.maxHostConnectionsPerHost = maxConnections;
      this.maxTotalConnections = maxConnections;
    }
    else {
      this.useConnectionPooling = false;
      this.maxHostConnectionsPerHost = 0;
      this.maxTotalConnections = 0;
    }
  }

  @Override
  public final String getContextPath() {
    return this.contextPath;
  }

  @Override
  public final int getPort() {
    return port;
  }

  @Override
  public final boolean isUseAddressRandomizer() {
    return this.useAddressRandomizer;
  }

  @Override
  public final String getHost() {
    return this.host;
  }

  @Override
  public final String getAddressRandomizerService() {
    return this.addressRandomizerService;
  }

  @Override
  public final String getAddressRandomizerHostClass() {
    return this.addressRandomizerHostClass;
  }

  @Override
  public final boolean isUseSSL() {
    return this.useSSL;
  }

  @Override
  public final boolean isUseJdkTruststore() {
    return this.useJdkTruststore;
  }

  @Override
  public final String getTruststorePath() {
    return this.truststorePath;
  }

  @Override
  public final String getTruststorePassword() {
    return this.truststorePassword;
  }

  public final boolean isUseConnectionPooling() {
    return this.useConnectionPooling;
  }

  @Override
  public final int getMaxHostConnections() {
    return this.maxHostConnectionsPerHost;
  }

  public final int getMaxTotalConnections() {
    return this.maxTotalConnections;
  }
}
