package com.masteringapi.attendees.controller;

import com.masteringapi.attendees.model.Attendee;
import com.masteringapi.attendees.model.AttendeeNotFoundException;
import com.masteringapi.attendees.model.AttendeeResponse;
import com.masteringapi.attendees.service.AttendeeStore;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AttendeesController {
    private AttendeeStore store;

    public AttendeesController(@Autowired AttendeeStore store) {
        this.store = store;
    }

    @GetMapping("/attendees")
    @ResponseBody
    @ApiOperation(value = "Retrieve a list of all attendees", nickname = "Get Attendees")
    public AttendeeResponse getAttendees() {
        return new AttendeeResponse(store.getAttendees());
    }

    @GetMapping("/attendees/{id}")
    @ResponseBody
    @ApiOperation(value = "Retrieve a list of all attendees", nickname = "Get Attendees")
    public ResponseEntity<Attendee> getAttendee(@PathVariable(value = "id") Integer id) {
        try {
            return ResponseEntity.ok(this.store.getAttendee(id));
        } catch(AttendeeNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

}
