package com.wholeseeds.mindle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class NcpObjectStorageConfig {
	@Value("${ncp.object.endpoint}")
	private String endpoint;
	@Value("${ncp.object.region}")
	private String region;
	@Value("${ncp.object.access-key}")
	private String accessKey;
	@Value("${ncp.object.secret-key}")
	private String secretKey;

	@Bean
	public AmazonS3 ncpObjectStorageClient() {
		AwsClientBuilder.EndpointConfiguration ep = new AwsClientBuilder.EndpointConfiguration(endpoint, region);

		return AmazonS3ClientBuilder.standard()
			.withEndpointConfiguration(ep)
			.withCredentials(new AWSStaticCredentialsProvider(
				new BasicAWSCredentials(accessKey, secretKey)))
			.withPathStyleAccessEnabled(true)
			.build();
	}
}
