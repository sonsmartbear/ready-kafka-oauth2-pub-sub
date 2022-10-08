package com.smartbear.ready.kafka.oauth2.config;

import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.security.auth.AuthenticateCallbackHandler;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerTokenCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OauthAuthenticateLoginCallbackHandler implements AuthenticateCallbackHandler {
    private final Logger log = LoggerFactory.getLogger(OauthAuthenticateLoginCallbackHandler.class);

    private Map<String, String> moduleOptions = null;
    private boolean configured = false;

    @Override
    public void configure(Map<String, ?> map, String saslMechanism, List<AppConfigurationEntry> jaasConfigEntries) {
        if (!OAuthBearerLoginModule.OAUTHBEARER_MECHANISM.equals(saslMechanism))
            throw new IllegalArgumentException(String.format("Unexpected SASL mechanism: %s", saslMechanism));
        if (Objects.requireNonNull(jaasConfigEntries).size() != 1 || jaasConfigEntries.get(0) == null)
            throw new IllegalArgumentException(
                    String.format("Must supply exactly 1 non-null JAAS mechanism configuration (size was %d)",
                            jaasConfigEntries.size()));
        this.moduleOptions = Collections.unmodifiableMap((Map<String, String>) jaasConfigEntries.get(0).getOptions());
        configured = true;
    }

    public boolean isConfigured(){
        return this.configured;
    }

    @Override
    public void close() {
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        if (!isConfigured())
            throw new IllegalStateException("Callback handler not configured");
        for (Callback callback : callbacks) {
            if (callback instanceof OAuthBearerTokenCallback)
                try {
                    handleCallback((OAuthBearerTokenCallback) callback);
                } catch (KafkaException e) {
                    throw new IOException(e.getMessage(), e);
                }
            else
                throw new UnsupportedCallbackException(callback);
        }
    }

    private void handleCallback(OAuthBearerTokenCallback callback){
        if (callback.token() != null)
            throw new IllegalArgumentException("Callback had a token already");

        log.info("Try to acquire token!");
//        OauthBearerTokenJwt token = OauthHttpCalls.login(null);
        String accessToken = (String) OauthHttpCalls.getEnvironmentVariables("OAUTH2_ACCESS_TOKEN", "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJqRHN1NDc0RXdfYVZQYkxHTzFBTWwxQUotVEUtbjdCdVBXaUMtbkNsR0JNIn0.eyJleHAiOjE2NjUwMDc4MDMsImlhdCI6MTY2NDk3MTgwMywianRpIjoiNTE4MGFkZTMtNTQ1Yy00YzcyLWEzN2ItNTVmYTI1NDFmMzA2IiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjgwODAvYXV0aC9yZWFsbXMvZGVtbyIsInN1YiI6IjhjOWVkZGE1LThlODAtNGZlNC1iMTNhLWY1M2U5Y2MwMzgxYiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImthZmthLXByb2R1Y2VyLWNsaWVudCIsImFjciI6IjEiLCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJjbGllbnRJZCI6ImthZmthLXByb2R1Y2VyLWNsaWVudCIsImNsaWVudEhvc3QiOiIxNzIuMjEuMC4xIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzZXJ2aWNlLWFjY291bnQta2Fma2EtcHJvZHVjZXItY2xpZW50IiwiY2xpZW50QWRkcmVzcyI6IjE3Mi4yMS4wLjEiLCJlbWFpbCI6InNlcnZpY2UtYWNjb3VudC1rYWZrYS1wcm9kdWNlci1jbGllbnRAcGxhY2Vob2xkZXIub3JnIn0.C8MZ3GbyvZBjsE7TwwFVaZxkStcvtoyD15RgT6QN6eqDII1AaseMnz6Rr79_ob00X2VLNOGlcrTiXaUtJBuF8POr5MbXH-21srIDHfumoAwpknq2s8rFztnu7EQuZGERbnSxrqw2CWQox5LcQuK9eMbc39hnVWsCCkM3-vOtAEZpJOHLiDvqy3TDwqot03TtliQrPQg7yMTzRrV01-FyIB733FmiQ95wqOLTUW-hdO0iWr-0jARUZCzvKLHOoy_V0UD38ilLfilTD1_UTDDKGwBpi28sZdBrkH0kF9kSUPEpXalEb0ixEXOJ5JtIQZjpTl8Me2C8W9PZQ83aSrbMDg");
        OauthBearerTokenJwt token = new OauthBearerTokenJwt(accessToken
                , 3600000l, 1663948450514l, "0oa6kif2kqiQBybKT5d7");
        log.info("Retrieved token..");
        if(token == null){
            throw new IllegalArgumentException("Null token returned from server");
        }
        callback.token(token);
    }

}
