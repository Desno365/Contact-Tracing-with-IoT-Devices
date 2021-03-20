package it.polimi.middleware.project1.server.datastructures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the data structure that is able to contain the information of contacts between devices and also the timestamp of this contact.
 */
public class Contacts implements Serializable {

	private final HashMap<String, ContactsOfSingleDevice> contactsHashMap;

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
		final Type type = new TypeToken<HashMap<String, ContactsOfSingleDevice>>(){}.getType();
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
		for(Map.Entry<String, ContactsOfSingleDevice> entry : contactsHashMap.entrySet()) {
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
	public void addContact(String deviceId1, String deviceId2) {
		if(deviceId1.equals(deviceId2))
			throw new IllegalArgumentException("Device can't be in contact with itself. Device id: " + deviceId1 + ".");

		// Two directional contacts since the contact is bidirectional.
		final long timestamp = System.currentTimeMillis();
		addDirectionalContact(deviceId1, deviceId2, timestamp);
		addDirectionalContact(deviceId2, deviceId1, timestamp);
	}

	/**
	 * Get all the devices that had been in contact with affectedDeviceId, with also the timestamp of the contacts.
	 * @param affectedDeviceId the id of the device affected.
	 * @return a <code>Map</code> containing the devices that affectedDeviceId entered in contact with (and also the timestamp of the contact).
	 */
	public Map<String, Long> getTimestampOfContactsOfAffectedDevice(String affectedDeviceId) {
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

	private void addDirectionalContact(String deviceId, String otherDeviceId, long timestamp) {
		// Get container of contacts of "deviceId" (if it is absent it is created).
		ContactsOfSingleDevice contactsOfDevice = contactsHashMap.computeIfAbsent(deviceId, ContactsOfSingleDevice::new);

		// Add contact "deviceId"->"otherDeviceId" using the container of contacts of "deviceId".
		contactsOfDevice.addOrUpdateContact(otherDeviceId, timestamp);
	}
	//endregion
}
