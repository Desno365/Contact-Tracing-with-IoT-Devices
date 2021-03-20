package it.polimi.middleware.project1.server.datastructures;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ContactsOfSingleDevice implements Serializable {

	private final String deviceId;
	private final Map<String, Long> timestampOfContacts = new HashMap<>();

	public ContactsOfSingleDevice(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String toString() {
		return "Contacts of device \"" + deviceId + "\": " + timestampOfContacts.toString() + ".";
	}

	/**
	 * Adds or update the timestamp of the contact between this single device and another device represented by <code>otherDeviceId</code>.
	 * @param otherDeviceId the device that entered in contact with this single device.
	 * @param timestampOfContact the timestamp of the contact.
	 */
	public void addOrUpdateContact(final String otherDeviceId, final long timestampOfContact) {
		if(deviceId.equals(otherDeviceId))
			throw new IllegalArgumentException("Device can't be in contact with itself. Device id: " + deviceId + ".");
		timestampOfContacts.put(otherDeviceId, timestampOfContact);
	}

	/**
	 * Returns a copy of the contacts of this single device.
	 * @return a <code>Map</code> containing the devices that affectedDeviceId entered in contact with (and also the timestamp of the contact).
	 */
	public Map<String, Long> getCopyOfTimestampOfContacts() {
		return new HashMap<>(timestampOfContacts);
	}
}
