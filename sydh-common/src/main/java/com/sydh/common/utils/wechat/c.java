package com.sydh.common.utils.wechat;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


class c {
    public static Object[] r(String paramString) throws AesException {
        Object[] arrayOfObject = new Object[3];
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

            String str = null;


            str = "http://apache.org/xml/features/disallow-doctype-decl";
            documentBuilderFactory.setFeature(str, true);


            str = "http://xml.org/sax/features/external-general-entities";
            documentBuilderFactory.setFeature(str, false);


            str = "http://xml.org/sax/features/external-parameter-entities";
            documentBuilderFactory.setFeature(str, false);


            str = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            documentBuilderFactory.setFeature(str, false);


            documentBuilderFactory.setXIncludeAware(false);
            documentBuilderFactory.setExpandEntityReferences(false);


            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            StringReader stringReader = new StringReader(paramString);
            InputSource inputSource = new InputSource(stringReader);
            Document document = documentBuilder.parse(inputSource);

            Element element = document.getDocumentElement();
            NodeList nodeList = element.getElementsByTagName("Encrypt");
            arrayOfObject[0] = Integer.valueOf(0);
            arrayOfObject[1] = nodeList.item(0).getTextContent();
            return arrayOfObject;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new AesException(-40002);
        }
    }


    public static String a(String paramString1, String paramString2, String paramString3, String paramString4) {
        String str = "<xml>\n<Encrypt><![CDATA[%1$s]]></Encrypt>\n<MsgSignature><![CDATA[%2$s]]></MsgSignature>\n<TimeStamp>%3$s</TimeStamp>\n<Nonce><![CDATA[%4$s]]></Nonce>\n</xml>";


        return String.format(str, new Object[]{paramString1, paramString2, paramString3, paramString4});
    }
}
