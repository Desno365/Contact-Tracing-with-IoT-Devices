package it.polimi.middleware.project1.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Contacts implements Serializable {

	private HashMap<Integer, ContactsOfSingleDevice> contactsHashMap;

	/**
	 * Constructs an empty contacts structure.
	 */
	public Contacts() {
		contactsHashMap = new HashMap<>();
	}

	/**
	 * Constructs a contacts structure from a json string.
	 * @param jsonString the json string that contains the contacts.
	 */
	public Contacts(String jsonString) {
		final Gson gson = new Gson();
		final Type type = new TypeToken<HashMap<Integer, ContactsOfSingleDevice>>(){}.getType();
		contactsHashMap = gson.fromJson(jsonString, type);
	}


	// ##############################################################
	//region Public methods
	// ###############################

	/**
	 * Convert the Contacts structure to a json string.
	 * @return the json string that contains the contacts.
	 */
	public String toJson() {
		final Gson gson = new Gson();
		return gson.toJson(contactsHashMap);
	}

	/**
	 * Prints the content of the whole contacts structure.
	 */
	public void printContacts() {
		System.out.println("######### CURRENT CONTACTS #########");
		for(Map.Entry<Integer, ContactsOfSingleDevice> entry : contactsHashMap.entrySet()) {
			ContactsOfSingleDevice contactsOfSingleDevice = entry.getValue();
			System.out.println(contactsOfSingleDevice.toString());
		}
		System.out.println("####################################");
	}

	/**
	 * Add a contact between deviceId1 and deviceId2 to this contacts structure. The operation is commutative.
	 * @param deviceId1 the device that entered in contact with deviceId2.
	 * @param deviceId2 the device that entered in contact with deviceId1.
	 */
	public void addContact(int deviceId1, int deviceId2) {
		if(deviceId1 == deviceId2)
			throw new IllegalArgumentException("Device can't be in contact with itself. Device id: " + deviceId1 + ".");

		// Two directional contacts since the contact is bidirectional.
		final long timestamp = System.currentTimeMillis();
		addDirectionalContact(deviceId1, deviceId2, timestamp);
		addDirectionalContact(deviceId2, deviceId1, timestamp);
	}

	/**
	 * Get all the devices that had been in contact with affectedDeviceId, with also the timestamp of the contacts.
	 * @param affectedDeviceId the id of the device affected.
	 * @return an hashmap containing the devices that affectedDeviceId entered in contact with (and also the timestamp of the contact).
	 */
	public Map<Integer, Long> getTimestampOfContactsOfAffectedDevice(int affectedDeviceId) {
		ContactsOfSingleDevice contactsOfSingleDevice = contactsHashMap.get(affectedDeviceId);
		if(contactsOfSingleDevice != null)
			return contactsOfSingleDevice.getCopyOfTimestampOfContacts();
		else
			return new HashMap<>();
	}
	//endregion


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
