package com.powernode.common.config.redisson;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedissonConfig {

    private String host;
    private String port;
    private String password;
    private int timeout = 3000;

    private static String ADDRESS_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {

        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer().setAddress(ADDRESS_PREFIX + host + ":" + port).setTimeout(timeout);

        return Redisson.create(config);

    }

}
