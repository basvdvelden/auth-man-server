package nl.management.auth.server.config;

import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

public class JedisPool {
    private static final JedisPoolConfig POOL_CONFIG = JedisPool.buildPoolConfig();
    public static final redis.clients.jedis.JedisPool POOL = new redis.clients.jedis.JedisPool(POOL_CONFIG, "localhost");


    private static JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }
}
