package com.adobe.target.edge.client.local;

import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.model.LocalDecisioningRuleSet;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class DefaultRuleLoader implements RuleLoader {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRuleLoader.class);

    private LocalDecisioningRuleSet latestRules;

    private UnirestInstance unirestInstance = Unirest.spawnInstance();
    private Timer timer = new Timer();
    private boolean started = false;

    public DefaultRuleLoader() {}

    @Override
    public LocalDecisioningRuleSet getLatestRules() {
        return latestRules;
    }

    @Override
    public synchronized void start(final ClientConfig clientConfig) {

        if (clientConfig.getLocalEnvironment() == null) {
            return;
        }

        if (started) {
            return;
        }
        started = true;

        unirestInstance.config()
                .socketTimeout(clientConfig.getSocketTimeout())
                .connectTimeout(clientConfig.getConnectTimeout())
                .concurrency(clientConfig.getMaxConnectionsTotal(), clientConfig.getMaxConnectionsPerHost())
                .automaticRetries(clientConfig.isEnabledRetries())
                .enableCookieManagement(false)
                .setObjectMapper(new JacksonObjectMapper())
                .setDefaultHeader("Accept", "application/json");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DefaultRuleLoader.this.loadRules(clientConfig);
            }
        }, 0, clientConfig.getLocalDecisioningPollingIntSecs() * 1000);
    }

    public void stop() {
        this.timer.cancel();
    }

    private void loadRules(ClientConfig clientConfig) {
        LocalDecisioningRuleSet ruleSet = unirestInstance.get(getLocalDecisioningUrl(clientConfig))
                .asObject(new GenericType<LocalDecisioningRuleSet>(){})
                .getBody();
        if (ruleSet != null && ruleSet.getVersion() != null && ruleSet.getVersion().startsWith("1.")) {
            this.latestRules = ruleSet;
            logger.info("rulesList="+latestRules);
        }
        else if (ruleSet != null) {
            logger.warn("Unknown rules version: " + ruleSet.getVersion());
        }
        else {
            logger.warn("Unable to parse rule set");
        }
    }

    private String getLocalDecisioningUrl(ClientConfig clientConfig) {
        return "https://targetuploadtest.s3-us-west-2.amazonaws.com/" +
                clientConfig.getClient() + "/" +
                clientConfig.getLocalEnvironment().toLowerCase() + "/" +
                "rules.json";
    }
}
