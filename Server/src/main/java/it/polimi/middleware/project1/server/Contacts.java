package it.polimi.middleware.project1.server;

import java.util.HashMap;
import java.util.Map;

public class Contacts {

	private final HashMap<Integer, ContactsOfSingleDevice> contactsHashMap = new HashMap<>();

	public void printContacts() {
		System.out.println("######### CURRENT CONTACTS #########");
		for(Map.Entry<Integer, ContactsOfSingleDevice> entry : contactsHashMap.entrySet()) {
			ContactsOfSingleDevice contactsOfSingleDevice = entry.getValue();
			System.out.println(contactsOfSingleDevice.toString());
		}
		System.out.println("####################################");
	}

	public void addContact(int deviceId1, int deviceId2) {
		if(deviceId1 == deviceId2)
			throw new IllegalArgumentException("Device can't be in contact with itself. Device id: " + deviceId1 + ".");

		// Two directional contacts since the contact is bidirectional.
		final long timestamp = System.currentTimeMillis();
		addDirectionalContact(deviceId1, deviceId2, timestamp);
		addDirectionalContact(deviceId2, deviceId1, timestamp);
	}

	public Map<Integer, Long> getTimestampOfContactsOfSingleDevice(int affectedDeviceId) {
		ContactsOfSingleDevice contactsOfSingleDevice = contactsHashMap.get(affectedDeviceId);
		if(contactsOfSingleDevice != null)
			return contactsOfSingleDevice.getCopyOfTimestampOfContacts();
		else
			return new HashMap<>();
	}


	// ##############################################################
	//region Private methods
	// ###############################

	private void addDirectionalContact(int deviceId, int otherDeviceId, long timestamp) {
		// Get container of contacts of "deviceId" (if it is absent it is created).
		ContactsOfSingleDevice contactsOfDevice = contactsHashMap.computeIfAbsent(deviceId, ContactsOfSingleDevice::new);

		// Add contact "deviceId"->"otherDeviceId" using the container of contacts of "deviceId".
		contactsOfDevice.addOrUpdateContact(otherDeviceId, timestamp);
	}
	//endregion
}
