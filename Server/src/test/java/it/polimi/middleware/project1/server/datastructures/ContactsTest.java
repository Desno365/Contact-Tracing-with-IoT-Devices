package it.polimi.middleware.project1.server.datastructures;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ContactsTest {

	Contacts testContacts;

	@BeforeEach
	void setUp() {
		// Add sample contacts: 1001<->1002; 1002<->1003.
		testContacts = new Contacts();
		testContacts.addContact(1001, 1002);
		testContacts.addContact(1002, 1003);
	}

	@AfterEach
	void tearDown() {
		testContacts = null;
	}

	@Test
	void toJson_and_fromJson() {
		final String jsonString = testContacts.toJson();
		final Contacts testContactsCopy = new Contacts(jsonString);
		final String jsonStringCopy = testContactsCopy.toJson();
		assertEquals(jsonString, jsonStringCopy);
	}

	@Test
	void addContact_and_getTimestampOfContactsOfAffectedDevice_device1001() {
		// 1001 affected.
		Map<Integer, Long> timestampOfContact1001 = testContacts.getTimestampOfContactsOfAffectedDevice(1001);

		// Contacts of affected device doesn't contain device affected itself.
		assertFalse(timestampOfContact1001.containsKey(1001));

		// Contacts of affected device contains devices entered in contact.
		assertTrue(timestampOfContact1001.containsKey(1002));

		// Contacts of affected device does not contain devices not entered in contact.
		assertFalse(timestampOfContact1001.containsKey(1003));
	}

	@Test
	void addContact_and_getTimestampOfContactsOfAffectedDevice_device1002() {
		// 1002 affected.
		Map<Integer, Long> timestampOfContact1002 = testContacts.getTimestampOfContactsOfAffectedDevice(1002);

		// Contacts of affected device doesn't contain device affected itself.
		assertFalse(timestampOfContact1002.containsKey(1002));

		// Contacts of affected device contains devices entered in contact.
		assertTrue(timestampOfContact1002.containsKey(1001));
		assertTrue(timestampOfContact1002.containsKey(1003));
	}

	@Test
	void addContact_and_getTimestampOfContactsOfAffectedDevice_device1003() {
		// 1003 affected.
		Map<Integer, Long> timestampOfContact1003 = testContacts.getTimestampOfContactsOfAffectedDevice(1003);

		// Contacts of affected device doesn't contain device affected itself.
		assertFalse(timestampOfContact1003.containsKey(1003));

		// Contacts of affected device contains devices entered in contact.
		assertTrue(timestampOfContact1003.containsKey(1002));

		// Contacts of affected device does not contain devices not entered in contact.
		assertFalse(timestampOfContact1003.containsKey(1001));
	}

	@Test
	void addContact_and_getTimestampOfContactsOfAffectedDevice_device1004() {
		// 1004 affected (device didn't have any contact)
		Map<Integer, Long> timestampOfContact1004 = testContacts.getTimestampOfContactsOfAffectedDevice(1004);

		// Contacts of affected device doesn't contain device affected itself.
		assertFalse(timestampOfContact1004.containsKey(1004));

		// Size of contacts of affected device is zero
		assertEquals(0, timestampOfContact1004.size());
	}

	@Test
	void addContact_contactWithItself() {
		try {
			testContacts.addContact(1005, 1005);
			throw new RuntimeException("Should have thrown IllegalArgumentException.");
		} catch(IllegalArgumentException e) {
			// 1005 affected (device shouldn't had any contact)
			Map<Integer, Long> timestampOfContact1005 = testContacts.getTimestampOfContactsOfAffectedDevice(1005);

			// Contacts of affected device doesn't contain device affected itself.
			assertFalse(timestampOfContact1005.containsKey(1005));

			// Size of contacts of affected device is zero
			assertEquals(0, timestampOfContact1005.size());
		}
	}

	@Test
	void printContacts() {
		testContacts.printContacts();
	}
}