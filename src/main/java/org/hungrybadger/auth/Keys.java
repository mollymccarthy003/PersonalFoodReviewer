package org.hungrybadger.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a collection of JWK (JSON Web Key) objects.
 */
public class Keys{


    /** The list of JWK keys. */
	@JsonProperty("keys")
	private List<KeysItem> keys;

    /**
     * Gets the list of keys.
     *
     * @return the keys
     */
	public List<KeysItem> getKeys(){
		return keys;
	}
}