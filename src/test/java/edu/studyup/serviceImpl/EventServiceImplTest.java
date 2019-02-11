package edu.studyup.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.studyup.entity.Event;
import edu.studyup.entity.Location;
import edu.studyup.entity.Student;
import edu.studyup.util.DataStorage;
import edu.studyup.util.StudyUpException;

class EventServiceImplTest {

	EventServiceImpl eventServiceImpl;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		eventServiceImpl = new EventServiceImpl();
		//Create Student
		Student student = new Student();
		student.setFirstName("John");
		student.setLastName("Doe");
		student.setEmail("JohnDoe@email.com");
		student.setId(1);
		
		//Create Event1
		Event event = new Event();
		event.setEventID(1);
		event.setDate(new Date());
		event.setName("Event 1");
		Location location = new Location(-122, 37);
		event.setLocation(location);
		List<Student> eventStudents = new ArrayList<>();
		eventStudents.add(student);
		event.setStudents(eventStudents);
		
		DataStorage.eventData.put(event.getEventID(), event);
	}

	@AfterEach
	void tearDown() throws Exception {
		DataStorage.eventData.clear();
	}

	/**
	 * Test that we can properly update an Event's name
	 * @throws StudyUpException
	 */
	@Test
	void testUpdateEventName_GoodCase() throws StudyUpException {
		int eventID = 1;
		eventServiceImpl.updateEventName(eventID, "Renamed Event 1");
		assertEquals("Renamed Event 1",
						DataStorage.eventData.get(eventID).getName());
	}
	
	/**
	 * Test that we can store names up to 20 characters long
	 * @throws StudyUpException
	 */
	@Test
	void testUpdateEventName_TwentyCharacterName() throws StudyUpException {
		int eventID = 1;
		eventServiceImpl.updateEventName(eventID, "A 20 character name!");
		assertEquals("A 20 character name!",
						DataStorage.eventData.get(eventID).getName());
	}
	
	/**
	 * Test that we cannot store names greater than 20 characters
	 */
	@Test
	void testUpdateEventName_TwentyFiveCharacter() {
		int eventID = 1;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID,
												"A 25 character name!!!!!!");
		});
	}
	
	/**
	 * Test that passing an invalid event ID to updateEventName throws an
	 * exception
	 */
	@Test
	void testUpdateEventName_WrongEventID() {
		int eventID = 3;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Renamed Event 3");
		  });
	}
	
	/**
	 * Test that we can add 2 students to an Event
	 * @throws StudyUpException
	 */
	@Test
	void testAddStudent_AddSecondStudent() throws StudyUpException {
		int eventID = 1;
		//Create Student
		Student student = new Student();
		student.setFirstName("Forrest");
		student.setLastName("Gump");
		student.setEmail("fgump@icloud.com");
		student.setId(2);
		eventServiceImpl.addStudentToEvent(student, eventID);
		
		Event event = DataStorage.eventData.get(1);
		List<Student> students = event.getStudents();
		assert(students.get(1).equals(student));
	}
	
	/**
	 * Test that attempting to add a third student throws an exception
	 * @throws StudyUpException
	 */
	@Test
	void testAddStudent_AddThirdStudent() throws StudyUpException {
		int eventID = 1;
		//Create Student
		Student student = new Student();
		student.setFirstName("Forrest");
		student.setLastName("Gump");
		student.setEmail("fgump@icloud.com");
		student.setId(2);
		eventServiceImpl.addStudentToEvent(student, eventID);
		
		//Create another Student
		Student studentThree = new Student();
		studentThree.setFirstName("Spongebob");
		studentThree.setLastName("Squarepants");
		studentThree.setEmail("spongebob@pineapple.com");
		studentThree.setId(3);
		
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.addStudentToEvent(studentThree, 1);
		});
	}
	
	/**
	 * Test that passing an invalid event ID to addStudentToEvent throws an exception
	 */
	@Test
	void testAddStudentToEvent_WrongEventID() {
		int eventID = 3;
		//Create Student
		Student student = new Student();
		student.setFirstName("Forrest");
		student.setLastName("Gump");
		student.setEmail("fgump@icloud.com");
		student.setId(2);
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.addStudentToEvent(student, eventID);
		  });
	}
	
	/**
	 * Test that getActiveEvents only returns *active* events -- that is,
	 * ones scheduled in the future.
	 */
	@Test
	void testGetActiveEvents_noPastEvents() {
		//Create Event 2
		Event eventPast = new Event();
		eventPast.setEventID(2);
		eventPast.setDate(new Date(1)); // Date in 1970, far in the past
		eventPast.setName("Event 1");
		Location location = new Location(-122, 37);
		eventPast.setLocation(location);
		DataStorage.eventData.put(eventPast.getEventID(), eventPast);
		
		List<Event> activeEvents = eventServiceImpl.getActiveEvents();
		for (Event event : activeEvents) {
			if (event.getDate().before(new Date())) {
				fail("getActiveEvents() returned a list that contained a " 
						+ "past event");
			}
		}
	}
	
	/**
	 * Test that getActiveEvents does correctly show events scheduled in the
	 * future.
	 */
	@Test
	void testGetActiveEvents_containsFutureEvents() {
		//Create Event 2
		Event eventFuture = new Event();
		eventFuture.setEventID(2);
		Date currentDate = new Date();
		eventFuture.setDate(new Date(currentDate.getTime() + 1000));
		eventFuture.setName("Event 1");
		Location location = new Location(-122, 37);
		eventFuture.setLocation(location);
		DataStorage.eventData.put(eventFuture.getEventID(), eventFuture);
		
		List<Event> activeEvents = eventServiceImpl.getActiveEvents();
		assertTrue(activeEvents.contains(eventFuture));
	}
	
	/*
	 * Test that getPastEvents does not return any active events.
	 */
	@Test
	void testGetPastEvents_noActiveEvents() {		
		List<Event> pastEvents = eventServiceImpl.getPastEvents();
		for (Event event : pastEvents) {
			if (event.getDate().after(new Date())
					|| event.getDate().equals(new Date())) {
				fail("getPastEvents() returned a list that contained "
						+ "an active event");
			}
		}
	}
	
	/*
	 * Test that we can add an event in the past, and that it appears in
	 * getPastEvents.
	 */
	@Test
	void testGetPastEvents_addPastEvent() {
		//Create Event 2
		Event eventPast = new Event();
		eventPast.setEventID(2);
		eventPast.setDate(new Date(1));
		eventPast.setName("Event 1");
		Location location = new Location(-122, 37);
		eventPast.setLocation(location);
		DataStorage.eventData.put(eventPast.getEventID(), eventPast);
		
		List<Event> pastEvents = eventServiceImpl.getPastEvents();
		assertTrue(pastEvents.contains(eventPast));
	}
	
	/*
	 * Test that deleting an event correctly removes it from the list of
	 * events.
	 */
	@Test
	void testDeleteEvent_addAndRemove() {
		//Create Event 2
		Event eventPast = new Event();
		eventPast.setEventID(2);
		eventPast.setDate(new Date(1));
		eventPast.setName("Event 1");
		Location location = new Location(-122, 37);
		eventPast.setLocation(location);
		DataStorage.eventData.put(eventPast.getEventID(), eventPast);
		
		eventServiceImpl.deleteEvent(2);
		
		Map<Integer, Event> eventData = DataStorage.eventData;
		assertFalse(eventData.containsKey(2));
	}
}
