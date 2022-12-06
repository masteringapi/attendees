package com.masteringapi.attendees.service;

import com.masteringapi.attendees.model.Attendee;
import com.masteringapi.attendees.model.AttendeeNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AttendeeStore {
    private final Map<Integer, Attendee> attendees = new LinkedHashMap<>();

    private final AtomicInteger counter = new AtomicInteger(0);

    public AttendeeStore() {
        //Setup some mock data
        Attendee attendee = new Attendee();
        attendee.setId(counter.incrementAndGet());
        attendee.setGivenName("Jim");
        attendee.setSurname("Gough");
        attendee.setEmail("gough@mail.com");
        this.attendees.put(1, attendee);
        attendee = new Attendee();
        attendee.setId(counter.incrementAndGet());
        attendee.setGivenName("Matt");
        attendee.setSurname("Auburn");
        attendee.setEmail("auburn@mail.com");
        this.attendees.put(2, attendee);
        attendee = new Attendee();
        attendee.setId(counter.incrementAndGet());
        attendee.setGivenName("Daniel");
        attendee.setSurname("Bryant");
        attendee.setEmail("bryant@mail.com");
        this.attendees.put(3, attendee);
    }

    public Collection<Attendee> getAttendees() {
        return this.attendees.values();
    }

    public Attendee getAttendee(Integer id) throws AttendeeNotFoundException {
        if(this.attendees.containsKey(id)) {
            return this.attendees.get(id);
        } else {
            throw new AttendeeNotFoundException();
        }
    }

    public int addAttendee(Attendee attendee) {
        int id = counter.incrementAndGet();
        attendee.setId(id);
        this.attendees.put(id, attendee);
        return this.attendees.size();
    }

    public void removeAttendee(Integer id) throws AttendeeNotFoundException {
        if(this.attendees.containsKey(id)) {
            this.attendees.remove(id);
        } else {
            throw new AttendeeNotFoundException();
        }
    }
}
