package nl.management.auth.server.common;

import nl.management.auth.server.config.JedisPool;
import redis.clients.jedis.Jedis;

public class JedisService {
    public JedisService() {}

    public boolean isBlacklisted(String accessToken) {
        try (Jedis jedis = JedisPool.POOL.getResource()) {
            deleteExpiredFromBlacklist(jedis);
            Double nullIfNotBlacklisted = jedis.zscore("blacklist", accessToken);
            return nullIfNotBlacklisted != null;
        }
    }

    public boolean isBlacklistedForRefresh(String accessToken) {
        try (Jedis jedis = JedisPool.POOL.getResource()) {
            deleteExpiredFromRefreshBlacklist(jedis);
            Double nullIfNotBlacklisted = jedis.zscore("refreshBlacklist", accessToken);
            return nullIfNotBlacklisted != null;
        }
    }

    public void addToBlacklist(Double exp, String token) {
        try (Jedis jedis = JedisPool.POOL.getResource()) {
            jedis.zadd("blacklist", exp, token);
        }
    }

    public void addToRefreshBlacklist(Double exp, String accessToken) {
        try (Jedis jedis = JedisPool.POOL.getResource()) {
            jedis.zadd("refreshBlacklist", exp, accessToken);
        }
    }

    private void deleteExpiredFromBlacklist(Jedis jedis) {
        jedis.zremrangeByScore("blacklist", 0.0, Long.valueOf(System.currentTimeMillis()).doubleValue());
    }

    private void deleteExpiredFromRefreshBlacklist(Jedis jedis) {
        jedis.zremrangeByScore("refreshBlacklist", 0.0, Long.valueOf(System.currentTimeMillis()).doubleValue());
    }
}
