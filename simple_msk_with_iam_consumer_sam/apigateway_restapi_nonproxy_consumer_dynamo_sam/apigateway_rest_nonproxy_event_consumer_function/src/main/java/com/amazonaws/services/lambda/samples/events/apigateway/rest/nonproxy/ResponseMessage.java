package com.amazonaws.services.lambda.samples.events.apigateway.rest.nonproxy;

import java.util.Objects;

public class ResponseMessage {
	String customerID;
	String message;
	/**
	 * 
	 */
	public ResponseMessage() {
		super();
	}
	/**
	 * @param customerID
	 * @param message
	 */
	public ResponseMessage(String customerID, String message) {
		super();
		this.customerID = customerID;
		this.message = message;
	}
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public int hashCode() {
		return Objects.hash(customerID, message);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponseMessage other = (ResponseMessage) obj;
		return Objects.equals(customerID, other.customerID) && Objects.equals(message, other.message);
	}
	@Override
	public String toString() {
		return "ResponseMessage [customerID=" + customerID + ", message=" + message + "]";
	}
	
}
