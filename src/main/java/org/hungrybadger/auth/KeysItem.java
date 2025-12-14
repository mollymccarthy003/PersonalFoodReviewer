package org.hungrybadger.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an individual JSON Web Key (JWK) item.
 * Includes all parameters required to verify JWTs.
 */
public class KeysItem{

    /** Key type (kty), e.g., "RSA". */
	@JsonProperty("kty")
	private String kty;

    /** Public exponent (e) for the key. */
	@JsonProperty("e")
	private String E;

    /** Use of the key (use), e.g., "sig" for signature. */
	@JsonProperty("use")
	private String use;

    /** Key ID (kid). */
	@JsonProperty("kid")
	private String kid;

    /** Algorithm (alg) used by this key. */
	@JsonProperty("alg")
	private String alg;

    /** Modulus (n) for the key. */
	@JsonProperty("n")
	private String N;

	public String getKty(){
		return kty;
	}

	public String getE(){
		return E;
	}

	public String getUse(){
		return use;
	}

	public String getKid(){
		return kid;
	}

	public String getAlg(){
		return alg;
	}

	public String getN(){
		return N;
	}
}