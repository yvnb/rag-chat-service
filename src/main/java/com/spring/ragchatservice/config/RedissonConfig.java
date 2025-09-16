package com.spring.ragchatservice.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Value("${spring.data.redis.password:}")
    private String password;

    @Value("${spring.data.redis.database:0}")
    private int database;

    @Value("${spring.data.redis.ssl.enabled:false}")
    private boolean ssl;

    @Value("${spring.data.redis.timeout:2000}")
    private int timeout;

    @Value("${spring.data.redis.connect-timeout:1000}")
    private int connectTimeout;

    @Value("${spring.data.redis.client-name:redisson-client}")
    private String clientName;

    @Bean
    @Primary
    public RedissonClient redissonClient() {
        Config config = new Config();

        // Build Redis URL
        String protocol = ssl ? "rediss://" : "redis://";
        String address = protocol + host + ":" + port;

        // Configure single server
        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(address)
                .setDatabase(database)
                .setClientName(clientName)
                .setConnectionPoolSize(32)
                .setConnectionMinimumIdleSize(8)
                .setIdleConnectionTimeout(10000)
                .setConnectTimeout(connectTimeout)
                .setTimeout(timeout)
                .setRetryAttempts(3)
                .setPingConnectionInterval(30000)
                .setKeepAlive(false)
                .setTcpNoDelay(true)
                .setDnsMonitoringInterval(5000);

        // Set password if provided
        if (password != null && !password.trim().isEmpty()) {
            singleServerConfig.setPassword(password);
        }

        // Set thread pools
        config.setThreads(16);
        config.setNettyThreads(32);

        // Use JSON codec for better readability in Redis
        config.setCodec(new org.redisson.codec.JsonJacksonCodec());

        return Redisson.create(config);
    }
}