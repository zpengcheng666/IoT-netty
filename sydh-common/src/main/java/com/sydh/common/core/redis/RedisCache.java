package com.sydh.common.core.redis;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sydh.common.utils.StringUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Component
public class RedisCache {
    private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);
    @Resource
    public RedisTemplate redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public RedisCache() {
    }

    public <T> void setCacheObject(String key, T value) {
        this.redisTemplate.opsForValue().set(key, value);
    }

    public <T> void setCacheObject(String key, T value, Integer timeout, TimeUnit timeUnit) {
        this.redisTemplate.opsForValue().set(key, value, (long)timeout, timeUnit);
    }

    public boolean expire(String key, long timeout) {
        return this.expire(key, timeout, TimeUnit.SECONDS);
    }

    public boolean expire(String key, long timeout, TimeUnit unit) {
        return this.redisTemplate.expire(key, timeout, unit);
    }

    public long getExpire(String key) {
        return this.redisTemplate.getExpire(key);
    }

    public Boolean hasKey(String key) {
        return this.redisTemplate.hasKey(key);
    }

    public <T> T getCacheObject(String key) {
        ValueOperations valueOperations = this.redisTemplate.opsForValue();
        return (T) valueOperations.get(key);
    }

    public boolean deleteObject(String key) {
        return this.redisTemplate.delete(key);
    }

    public boolean deleteObject(Collection collection) {
        return this.redisTemplate.delete(collection) > 0L;
    }

    public <T> long setCacheList(String key, List<T> dataList) {
        Long result = this.redisTemplate.opsForList().rightPushAll(key, dataList);
        return result == null ? 0L : result;
    }

    public <T> List<T> getCacheList(String key) {
        return this.redisTemplate.opsForList().range(key, 0L, -1L);
    }

    public <T> BoundSetOperations<String, T> setCacheSet(String key, Set<T> dataSet) {
        BoundSetOperations<String, T> setOperations = this.redisTemplate.boundSetOps(key);
        Iterator<T> iterator = dataSet.iterator();

        while(iterator.hasNext()) {
            setOperations.add(iterator.next());
        }

        return setOperations;
    }

    public <T> Set<T> getCacheSet(String key) {
        return this.redisTemplate.opsForSet().members(key);
    }

    public <T> void setCacheMap(String key, Map<String, T> dataMap) {
        if (dataMap != null) {
            this.redisTemplate.opsForHash().putAll(key, dataMap);
        }

    }

    public <T> Map<String, T> getCacheMap(String key) {
        return this.redisTemplate.opsForHash().entries(key);
    }

    public <T> void setCacheMapValue(String key, String hKey, T value) {
        this.redisTemplate.opsForHash().put(key, hKey, value);
    }

    public <T> T getCacheMapValue(String key, String hKey) {
        HashOperations<String, String, T> hashOperations = this.redisTemplate.opsForHash();
        return hashOperations.get(key, hKey);
    }

    public Long getCacheMapSize(String key) {
        return this.redisTemplate.opsForHash().size(key);
    }

    public <T> List<T> getMultiCacheMapValue(String key, Collection<Object> hKeys) {
        return this.redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    public boolean deleteCacheMapValue(String key, String hKey) {
        return this.redisTemplate.opsForHash().delete(key, new Object[]{hKey}) > 0L;
    }

    public Collection<String> keys(String pattern) {
        return this.redisTemplate.keys(pattern);
    }

    public boolean containsKey(String key) {
        return this.redisTemplate.hasKey(key);
    }

    public long incr(String key, long delta) {
        if (delta < 0L) {
            throw new RuntimeException("递增因子必须大于0");
        } else {
            return this.redisTemplate.opsForValue().increment(key, delta);
        }
    }

    public Long incr2(String key, long liveTime) {
        RedisAtomicLong atomicLong = new RedisAtomicLong(key, this.redisTemplate.getConnectionFactory());
        Long incrementValue = atomicLong.getAndIncrement();
        if (incrementValue == 0L && liveTime > 0L) {
            atomicLong.expire(liveTime, TimeUnit.HOURS);
        }

        return incrementValue;
    }

    public long sAdd(String key, Object... values) {
        try {
            return this.redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = this.redisTemplate.opsForSet().add(key, values);
            if (time > 0L) {
                this.expire(key, time);
            }

            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public long setRemove(String key, Object... values) {
        try {
            Long count = this.redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public boolean zSetAdd(String key, String value, double score) {
        try {
            Boolean result = this.stringRedisTemplate.opsForZSet().add(key, value, score);
            return BooleanUtils.isTrue(result);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean zRem(String key, Object... values) {
        try {
            Long count = this.stringRedisTemplate.opsForZSet().remove(key, values);
            return count != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Long zRemBySocre(String key, double start, double end) {
        try {
            return this.stringRedisTemplate.opsForZSet().removeRangeByScore(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long zRank(String key, String value) {
        try {
            return this.stringRedisTemplate.opsForZSet().rank(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<String> zRange(String key, int start, int end) {
        try {
            return this.stringRedisTemplate.opsForZSet().range(key, (long)start, (long)end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<String> zRangeByScore(String key, double start, double end) {
        try {
            return this.stringRedisTemplate.opsForZSet().rangeByScore(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<String> zRange(String key, long start, long end) {
        try {
            return this.stringRedisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long zSize(String key) {
        try {
            return this.stringRedisTemplate.opsForZSet().zCard(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<String> getListKeyByPrefix(String prefix) {
        Set<String> keys = this.redisTemplate.keys(prefix.concat("*"));
        return keys;
    }

    public Cursor<Map.Entry<Object, Object>> hashScan(String key, ScanOptions options) {
        return this.redisTemplate.opsForHash().scan(key, options);
    }

    public Map hashEntity(String key) {
        return this.redisTemplate.boundHashOps(key).entries();
    }

    public void hashPutAll(String key, Map<String, String> maps) {
        this.redisTemplate.opsForHash().putAll(key, maps);
    }

    public void hashPutAllObj(String key, Map<String, Object> maps) {
        this.redisTemplate.opsForHash().putAll(key, maps);
    }

    public Map<String, Map> hashGetAllByKeys(Set<String> keys, String hkeyCondition) {
        return (Map)this.redisTemplate.execute((RedisCallback) (connection) -> {
            Iterator<String> keyIterator = keys.iterator();
            HashMap<String, Map> resultMap = new HashMap<>();

            while(keyIterator.hasNext()) {
                String key = keyIterator.next();
                Map hashValues = connection.hGetAll(key.getBytes());
                if (CollectionUtils.isEmpty(hashValues)) {
                    return new HashMap(0);
                }

                HashMap<String, String> keyValueMap = new HashMap<>(hashValues.size());
                Iterator entryIterator = hashValues.entrySet().iterator();

                while(entryIterator.hasNext()) {
                    Map.Entry entry = (Map.Entry)entryIterator.next();
                    String hashKey = new String((byte[])((byte[])entry.getKey()));
                    if (!"".equals(hkeyCondition)) {
                        if (hashKey.endsWith(hkeyCondition)) {
                            keyValueMap.put(new String((byte[])((byte[])entry.getKey())), new String((byte[])((byte[])entry.getValue())));
                        }
                    } else {
                        keyValueMap.put(new String((byte[])((byte[])entry.getKey())), new String((byte[])((byte[])entry.getValue())));
                    }
                }

                resultMap.put(key, keyValueMap);
            }

            return resultMap;
        });
    }

    public Map<String, String> hashGetAllMatchByKeys(Set<String> keys, String operator, String id, String triggerValue, String modelIndex) {
        String finalId;
        int arrayIndex;
        if (id.startsWith("array_")) {
            int underscoreIndex = id.indexOf("_", id.indexOf("_") + 1);
            finalId = id.substring(underscoreIndex + 1);
            List<String> idParts = StringUtils.str2List(id, "_", true, true);
            arrayIndex = Integer.parseInt(idParts.get(1));
        } else {
            arrayIndex = -1;
            finalId = id;
        }

        return (Map)this.redisTemplate.execute((RedisCallback) (connection) -> {
            Iterator<String> keyIterator = keys.iterator();
            HashMap<String, String> resultMap = new HashMap<>();

            label42:
            while(keyIterator.hasNext()) {
                String key = keyIterator.next();
                Map hashValues = connection.hGetAll(key.getBytes());
                if (CollectionUtils.isEmpty(hashValues)) {
                    return new HashMap(0);
                }

                Iterator entryIterator = hashValues.entrySet().iterator();

                while(true) {
                    Map.Entry entry;
                    String hashKey;
                    do {
                        if (!entryIterator.hasNext()) {
                            continue label42;
                        }

                        entry = (Map.Entry)entryIterator.next();
                        hashKey = new String((byte[])((byte[])entry.getKey()));
                    } while(!hashKey.equals(finalId) && !hashKey.equals(finalId + "#V"));

                    String hashValue = new String((byte[])((byte[])entry.getValue()));
                    JSONObject jsonValue = JSONObject.parseObject((String)JSON.parse(hashValue));
                    String actualValue = (String)jsonValue.get("value");
                    if (arrayIndex >= 0) {
                        List<String> valueList = StringUtils.str2List(actualValue, ",", true, true);
                        actualValue = org.apache.commons.collections4.CollectionUtils.isEmpty(valueList) ? "" : valueList.get(arrayIndex);
                    }

                    if (this.compareValues(operator, actualValue, triggerValue)) {
                        resultMap.put(key, actualValue);
                    }
                }
            }

            return resultMap;
        });
    }

    public Map<String, String> CheckMatchByProductId(Long productId, String operator, String id, String triggerValue) {
        Set<String> keys = this.getListKeyByPrefix("TSLV:" + productId);
        return (Map)this.redisTemplate.execute((RedisCallback) (connection) -> {
            Iterator<String> keyIterator = keys.iterator();
            HashMap<String, String> resultMap = new HashMap<>();

            while(keyIterator.hasNext()) {
                String key = keyIterator.next();
                String matchValue = this.CheckMatchByCacheKey(key, operator, id, triggerValue);
                if (!Objects.equals(matchValue, "")) {
                    resultMap.put(key, matchValue);
                }
            }

            return resultMap;
        });
    }

    public String CheckMatchByCacheKey(String cacheKey, String operator, String id, String triggerValue) {
        String cacheValue = (String)this.getCacheMapValue(cacheKey, id);
        Map<String, Object> valueMap = (Map)JSON.parseObject(cacheValue, Map.class);
        if (CollectionUtils.isEmpty(valueMap)) {
            return "";
        } else {
            Iterator<Map.Entry<String, Object>> entryIterator = valueMap.entrySet().iterator();

            while(entryIterator.hasNext()) {
                Map.Entry<String, Object> entry = entryIterator.next();
                String key = entry.getKey();
                if (key.equals("value")) {
                    String value = (String)entry.getValue();
                    value = value.replace("\"", "");
                    if (this.compareValues(operator, value, triggerValue)) {
                        return value;
                    }
                }
            }

            return "";
        }
    }

    public Map<String, String> getStringAllByKeys(Set<String> keys) {
        return (Map)this.redisTemplate.execute((RedisCallback) (connection) -> {
            Iterator<String> keyIterator = keys.iterator();
            HashMap<String, String> resultMap = new HashMap<>();

            while(keyIterator.hasNext()) {
                String key = keyIterator.next();
                byte[] valueBytes = connection.get(key.getBytes());
                if (valueBytes == null) {
                    return new HashMap(0);
                }

                String value = new String(valueBytes);
                resultMap.put(key, value);
            }

            return resultMap;
        });
    }

    public List<Object> scan(String query) {
        Set<String> scanResult = (Set)this.redisTemplate.execute((RedisCallback) (connection) -> {
            HashSet<String> results = new HashSet<>();
            Cursor<byte[]> cursor = connection.scan((new ScanOptions.ScanOptionsBuilder()).match("*" + query + "*").count(1000L).build());

            while(cursor.hasNext()) {
                results.add(new String(cursor.next()));
            }

            return results;
        });
        return new ArrayList(scanResult);
    }

    private boolean compareValues(String operator, String actualValue, String expectedValue) {
        boolean result = false;
        if ("".equals(actualValue)) {
            return result;
        } else {
            switch (operator) {
                case "=":
                    result = actualValue.equals(expectedValue);
                    break;
                case "!=":
                    result = !actualValue.equals(expectedValue);
                    break;
                case ">":
                    if (this.isNumeric(actualValue) && this.isNumeric(expectedValue)) {
                        result = Double.parseDouble(actualValue) > Double.parseDouble(expectedValue);
                    }
                    break;
                case "<":
                    if (this.isNumeric(actualValue) && this.isNumeric(expectedValue)) {
                        result = Double.parseDouble(actualValue) < Double.parseDouble(expectedValue);
                    }
                    break;
                case ">=":
                    if (this.isNumeric(actualValue) && this.isNumeric(expectedValue)) {
                        result = Double.parseDouble(actualValue) >= Double.parseDouble(expectedValue);
                    }
                    break;
                case "<=":
                    if (this.isNumeric(actualValue) && this.isNumeric(expectedValue)) {
                        result = Double.parseDouble(actualValue) <= Double.parseDouble(expectedValue);
                    }
                    break;
                case "contain":
                    result = actualValue.contains(expectedValue);
                    break;
                case "notcontain":
                    result = !actualValue.contains(expectedValue);
            }

            return result;
        }
    }

    private boolean isNumeric(String value) {
        Pattern pattern = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public void publish(Object message, String channel) {
        try {
            this.redisTemplate.convertAndSend(channel, message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public <T> void setHashValue(String key, String hKey, T value) {
        this.redisTemplate.opsForHash().put(key, hKey, value);
    }

    public String getStrCacheObject(String key) {
        return (String)this.stringRedisTemplate.opsForValue().get(key);
    }

    public void deleteStrObject(String key) {
        this.stringRedisTemplate.delete(key);
    }

    public void deleteStrHash(String key) {
        this.stringRedisTemplate.opsForHash().getOperations().delete(key);
    }

    public void delHashValue(String key, String hkey) {
        HashOperations hashOperations = this.redisTemplate.opsForHash();
        hashOperations.delete(key, new Object[]{hkey});
    }

    public <T> Object getStringHashValue(String key, String hKey) {
        return this.stringRedisTemplate.opsForHash().get(key, hKey);
    }

    public <T> void delStringHashValue(String key, String hKey) {
        this.stringRedisTemplate.opsForHash().delete(key, new Object[]{hKey});
    }

    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            rollbackFor = {Exception.class}
    )
    public synchronized String getCacheModbusTcpId(String serialNumber) {
        String cacheKey = RedisKeyBuilder.buildModbusTcpCacheKey(serialNumber);
        String currentValue = (String)this.stringRedisTemplate.opsForValue().get(cacheKey);
        long result = 0L;
        if (StringUtils.isBlank(currentValue)) {
            this.stringRedisTemplate.opsForValue().set(cacheKey, String.valueOf(0));
        } else {
            int intValue = Integer.parseInt(currentValue);
            if (intValue < 65535) {
                Long incrementedValue = this.stringRedisTemplate.opsForValue().increment(cacheKey);
                if (null != incrementedValue) {
                    result = incrementedValue;
                }
            } else {
                this.stringRedisTemplate.opsForValue().set(cacheKey, String.valueOf(0));
            }
        }

        return String.valueOf(result);
    }

    public synchronized void cacheModbusTcpData(String serialNumber, String id, String data) {
        String cacheKey = RedisKeyBuilder.buildModbusTcpRuntimeCacheKey(serialNumber);
        this.stringRedisTemplate.opsForHash().put(cacheKey, id, data);
        this.stringRedisTemplate.expire(cacheKey, 30L, TimeUnit.SECONDS);
    }
}
