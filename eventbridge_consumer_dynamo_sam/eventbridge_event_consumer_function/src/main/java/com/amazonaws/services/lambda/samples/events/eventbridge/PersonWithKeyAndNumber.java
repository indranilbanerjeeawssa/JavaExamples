package com.amazonaws.services.lambda.samples.events.eventbridge;

import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class PersonWithKeyAndNumber {
	Person person;
	String messageKey;
	int messageNumber;
	/**
	 * 
	 */
	public PersonWithKeyAndNumber() {
		super();
	}
	/**
	 * @param person
	 * @param messageKey
	 * @param messageNumber
	 */
	public PersonWithKeyAndNumber(Person person, String messageKey, int messageNumber) {
		super();
		this.person = person;
		this.messageKey = messageKey;
		this.messageNumber = messageNumber;
	}
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	public String getMessageKey() {
		return messageKey;
	}
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
	public int getMessageNumber() {
		return messageNumber;
	}
	public void setMessageNumber(int messageNumber) {
		this.messageNumber = messageNumber;
	}
	@Override
	public int hashCode() {
		return Objects.hash(messageKey, messageNumber, person);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonWithKeyAndNumber other = (PersonWithKeyAndNumber) obj;
		return Objects.equals(messageKey, other.messageKey) && messageNumber == other.messageNumber
				&& Objects.equals(person, other.person);
	}
	@Override
	public String toString() {
		return "PersonWithKeyAndNumber [person=" + person + ", messageKey=" + messageKey + ", messageNumber="
				+ messageNumber + "]";
	}
	public String toJson() {
		ObjectMapper objectMapper = new ObjectMapper().registerModule(new JodaModule());
		String returnString = this.toString();
		try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return returnString;
		}
	}
}
