package com.bwg.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnProperty(name = "storage.type", havingValue = "S3")
public class AwsS3Config {

    @Bean
    public AmazonS3 amazonS3(@Value("${AWS_ACCESS_KEY_ID:''}") String accessKey,
                             @Value("${AWS_SECRET_ACCESS_KEY:''}")String secretKey,
                             @Value("${AWS_DEFAULT_REGION:eu-west-2}") String region) {

        if(StringUtils.isNotBlank(accessKey) && StringUtils.isNotBlank(secretKey))
        {
            return AmazonS3ClientBuilder.standard()
                    .withCredentials(new InstanceProfileCredentialsProvider(false))
                    .withRegion("eu-west-2")
                    .build();
        }
        else
            return AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.fromName(region))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                    .build();
    }
}
