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
        String accessToken = (String) OauthHttpCalls.getEnvironmentVariables("OAUTH2_ACCESS_TOKEN", "eyJraWQiOiJ5bGJTenZBb08tMC1SUzVUYXU1WHFHYzJLU0xKTFVVWUV6d015QXFxYVhrIiwiYWxnIjoiUlMyNTYifQ.eyJ2ZXIiOjEsImp0aSI6IkFULkFoTm1sMU5xX1R0TkVBLWQ1eVZVTDRlMlpmMmdNam5SN3ZoLVIzOF9jODAiLCJpc3MiOiJodHRwczovL2Rldi02ODgzMTc0My5va3RhLmNvbS9vYXV0aDIvYWFzb2t0YSIsImF1ZCI6ImFwaTovL2Fhc29rdGEiLCJpYXQiOjE2NjQyNjg0MjQsImV4cCI6MTY2NDI3MjAyNCwiY2lkIjoiMG9hNmtpZjJrcWlRQnliS1Q1ZDciLCJzY3AiOlsia2Fma2EiXSwic3ViIjoiMG9hNmtpZjJrcWlRQnliS1Q1ZDcifQ.G7JS6Yv5iEUCnShg8y8RaspuwOUxj6IrEo3Co6w-ieVWGK0a_ri2oYbX4KwBbvfjnH63dAgdxeKPAPoHBGm6b1WijFSPufvwdHkrWxmfN4iRJmbmLK7ALZD_wkEjxj8nvYncL1rlDgrdm1wneu1gFmlvPO44phRJUfX3q8jJSBs1vj85sqW6tbmrTF6mtcZib9rJsGAPCsqTRllOUnwZMQFt68QdvsO7JALEDG7Pmtdsu1anDoO2wAe0TWEtMzDwpTcaL4xX2njmC6477PIJi1SlWJxKNFvmV_Ok79twGUrU9xgvS-0Il-S8mNV1ScOUWBTE659wUb-7p32gBrO1Bg");
        OauthBearerTokenJwt token = new OauthBearerTokenJwt(accessToken
                , 3600000l, 1663948450514l, "0oa6kif2kqiQBybKT5d7");
        log.info("Retrieved token..");
        if(token == null){
            throw new IllegalArgumentException("Null token returned from server");
        }
        callback.token(token);
    }

}
