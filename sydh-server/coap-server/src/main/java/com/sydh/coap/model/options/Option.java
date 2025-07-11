
package com.sydh.coap.model.options;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;

import static com.sydh.coap.model.MessageCode.*;
import static com.sydh.coap.model.options.Option.Occurence.*;

public abstract class Option {

    public enum Occurence {
        NONE, ONCE, MULTIPLE
    }

    /**
     * Corresponds to option number -1  = unknown)
     */
    public static final int UNKNOWN = -1;

    /**
     * Corresponds to option number 1
     */
    public static final int IF_MATCH = 1;

    /**
     * Corresponds to option number 3
     */
    public static final int URI_HOST = 3;

    /**
     * Corresponds to option number 4
     */
    public static final int ETAG = 4;

    /**
     * Corresponds to option number 5
     */
    public static final int IF_NONE_MATCH = 5;

    /**
     * Corresponds to option number 6
     */
    public static final int OBSERVE = 6;

    /**
     * Corresponds to option number 7
     */
    public static final int URI_PORT = 7;

    /**
     * Corresponds to option number 8
     */
    public static final int LOCATION_PATH = 8;

    /**
     * Corresponds to option number 11
     */
    public static final int URI_PATH = 11;

    /**
     * Corresponds to option number 12
     */
    public static final int CONTENT_FORMAT = 12;

    /**
     * Corresponds to option number 14
     */
    public static final int MAX_AGE = 14;

    /**
     * Corresponds to option number 15
     */
    public static final int URI_QUERY = 15;

    /**
     * Corresponds to option number 17
     */
    public static final int ACCEPT = 17;

    /**
     * Corresponds to option number 20
     */
    public static final int LOCATION_QUERY = 20;

    /**
     * Corresponds to option number 23
     */
    public static final int BLOCK_2 = 23;

    /**
     * Corresponds to option number 27
     */
    public static final int BLOCK_1 = 27;

    /**
     * Corresponds to option number 28
     */
    public static final int SIZE_2 = 28;

    /**
     * Corresponds to option number 35
     */
    public static final int PROXY_URI = 35;

    /**
     * Corresponds to option number 39
     */
    public static final int PROXY_SCHEME = 39;

    /**
     * Corresponds to option number 60
     */
    public static final int SIZE_1 = 60;

    /**
     * Corresponds to option number 124
     */
    public static final int ENDPOINT_ID_1 = 124;

    /**
     * Corresponds to option number 189
     */
    public static final int ENDPOINT_ID_2 = 189;

    private static HashMap<Integer, String> OPTIONS = new HashMap<>();
    static {
        OPTIONS.putAll(ImmutableMap.<Integer, String>builder()
            .put(IF_MATCH,        "IF MATCH (" + IF_MATCH + ")")
            .put(URI_HOST,        "URI HOST (" + URI_HOST + ")")
            .put(ETAG,            "ETAG (" + ETAG + ")")
            .put(IF_NONE_MATCH,   "IF NONE MATCH (" + IF_NONE_MATCH + ")")
            .put(OBSERVE,         "OBSERVE (" + OBSERVE + ")")
            .put(URI_PORT,        "URI PORT (" + URI_PORT + ")")
            .put(LOCATION_PATH,   "LOCATION PATH (" + LOCATION_PATH + ")")
            .put(URI_PATH,        "URI PATH (" + URI_PATH + ")")
            .put(CONTENT_FORMAT,  "CONTENT FORMAT (" + CONTENT_FORMAT + ")")
            .put(MAX_AGE,         "MAX AGE (" + MAX_AGE + ")")
            .put(URI_QUERY,       "URI QUERY (" + URI_QUERY + ")")
            .put(ACCEPT,          "ACCEPT (" + ACCEPT + ")")
            .put(LOCATION_QUERY,  "LOCATION QUERY (" + LOCATION_QUERY + ")")
            .put(BLOCK_2,         "BLOCK 2 (" + BLOCK_2 + ")")
            .put(BLOCK_1,         "BLOCK 1 (" + BLOCK_1 + ")")
            .put(SIZE_2,          "SIZE 2 (" + SIZE_2 + ")")
            .put(PROXY_URI,       "PROXY URI (" + PROXY_URI + ")")
            .put(PROXY_SCHEME,    "PROXY SCHEME (" + PROXY_SCHEME + ")")
            .put(SIZE_1,          "SIZE 1 (" + SIZE_1 + ")")
            .put(ENDPOINT_ID_1,   "ENDPOINT ID 1 (" + ENDPOINT_ID_1 + ")")
            .put(ENDPOINT_ID_2,   "ENDPOINT ID 2 (" + ENDPOINT_ID_2 + ")")
            .build()
        );
    }

