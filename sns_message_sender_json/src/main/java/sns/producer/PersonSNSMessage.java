package sns.producer;

import java.util.Objects;

public class PersonSNSMessage {
	String messageKey;
	int messageNumber;
	Person person;
	public PersonSNSMessage() {
		super();
	}
	public PersonSNSMessage(String messageKey, int messageNumber, Person person) {
		super();
		this.messageKey = messageKey;
		this.messageNumber = messageNumber;
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
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	@Override
	public String toString() {
		return "PersonSNSMessage [messageKey=" + messageKey + ", messageNumber=" + messageNumber + ", person=" + person
				+ "]";
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
		PersonSNSMessage other = (PersonSNSMessage) obj;
		return Objects.equals(messageKey, other.messageKey) && messageNumber == other.messageNumber
				&& Objects.equals(person, other.person);
	}
	
}
