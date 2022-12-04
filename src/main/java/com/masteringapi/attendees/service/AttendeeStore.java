package com.masteringapi.attendees.service;

import com.masteringapi.attendees.model.Attendee;
import com.masteringapi.attendees.model.AttendeeNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AttendeeStore {

    private List<Attendee> attendees = new ArrayList<>();

    public AttendeeStore() {
        //Setup some mock data
        Attendee attendee = new Attendee();
        attendee.setId(1);
        attendee.setGivenName("Jim");
        attendee.setSurname("Gough");
        attendee.setEmail("gough@mail.com");
        this.attendees.add(attendee);
        attendee = new Attendee();
        attendee.setId(2);
        attendee.setGivenName("Matt");
        attendee.setSurname("Auburn");
        attendee.setEmail("auburn@mail.com");
        this.attendees.add(attendee);
        attendee = new Attendee();
        attendee.setId(3);
        attendee.setGivenName("Daniel");
        attendee.setSurname("Bryant");
        attendee.setEmail("bryant@mail.com");
        this.attendees.add(attendee);
    }

    public List<Attendee> getAttendees() {
        return this.attendees;
    }

    public Attendee getAttendee(Integer id) throws AttendeeNotFoundException {
        if(id - 1 >= this.attendees.size()) {
            throw new AttendeeNotFoundException();
        } else {
            return this.attendees.get(id - 1);
        }
    }

}
