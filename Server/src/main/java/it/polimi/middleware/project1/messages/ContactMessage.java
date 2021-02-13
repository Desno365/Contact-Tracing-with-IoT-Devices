package it.polimi.middleware.project1.messages;

import java.io.Serializable;

public class ContactMessage implements Serializable {

	private final int myId;
	private final int otherId;

	public int getMyId() {
		return myId;
	}

	public int getOtherId() {
		return otherId;
	}

	public ContactMessage(int myId, int otherId) {
		this.myId = myId;
		this.otherId = otherId;
	}
}
