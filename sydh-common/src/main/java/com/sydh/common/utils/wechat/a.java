package com.sydh.common.utils.wechat;

import java.util.ArrayList;

class a {
    ArrayList<Byte> dg = new ArrayList<>();

    public byte[] i() {
        byte[] arrayOfByte = new byte[this.dg.size()];
        for (byte b = 0; b < this.dg.size(); b++) {
            arrayOfByte[b] = ((Byte) this.dg.get(b)).byteValue();
        }
        return arrayOfByte;
    }

    public a g(byte[] paramArrayOfbyte) {
        for (byte b : paramArrayOfbyte) {
            this.dg.add(Byte.valueOf(b));
        }
        return this;
    }

    public int size() {
        return this.dg.size();
    }
}
