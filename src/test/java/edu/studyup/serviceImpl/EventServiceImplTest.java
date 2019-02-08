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

	@Test
	void testUpdateEventName_GoodCase() throws StudyUpException {
		int eventID = 1;
		eventServiceImpl.updateEventName(eventID, "Renamed Event 1");
		assertEquals("Renamed Event 1", DataStorage.eventData.get(eventID).getName());
	}
	
	@Test
	void testUpdateEventName_TwentyCharacterName() throws StudyUpException {
		int eventID = 1;
		eventServiceImpl.updateEventName(eventID, "A 20 character name!");
		assertEquals("A 20 character name!", DataStorage.eventData.get(eventID).getName());
	}
	
	
	@Test
	void testUpdateEventName_TwentyFiveCharacter() {
		int eventID = 1;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "A 25 character name!!!!!!");
		});
	}
	
	@Test
	void testUpdateEventName_WrongEventID() {
		int eventID = 3;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Renamed Event 3");
		  });
	}
	
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
	
	@Test
	void testGetActiveEvents_noPastEvents() {
		//Create Event 2
		Event eventPast = new Event();
		eventPast.setEventID(2);
		eventPast.setDate(new Date(1));
		eventPast.setName("Event 1");
		Location location = new Location(-122, 37);
		eventPast.setLocation(location);
		DataStorage.eventData.put(eventPast.getEventID(), eventPast);
		
		List<Event> activeEvents = eventServiceImpl.getActiveEvents();
		for (Event event : activeEvents) {
			if (event.getDate().before(new Date())) {
				fail("getActiveEvents() returned a list that contained a past event");
			}
		}
	}
	
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
	
	@Test
	void testGetPastEvents_noActiveEvents() {		
		List<Event> pastEvents = eventServiceImpl.getPastEvents();
		for (Event event : pastEvents) {
			if (event.getDate().after(new Date()) || event.getDate().equals(new Date())) {
				fail("getPastEvents() returned a list that contained an active event");
			}
		}
	}
	
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
