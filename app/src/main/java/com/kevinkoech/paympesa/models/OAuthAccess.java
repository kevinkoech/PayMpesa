package com.kevinkoech.paympesa.models;

import com.google.gson.annotations.SerializedName;

public class OAuthAccess{

	@SerializedName("access_token")
	private String accessToken;

	@SerializedName("expires_in")
	private String expiresIn;

	public String getAccessToken(){
		return accessToken;
	}

	public String getExpiresIn(){
		return expiresIn;
	}

	@Override
 	public String toString(){
		return 
			"OAuthAccess{" + 
			"access_token = '" + accessToken + '\'' + 
			",expires_in = '" + expiresIn + '\'' + 
			"}";
		}
}