    public static String asString(int optionNumber) {
        String result = OPTIONS.get(optionNumber);
        return result == null ? "UNKOWN (" + optionNumber + ")" : result;
    }

    private static HashMultimap<Integer, Integer> MUTUAL_EXCLUSIONS = HashMultimap.create();

    static {
        MUTUAL_EXCLUSIONS.put(URI_HOST, PROXY_URI);
        MUTUAL_EXCLUSIONS.put(PROXY_URI, URI_HOST);

        MUTUAL_EXCLUSIONS.put(URI_PORT, PROXY_URI);
        MUTUAL_EXCLUSIONS.put(PROXY_URI, URI_PORT);

        MUTUAL_EXCLUSIONS.put(URI_PATH, PROXY_URI);
        MUTUAL_EXCLUSIONS.put(PROXY_URI, URI_PATH);

        MUTUAL_EXCLUSIONS.put(URI_QUERY, PROXY_URI);
        MUTUAL_EXCLUSIONS.put(PROXY_URI, URI_QUERY);

        MUTUAL_EXCLUSIONS.put(PROXY_SCHEME, PROXY_URI);
        MUTUAL_EXCLUSIONS.put(PROXY_URI, PROXY_SCHEME);
    }

    public static boolean mutuallyExcludes(int firstOptionNumber, int secondOptionNumber) {
        return MUTUAL_EXCLUSIONS.get(firstOptionNumber).contains(secondOptionNumber);
    }


    private static final HashBasedTable<Integer, Integer, Option.Occurence> OCCURENCE_CONSTRAINTS
            = HashBasedTable.create();

    static {
        // GET Requests
        OCCURENCE_CONSTRAINTS.row(GET).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(URI_HOST, ONCE)
                        .put(URI_PORT, ONCE)
                        .put(URI_PATH, MULTIPLE)
                        .put(URI_QUERY, MULTIPLE)
                        .put(PROXY_URI, ONCE)
                        .put(PROXY_SCHEME, ONCE)
                        .put(ACCEPT, MULTIPLE)
                        .put(ETAG, MULTIPLE)
                        .put(OBSERVE, ONCE)
                        .put(BLOCK_2, ONCE)
                        .put(SIZE_2, ONCE)
                        .put(ENDPOINT_ID_1, ONCE)
                        .build()
        );

        // POST Requests
        OCCURENCE_CONSTRAINTS.row(POST).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(URI_HOST, ONCE)
                        .put(URI_PORT, ONCE)
                        .put(URI_PATH, MULTIPLE)
                        .put(URI_QUERY, MULTIPLE)
                        .put(ACCEPT, MULTIPLE)
                        .put(PROXY_URI, ONCE)
                        .put(PROXY_SCHEME, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(BLOCK_2, ONCE)
                        .put(BLOCK_1, ONCE)
                        .put(SIZE_2, ONCE)
                        .put(SIZE_1, ONCE)
                        .put(ENDPOINT_ID_1, ONCE)
                        .build()
        );

        // PUT Requests
        OCCURENCE_CONSTRAINTS.row(PUT).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(URI_HOST, ONCE)
                        .put(URI_PORT, ONCE)
                        .put(URI_PATH, MULTIPLE)
                        .put(URI_QUERY, MULTIPLE)
                        .put(ACCEPT, MULTIPLE)
                        .put(PROXY_URI, ONCE)
                        .put(PROXY_SCHEME, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(IF_MATCH, ONCE)
                        .put(IF_NONE_MATCH, ONCE)
                        .put(BLOCK_2, ONCE)
                        .put(BLOCK_1, ONCE)
                        .put(SIZE_2, ONCE)
                        .put(SIZE_1, ONCE)
                        .put(ENDPOINT_ID_1, ONCE)
                        .build()
        );

