package com.sydh.framework.handler;

import com.sydh.common.utils.StringUtils;
import org.redisson.api.NameMapper;


public class KeyPrefixHandler
        implements NameMapper {
    private final String keyPrefix;

    public KeyPrefixHandler(String keyPrefix) {
        this.keyPrefix = StringUtils.isBlank(keyPrefix) ? "" : (keyPrefix + ":");
    }

    @Override
    public String map(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        if (StringUtils.isNotBlank(this.keyPrefix) && !name.startsWith(this.keyPrefix)) {
            return this.keyPrefix + name;
        }
        return name;
    }


    @Override
    public String unmap(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        if (StringUtils.isNotBlank(this.keyPrefix) && name.startsWith(this.keyPrefix)) {
            return name.substring(this.keyPrefix.length());
        }
        return name;
    }
}
