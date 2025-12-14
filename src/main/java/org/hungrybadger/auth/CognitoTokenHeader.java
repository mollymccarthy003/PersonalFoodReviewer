package org.hungrybadger.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the header of a Cognito JWT token.
 * Contains information about the signing key and algorithm.
 */
public class CognitoTokenHeader{

    /** The key ID (kid) used to sign the JWT. */
	@JsonProperty("kid")
	private String kid;

    /** The algorithm (alg) used for signing the JWT. */
	@JsonProperty("alg")
	private String alg;

    /**
     * Gets the key ID.
     *
     * @return the key ID
     */
	public String getKid(){
		return kid;
	}

    /**
     * Gets the algorithm used.
     *
     * @return the algorithm
     */
	public String getAlg(){
		return alg;
	}
}