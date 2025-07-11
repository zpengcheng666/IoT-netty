package com.sydh.iot.service;

import com.sydh.iot.model.login.AuthRequestWrap;

/**
 * AuthRequest简单工程类接口
 *
 * @author json
 * @date 2022-04-12
 */
public interface IAuthRequestFactory {

    AuthRequestWrap getAuthRequest(String source);
}
