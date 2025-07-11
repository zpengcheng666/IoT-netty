package com.sydh.coap.model;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.google.common.primitives.UnsignedLongs;

import java.util.Arrays;

public class Token implements Comparable<Token>{

    public static int MAX_LENGTH = 8;

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private final byte[] token;

    public Token(byte[] token) {
        if (token.length > 8)
            throw new IllegalArgumentException("Maximum token length is 8 (but given length was " + token.length + ")");

        this.token = token;
    }

    public byte[] getBytes() {
        return this.token;
    }

    @Override
    public String toString() {
        String tmp = bytesToHex(getBytes());

        if (tmp.length() == 0)
            return "<EMPTY>";
        else
            return "0x" + tmp;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Override
    public boolean equals(Object object) {
        if ((!(object instanceof Token)))
            return false;

        Token other = (Token) object;
        return Arrays.equals(this.getBytes(), other.getBytes());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(token);
    }

    @Override
    public int compareTo(Token other) {

        if (other.equals(this))
            return 0;
        if (this.getBytes().length < other.getBytes().length)
            return -1;
        if (this.getBytes().length > other.getBytes().length)
            return 1;

        return UnsignedLongs.compare(Longs.fromByteArray(Bytes.concat(this.getBytes(), new byte[8])),
                                     Longs.fromByteArray(Bytes.concat(other.getBytes(), new byte[8])));
    }
}