        // DELETE Requests
        OCCURENCE_CONSTRAINTS.row(DELETE).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(URI_HOST, ONCE)
                        .put(URI_PORT, ONCE)
                        .put(URI_PATH, MULTIPLE)
                        .put(URI_QUERY, MULTIPLE)
                        .put(PROXY_URI, ONCE)
                        .put(PROXY_SCHEME, ONCE)
                        .put(ENDPOINT_ID_1, ONCE)
                        .build()
        );

        //Response success (2.x)
        OCCURENCE_CONSTRAINTS.row(CREATED_201).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(ETAG, ONCE)
                        .put(OBSERVE, ONCE)
                        .put(LOCATION_PATH, MULTIPLE)
                        .put(LOCATION_QUERY, MULTIPLE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(BLOCK_2, ONCE)
                        .put(BLOCK_1, ONCE)
                        .put(SIZE_2, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(DELETED_202).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(CONTENT_FORMAT, ONCE)
                        .put(BLOCK_2, ONCE)
                        .put(BLOCK_1, ONCE)
                        .put(SIZE_2, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(VALID_203).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(OBSERVE, ONCE)
                        .put(ETAG, ONCE)
                        .put(MAX_AGE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(ENDPOINT_ID_1, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(CHANGED_204).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(ETAG, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(BLOCK_2, ONCE)
                        .put(BLOCK_1, ONCE)
                        .put(SIZE_2, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(CONTENT_205).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(OBSERVE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(MAX_AGE, ONCE)
                        .put(ETAG, ONCE)
                        .put(BLOCK_2, ONCE)
                        .put(BLOCK_1, ONCE)
                        .put(SIZE_2, ONCE)
                        .put(ENDPOINT_ID_1, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(CONTINUE_231).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(BLOCK_1, ONCE)
                        .build()
        );

        // Client ERROR Responses (4.x)
        OCCURENCE_CONSTRAINTS.row(BAD_REQUEST_400).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(MAX_AGE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(UNAUTHORIZED_401).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(MAX_AGE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(BAD_OPTION_402).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(MAX_AGE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(FORBIDDEN_403).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(MAX_AGE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(NOT_FOUND_404).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(MAX_AGE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(METHOD_NOT_ALLOWED_405).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(MAX_AGE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(NOT_ACCEPTABLE_406).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(MAX_AGE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(REQUEST_ENTITY_INCOMPLETE_408).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(CONTENT_FORMAT, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(PRECONDITION_FAILED_412).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(MAX_AGE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(REQUEST_ENTITY_TOO_LARGE_413).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(MAX_AGE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(BLOCK_1, ONCE)
                        .put(SIZE_1, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(UNSUPPORTED_CONTENT_FORMAT_415).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(MAX_AGE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        // Server ERROR Responses ( 5.x )
        OCCURENCE_CONSTRAINTS.row(INTERNAL_SERVER_ERROR_500).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(MAX_AGE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(NOT_IMPLEMENTED_501).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(MAX_AGE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(BAD_GATEWAY_502).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(MAX_AGE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(GATEWAY_TIMEOUT_504).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(MAX_AGE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );

        OCCURENCE_CONSTRAINTS.row(PROXYING_NOT_SUPPORTED_505).putAll(ImmutableMap.<Integer, Occurence>builder()
                        .put(MAX_AGE, ONCE)
                        .put(CONTENT_FORMAT, ONCE)
                        .put(ENDPOINT_ID_2, ONCE)
                        .build()
        );
    }

    public static Occurence getPermittedOccurrence(int optionNumber, int messageCode) {
        Occurence result = OCCURENCE_CONSTRAINTS.get(messageCode, optionNumber);
        return result == null ? NONE : result;
    }

    public static boolean isCritical(int optionNumber) {
        return (optionNumber & 1) == 1;
    }

    public static boolean isSafe(int optionNumber) {
        return !((optionNumber & 2) == 2);
    }

    public static boolean isCacheKey(int optionNumber) {
        return !((optionNumber & 0x1e) == 0x1c);
    }
}
