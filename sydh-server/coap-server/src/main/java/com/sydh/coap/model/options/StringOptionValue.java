package com.sydh.coap.model.options;

import com.sydh.coap.model.CoapMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Locale;

@Slf4j
public class StringOptionValue extends OptionValue<String> {

    public StringOptionValue(int optionNumber, byte[] value) throws IllegalArgumentException {
        this(optionNumber, value, false);
    }

    public StringOptionValue(int optionNumber, byte[] value, boolean allowDefault) throws IllegalArgumentException {
        super(optionNumber, value, allowDefault);
        log.debug("String Option (#{}) created with value: '{}'.", optionNumber, this.getDecodedValue());
    }

    public StringOptionValue(int optionNumber, String value) throws IllegalArgumentException{

        this(optionNumber, optionNumber == Option.URI_HOST ?
                convertToByteArrayWithoutPercentEncoding(value.toLowerCase(Locale.ENGLISH)) :
                ((optionNumber == Option.URI_PATH || optionNumber == Option.URI_QUERY) ?
                            convertToByteArrayWithoutPercentEncoding(value) :
                                    value.getBytes(CoapMessage.CHARSET)));
    }

    @Override
    public String getDecodedValue() {
        return new String(value, CoapMessage.CHARSET);
    }


    @Override
    public int hashCode() {
        return getDecodedValue().hashCode();
    }


    @Override
    public boolean equals(Object object) {
        if (!(object instanceof StringOptionValue))
            return false;

        StringOptionValue other = (StringOptionValue) object;
        return Arrays.equals(this.getValue(), other.getValue());
    }

    public static byte[] convertToByteArrayWithoutPercentEncoding(String s) throws IllegalArgumentException{
        log.debug("With percent encoding: {}", s);
        ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes(CoapMessage.CHARSET));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int i;
        do {
            i = in.read();
            //-1 indicates end of stream
            if (i == -1) {
                break;
            }
            //0x25 = '%'
            if (i == 0x25) {
                //Character.digit returns the integer value encoded as in.read(). Since we know that percent encoding
                //uses bytes from 0x0 to 0xF (i.e. 0 to 15) the radix must be 16.
                int d1 = Character.digit(in.read(), 16);
                int d2 = Character.digit(in.read(), 16);

                if (d1 == -1 || d2 == -1) {
                    //Unexpected end of stream (at least one byte missing after '%')
                    throw new IllegalArgumentException("Invalid percent encoding in: " + s);
                }

                //Write decoded value to output stream (e.g. sequence [0x02, 0x00] results into byte 0x20
                out.write((d1 << 4) | d2);
            } else {
                out.write(i);
            }
        } while(true);

        byte[] result = out.toByteArray();
        log.debug("Without percent encoding: {}", new String(result, CoapMessage.CHARSET));
        return result;
    }
}
