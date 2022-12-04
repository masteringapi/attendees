package com.masteringapi.attendees.model;

import java.util.List;

/**
 * The AttendeeResponse is a wrapper response allowing the API to conform to the Microsoft API Guidelines.
 * This allows for forward compatibility if later we introduce pagination/filtering to the service.
 */
public class AttendeeResponse {

    private List<Attendee> value;

    public AttendeeResponse(List<Attendee> attendees) {
        this.value = attendees;
    }

    //Used by the marshaller
    private AttendeeResponse() {}

    public List<Attendee> getValue() {
        return this.value;
    }
}
