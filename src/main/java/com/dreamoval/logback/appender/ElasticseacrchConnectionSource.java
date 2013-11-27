package com.dreamoval.logback.appender;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.ClientConfig;

/**
 * @author Henry Sampson
 */
public class ElasticseacrchConnectionSource {

    private JestClient client;
    private String applicationName = "some-app";
    private String hostName = "127.0.0.1";
    private String elasticIndex = "logging-index";
    private String elasticType = "logging";
    private String elasticHost = "http://localhost:9200";

    protected void initESClient(){
        try {
            // Configuration
            ClientConfig clientConfig = new ClientConfig.Builder(getElasticHost()).multiThreaded(true).build();

            // Construct a new Jest client according to configuration via factory
            JestClientFactory factory = new JestClientFactory();
            factory.setClientConfig(clientConfig);
            setClient(factory.getObject());
        } catch (Exception ex) {
            System.err.println("Unable to initialize client: " + ex.getLocalizedMessage());
        }

    }

    /**
     * @return the client
     */
    public JestClient getClient() {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(JestClient client) {
        this.client = client;
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * @param applicationName the applicationName to set
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * @return the hostName
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * @param hostName the hostName to set
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * @return the elasticIndex
     */
    public String getElasticIndex() {
        return elasticIndex;
    }

    /**
     * @param elasticIndex the elasticIndex to set
     */
    public void setElasticIndex(String elasticIndex) {
        this.elasticIndex = elasticIndex;
    }

    /**
     * @return the elasticType
     */
    public String getElasticType() {
        return elasticType;
    }

    /**
     * @param elasticType the elasticType to set
     */
    public void setElasticType(String elasticType) {
        this.elasticType = elasticType;
    }

    /**
     * @return the elasticHost
     */
    public String getElasticHost() {
        return elasticHost;
    }

    /**
     * @param elasticHost the elasticHost to set
     */
    public void setElasticHost(String elasticHost) {
        this.elasticHost = elasticHost;
    }


}
