package it.polimi.middleware.project1;

import java.util.HashMap;
import java.util.Map;

public class ContactsOfSingleDevice {

	private final int deviceId;
	private final Map<Integer, Long> timestampOfContacts = new HashMap<>();

	public ContactsOfSingleDevice(int deviceId) {
		this.deviceId = deviceId;
	}

	public void addOrUpdateContact(final int otherDeviceId, final long currentTimestampMs) {
		if(deviceId == otherDeviceId)
			throw new IllegalArgumentException("Device can't be in contact with itself. Device id: " + deviceId + ".");
		timestampOfContacts.put(otherDeviceId, currentTimestampMs);
	}

	@Override
	public String toString() {
		return "Contacts of device " + deviceId + ": " + timestampOfContacts.toString() + ".";
	}
}
