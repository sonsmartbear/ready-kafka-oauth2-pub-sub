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
//        OauthBearerTokenJwt oauthBearerTokenJwt = OauthHttpCalls.login(null);
//        0oa6kif2kqiQBybKT5d7
        String accessToken = (String) OauthHttpCalls.getEnvironmentVariables("OAUTH2_ACCESS_TOKEN", "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIwNHo1U1FrZk1PR05YU1pVaHViSy1DSU5zMnRvZkp0dTNYS0IxZWZncUZvIn0.eyJleHAiOjE2NjY0Njk1MDMsImlhdCI6MTY2NjQzMzUwMywianRpIjoiOGY3MjdmNTktZjhlMi00MDhiLTk5ZWYtZGFiNDk2NDNjMmUwIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjgwODAvYXV0aC9yZWFsbXMvZGVtbyIsInN1YiI6Ijk4ZWY1MzdjLWQzNzItNDA4My05MmZlLTkyNThjNWYzMjQzMyIsInR5cCI6IkJlYXJlciIsImF6cCI6ImthZmthLXByb2R1Y2VyLWNsaWVudCIsImFjciI6IjEiLCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJjbGllbnRJZCI6ImthZmthLXByb2R1Y2VyLWNsaWVudCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiY2xpZW50SG9zdCI6IjE3Mi4yNy4wLjEiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzZXJ2aWNlLWFjY291bnQta2Fma2EtcHJvZHVjZXItY2xpZW50IiwiY2xpZW50QWRkcmVzcyI6IjE3Mi4yNy4wLjEiLCJlbWFpbCI6InNlcnZpY2UtYWNjb3VudC1rYWZrYS1wcm9kdWNlci1jbGllbnRAcGxhY2Vob2xkZXIub3JnIn0.fPphBG9yeRqddgTqZ9nB_nWuujn8LFJm3ugbBEquoSCOZ45nqJnFDhEC8BzYoXErszj1-WXlBxDKgAwXkk_RvrH4Vy47ouY1XwMBhq6sd65Jbb20ivq_f3LVU5089DIZ2iiB9MCEkmPW2C0-LExlEbModl-Si3eQcwtratF1do2GsY8XcVBUs46Vd1BEjUbGTM7zXZL7hUsLukr4d_EeGlAgJoK68hdyncUSIYOrx9xRG57NbOEmT4V9S06M1VLO0rk1F9JTMrDvGZeDryoNEE2bkr0X1tB2A0UyMoag4euNYkxcBeYCIEGAbM6iDFlkUwuLvDrwsMECtOtV79qGxQ");
        OauthBearerTokenJwt oauthBearerTokenJwt = new OauthBearerTokenJwt(accessToken
                , 3600000l, 1663948450514l, "0oa6kif2kqiQBybKT5d7");
        log.info("Retrieved token..");
        if(oauthBearerTokenJwt == null){
            throw new IllegalArgumentException("Null token returned from server");
        }
        callback.token(oauthBearerTokenJwt);
    }

}
