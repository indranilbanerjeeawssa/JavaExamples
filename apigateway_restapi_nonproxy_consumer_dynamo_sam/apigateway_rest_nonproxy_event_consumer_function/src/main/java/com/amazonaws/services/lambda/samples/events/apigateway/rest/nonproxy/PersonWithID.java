package com.amazonaws.services.lambda.samples.events.apigateway.rest.nonproxy;

import java.util.Objects;
import com.fasterxml.jackson.databind.ObjectMapper;


public class PersonWithID {
	Person person;
	String id;
	/**
	 * 
	 */
	public PersonWithID() {
		super();
	}
	/**
	 * @param person
	 * @param id
	 */
	public PersonWithID(Person person, String id) {
		super();
		this.person = person;
		this.id = id;
	}
	/**
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}
	/**
	 * @param person the person to set
	 */
	public void setPerson(Person person) {
		this.person = person;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id, person);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonWithID other = (PersonWithID) obj;
		return Objects.equals(id, other.id) && Objects.equals(person, other.person);
	}
	
	@Override
	public String toString() {
		return "PersonWithID [person=" + person + ", id=" + id + "]";
	}
	public String toJson() {
		ObjectMapper om = new ObjectMapper();
		String returnString = "";
		try {
			returnString = om.writeValueAsString(this);
		} catch(Exception e) {
			returnString = this.toString();
		}
		return returnString;
	}
}
