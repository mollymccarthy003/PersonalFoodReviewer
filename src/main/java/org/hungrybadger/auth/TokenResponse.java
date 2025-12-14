package org.hungrybadger.auth;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents the response from Cognito token endpoint.
 * Contains access, refresh, and ID tokens with metadata.
 */
public class TokenResponse {

    /** Access token string. */
	@JsonProperty("access_token")
	private String accessToken;

    /** Refresh token string. */
	@JsonProperty("refresh_token")
	private String refreshToken;

    /** ID token string. */
	@JsonProperty("id_token")
	private String idToken;

    /** Token type, usually "Bearer". */
	@JsonProperty("token_type")
	private String tokenType;

    /** Expiration time in seconds. */
	@JsonProperty("expires_in")
	private int expiresIn;

	public String getAccessToken(){
		return accessToken;
	}

	public String getRefreshToken(){
		return refreshToken;
	}

	public String getIdToken(){
		return idToken;
	}

	public String getTokenType(){
		return tokenType;
	}

	public int getExpiresIn(){
		return expiresIn;
	}
}