package com.kevinkoech.paympesa.models;

import com.google.gson.annotations.SerializedName;

public class LNMPaymentReponse{

	@SerializedName("MerchantRequestID")
	private String merchantRequestID;

	@SerializedName("ResponseCode")
	private String responseCode;

	@SerializedName("CustomerMessage")
	private String customerMessage;

	@SerializedName("CheckoutRequestID")
	private String checkoutRequestID;

	@SerializedName("ResponseDescription")
	private String responseDescription;

	public String getMerchantRequestID(){
		return merchantRequestID;
	}

	public String getResponseCode(){
		return responseCode;
	}

	public String getCustomerMessage(){
		return customerMessage;
	}

	public String getCheckoutRequestID(){
		return checkoutRequestID;
	}

	public String getResponseDescription(){
		return responseDescription;
	}

	@Override
 	public String toString(){
		return 
			"LNMPaymentReponse{" + 
			"merchantRequestID = '" + merchantRequestID + '\'' + 
			",responseCode = '" + responseCode + '\'' + 
			",customerMessage = '" + customerMessage + '\'' + 
			",checkoutRequestID = '" + checkoutRequestID + '\'' + 
			",responseDescription = '" + responseDescription + '\'' + 
			"}";
		}
}