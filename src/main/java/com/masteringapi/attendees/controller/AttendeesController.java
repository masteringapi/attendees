package com.masteringapi.attendees.controller;

import com.masteringapi.attendees.model.Attendee;
import com.masteringapi.attendees.model.AttendeeNotFoundException;
import com.masteringapi.attendees.model.AttendeeResponse;
import com.masteringapi.attendees.service.AttendeeStore;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Retrieve a list of all attendees registered in the system")
    public AttendeeResponse getAttendees() {
        return new AttendeeResponse(new ArrayList<>(store.getAttendees()));
    }

    @GetMapping("/attendees/{id}")
    @ResponseBody
    @Operation(summary = "Retrieve a specific attendee registered in the system")
    public ResponseEntity<Attendee> getAttendee(@PathVariable(value = "id") Integer id) {
        try {
            return ResponseEntity.ok(this.store.getAttendee(id));
        } catch(AttendeeNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("attendees")
    @ResponseBody
    @Operation(summary = "Create a new attendee")
    public ResponseEntity<Void> addAttendee(@Validated @RequestBody Attendee attendee) {
        int id = this.store.addAttendee(attendee);
        URI uri = URI.create("/attendees/" + id);
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/attendees/{id}")
    @ResponseBody
    @Operation(summary = "Delete a specific attendee")
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
    @Operation(summary = "Update an attendees details registered in the system")
    public ResponseEntity<Attendee> updateAttendee(@PathVariable(value = "id") Integer id, @RequestBody Attendee attendee) {
        try {
            this.store.updateAttendee(id, attendee);
            return ResponseEntity.noContent().build();
        } catch(AttendeeNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
