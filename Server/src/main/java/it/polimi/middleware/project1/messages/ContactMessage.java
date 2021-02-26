package it.polimi.middleware.project1.messages;

import java.io.Serializable;

/**
 * Message representing a contact between devices with ids: <code>myId</code> and <code>otherId</code>.
 */
public class ContactMessage implements Serializable {

	public final int myId;
	public final int otherId;

	public ContactMessage(int myId, int otherId) {
		this.myId = myId;
		this.otherId = otherId;
	}
}
