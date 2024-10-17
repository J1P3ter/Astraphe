package com.j1p3ter.productserver.config;

import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

public class AwsCredentialProvider implements AwsCredentialsProvider {

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Override
    public AwsCredentials resolveCredentials() {
        return AwsBasicCredentials.builder()
                .accessKeyId(accessKey)
                .secretAccessKey(secretKey)
                .build();
    }
}
