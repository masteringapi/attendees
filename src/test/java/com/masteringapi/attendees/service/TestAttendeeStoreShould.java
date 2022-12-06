package com.masteringapi.attendees.service;

import com.masteringapi.attendees.model.Attendee;
import com.masteringapi.attendees.model.AttendeeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestAttendeeStoreShould {

    private AttendeeStore attendeeStore;

    @BeforeEach
    void before() {
        this.attendeeStore = new AttendeeStore();
    }
    @Test
    void initializes_with_sample_data() {
        assertThat(this.attendeeStore.getAttendees().size(), equalTo(3));
    }

    @Test
    void raises_exception_for_out_of_range_id() {
        assertThrows(AttendeeNotFoundException.class, () -> this.attendeeStore.getAttendee(5));
    }

    @Test
    void returns_valid_attendee_with_in_range_id() throws AttendeeNotFoundException {
        Attendee attendee = this.attendeeStore.getAttendee(1);
        assertThat(attendee.getId(), equalTo(1));
        assertThat(attendee.getSurname(), equalTo("Gough"));
    }

    @Test
    void add_a_new_attendee_to_store() throws AttendeeNotFoundException {
        Attendee attendee = new Attendee();
        //Should be replaced with 4
        attendee.setId(100);
        attendee.setEmail("test@mail.com");
        attendee.setGivenName("Test");
        attendee.setSurname("Surname");
        int id = this.attendeeStore.addAttendee(attendee);
        assertThat(id, equalTo(4));
        Attendee storedAttendee = this.attendeeStore.getAttendee(4);
        assertThat(storedAttendee.getId(), equalTo(4));
        assertThat(storedAttendee.getEmail(), equalTo("test@mail.com"));
    }

    @Test
    void remove_an_attendee_throws_when_out_of_bounds() {
        assertThrows(AttendeeNotFoundException.class, () -> this.attendeeStore.removeAttendee(5));
    }

    @Test
    void remove_a_valid_attendee() throws AttendeeNotFoundException {
        this.attendeeStore.removeAttendee(3);
        assertThat(this.attendeeStore.getAttendees().size(), equalTo(2));
    }

    @Test
    void update_an_attendee_throws_when_out_of_bounds() {
        assertThrows(AttendeeNotFoundException.class, () -> this.attendeeStore.updateAttendee(5, null));
    }

    @Test
    void update_an_attendee_with_new_values() throws AttendeeNotFoundException {
        Attendee attendee = new Attendee();
        attendee.setId(10);
        attendee.setEmail("test@mail.com");
        attendee.setGivenName("Test");
        attendee.setSurname("User");
        this.attendeeStore.updateAttendee(1, attendee);
        assertThat(1, equalTo(this.attendeeStore.getAttendee(1).getId()));
        assertThat("test@mail.com", equalTo(this.attendeeStore.getAttendee(1).getEmail()));
    }
}
