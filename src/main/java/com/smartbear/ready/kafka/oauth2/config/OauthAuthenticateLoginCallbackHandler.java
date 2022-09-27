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
        String accessToken = (String) OauthHttpCalls.getEnvironmentVariables("OAUTH2_ACCESS_TOKEN", "eyJraWQiOiJ5bGJTenZBb08tMC1SUzVUYXU1WHFHYzJLU0xKTFVVWUV6d015QXFxYVhrIiwiYWxnIjoiUlMyNTYifQ.eyJ2ZXIiOjEsImp0aSI6IkFULjZuMFpnRWlqR1dpMXZ5X19FTGp2SVUxcjBsc01aQzl1cWdNY2NJZ0xZZmciLCJpc3MiOiJodHRwczovL2Rldi02ODgzMTc0My5va3RhLmNvbS9vYXV0aDIvYWFzb2t0YSIsImF1ZCI6ImFwaTovL2Fhc29rdGEiLCJpYXQiOjE2NjQyNTk3OTIsImV4cCI6MTY2NDI2MzM5MiwiY2lkIjoiMG9hNmtpZjJrcWlRQnliS1Q1ZDciLCJzY3AiOlsia2Fma2EiXSwic3ViIjoiMG9hNmtpZjJrcWlRQnliS1Q1ZDcifQ.Mw7WMeI476qga5b3sgoz7gGGsWTGChy2TSbOh4ulgWb6cBvIQZuLuSPTlRe-HNz3l08Tf2V-GN2emOev-aDN66_SXZ519Ap5tlhqYXspH3DpMcdZmj4FMa9-zkRRkHpNfwPCYouwWwVlF2gYwxwzuRSwkl03afhFc69isQRRK-tmfsWIP2SijRIASS3bAKxieRhGp7pNPFIWR8HiGL1GEv_pUFQlU1oahq90N9_kkpqKvfwsxPvi33EnJ_TqZUSol3CyBRyWLB6g3Ak75l0MHumrGZzeMtHRHsvOVfKkiFa25vE5DG5TJ6tOzYvtpZiqLcMeAUf6nZqk4y05Y5dd6A");
        OauthBearerTokenJwt token = new OauthBearerTokenJwt(accessToken
                , 3600000l, 1663948450514l, "0oa6kif2kqiQBybKT5d7");
        log.info("Retrieved token..");
        if(token == null){
            throw new IllegalArgumentException("Null token returned from server");
        }
        callback.token(token);
    }

}
