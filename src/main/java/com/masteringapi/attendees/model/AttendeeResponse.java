package com.masteringapi.attendees.model;

import java.util.List;

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
