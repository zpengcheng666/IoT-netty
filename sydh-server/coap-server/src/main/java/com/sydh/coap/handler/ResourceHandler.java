package com.sydh.coap.handler;

import com.sydh.coap.model.CoapMessage;
import com.sydh.coap.model.CoapResponse;

public interface ResourceHandler {
    /**
     * The path served by this resource handler.
     */
    public String getPath();

    /**
     * Detailed title for this path (or <code>null</code>). See http://datatracker.ietf.org/doc/rfc6690/
     */
    public String getTitle();

    /**
     * Interface name of this resource (or <code>null</code>), can be an URL to a WADL file. See
     * http://datatracker.ietf.org/doc/rfc6690/
     */
    public String getInterface();

    /**
     * Resource type (or <code>null</code>). See http://datatracker.ietf.org/doc/rfc6690/
     */
    public String getResourceType();

    /**
     * Generate the response for this request.
     *
     * @param request the request to serve
     * @return the response
     */
    public CoapResponse handle(CoapMessage request);
}

