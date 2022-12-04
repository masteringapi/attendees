package com.masteringapi.attendees.service;

import com.masteringapi.attendees.model.Attendee;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AttendeeStore {

    public List<Attendee> getAttendees() {
        List<Attendee> attendees = new ArrayList<>();
        Attendee attendee = new Attendee();
        attendee.setId(1);
        attendee.setGivenName("Jim");
        attendee.setSurname("Gough");
        attendee.setEmail("gough@mail.com");
        attendees.add(attendee);
        attendee = new Attendee();
        attendee.setId(2);
        attendee.setGivenName("Matt");
        attendee.setSurname("Auburn");
        attendee.setEmail("auburn@mail.com");
        attendees.add(attendee);
        attendee = new Attendee();
        attendee.setId(3);
        attendee.setGivenName("Daniel");
        attendee.setSurname("Bryant");
        attendee.setEmail("bryant@mail.com");
        attendees.add(attendee);

        return attendees;
    }

}
