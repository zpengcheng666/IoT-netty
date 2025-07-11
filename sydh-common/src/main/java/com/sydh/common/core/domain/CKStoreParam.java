package com.sydh.common.core.domain;

import de.schlichtherle.license.AbstractKeyStoreParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class CKStoreParam extends AbstractKeyStoreParam {
    private String storePath;
    private String storePwd;
    private String alias;
    private String keyPwd;

    public CKStoreParam(Class clazz, String resource, String alias, String storePwd, String keyPwd) {
        super(clazz, resource);
        this.storePath = resource;
        this.alias = alias;
        this.storePwd = storePwd;
        this.keyPwd = keyPwd;
    }

    public String getAlias() {
        return this.alias;
    }

    public String getStorePwd() {
        return this.storePwd;
    }

    public String getKeyPwd() {
        return this.keyPwd;
    }

    public InputStream getStream() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(this.storePath));
        if (null == fileInputStream) {
            throw new FileNotFoundException(this.storePath);
        }
        return fileInputStream;
    }
}
