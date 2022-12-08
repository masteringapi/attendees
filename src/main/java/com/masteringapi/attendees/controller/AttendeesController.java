package com.masteringapi.attendees.controller;

import com.masteringapi.attendees.model.Attendee;
import com.masteringapi.attendees.model.AttendeeNotFoundException;
import com.masteringapi.attendees.model.AttendeeResponse;
import com.masteringapi.attendees.service.AttendeeStore;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;

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
        return new AttendeeResponse(new ArrayList<>(store.getAttendees()));
    }

    @GetMapping("/attendees/{id}")
    @ResponseBody
    @ApiOperation(value = "Retrieve a specific attendee", nickname = "Get Attendee")
    public ResponseEntity<Attendee> getAttendee(@PathVariable(value = "id") Integer id) {
        try {
            return ResponseEntity.ok(this.store.getAttendee(id));
        } catch(AttendeeNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("attendees")
    @ResponseBody
    @ApiOperation(value = "Add a new attendee", nickname = "Add Attendee")
    public ResponseEntity<Void> addAttendee(@Validated @RequestBody Attendee attendee) {
        int id = this.store.addAttendee(attendee);
        URI uri = URI.create("/attendees/" + id);
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/attendees/{id}")
    @ResponseBody
    @ApiOperation(value = "Delete a specific attendee", nickname = "Delete Attendee")
    public ResponseEntity<Attendee> deleteAttendee(@PathVariable(value = "id") Integer id) {
        try {
            this.store.removeAttendee(id);
            return ResponseEntity.ok().build();
        } catch(AttendeeNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("attendees/{id}")
    @ResponseBody
    @ApiOperation(value = "Update a specific attendee", nickname = "Update Attendee")
    public ResponseEntity<Attendee> updateAttendee(@PathVariable(value = "id") Integer id, @RequestBody Attendee attendee) {
        try {
            this.store.updateAttendee(id, attendee);
            return ResponseEntity.noContent().build();
        } catch(AttendeeNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }


}
