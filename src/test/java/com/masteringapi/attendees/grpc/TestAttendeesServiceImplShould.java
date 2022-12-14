package com.masteringapi.attendees.grpc;

import com.masteringapi.attendees.grpc.server.*;
import com.masteringapi.attendees.model.Attendee;
import com.masteringapi.attendees.model.AttendeeNotFoundException;
import com.masteringapi.attendees.service.AttendeeStore;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestAttendeesServiceImplShould {

    @Mock
    private AttendeeStore store;

    @InjectMocks
    private AttendeesServiceImpl attendeesService;

    @Test
    void return_empty_list_when_no_attendees_in_store() {
        when(store.getAttendees()).thenReturn(new ArrayList<>());
        GetAttendeesRequest request = GetAttendeesRequest.newBuilder().build();
        StreamRecorder<GetAttendeesResponse> responseObserver = StreamRecorder.create();

        this.attendeesService.getAttendees(request, responseObserver);
        assertThat(responseObserver.getError(), equalTo(null));
        List<GetAttendeesResponse> responses = responseObserver.getValues();
        assertThat(responses.size(), equalTo(1));
        assertThat(responses.get(0).getAttendeesList().size(), equalTo(0));
    }

    @Test
    void return_attendee_when_in_store() {
        List<Attendee> attendees = new ArrayList<>();
        attendees.add(testAttendee());
        GetAttendeesRequest request = GetAttendeesRequest.newBuilder().build();
        when(store.getAttendees()).thenReturn(attendees);
        StreamRecorder<GetAttendeesResponse> responseObserver = StreamRecorder.create();

        this.attendeesService.getAttendees(request, responseObserver);
        assertThat(responseObserver.getError(), equalTo(null));
        List<GetAttendeesResponse> responses = responseObserver.getValues();
        assertThat(responses.size(), equalTo(1));
        assertThat(responses.get(0).getAttendeesList().size(), equalTo(1));
        assertThat(responses.get(0).getAttendeesList().get(0).getEmail(), equalTo("jim@gough"));
    }

    @Test
    void throw_an_error_when_attendee_does_not_exist() throws AttendeeNotFoundException {
        GetAttendeeRequest request = GetAttendeeRequest.newBuilder().setId(1).build();
        when(store.getAttendee(anyInt())).thenThrow(new AttendeeNotFoundException());
        StreamRecorder<GetAttendeeResponse> responseObserver = StreamRecorder.create();

        this.attendeesService.getAttendee(request, responseObserver);
        assertThat(responseObserver.getError(), is(notNullValue()));
    }

    @Test
    void attendee_returned_for_a_give_id() throws AttendeeNotFoundException {
        GetAttendeeRequest request = GetAttendeeRequest.newBuilder().setId(1).build();
        when(store.getAttendee(anyInt())).thenReturn(testAttendee());
        StreamRecorder<GetAttendeeResponse> responseObserver = StreamRecorder.create();

        this.attendeesService.getAttendee(request, responseObserver);
        assertThat(responseObserver.getError(), equalTo(null));
        List<GetAttendeeResponse> responses = responseObserver.getValues();
        assertThat(responses.get(0).getAttendee().getEmail(), equalTo("jim@gough"));
        assertThat(responses.get(0).getAttendee().getId(), equalTo(1));
    }

    @Test
    void creates_an_attendee() {
        CreateAttendeeRequest createAttendeeRequest = CreateAttendeeRequest.newBuilder()
                .setAttendee(testGrpcAttendee()).build();
        StreamRecorder<CreateAttendeeResponse> responseObserver = StreamRecorder.create();

        this.attendeesService.createAttendee(createAttendeeRequest, responseObserver);
        assertThat(responseObserver.getError(), equalTo(null));
        List<CreateAttendeeResponse> responses = responseObserver.getValues();
        assertThat(responses.get(0).getAttendee().getEmail(), equalTo("jim@gough"));
        verify(store).addAttendee(ArgumentMatchers.isA(Attendee.class));
    }

    @Test
    void error_when_deleting_a_non_existing_attendee() throws AttendeeNotFoundException {
        DeleteAttendeeRequest deleteAttendeeRequest = DeleteAttendeeRequest.newBuilder().setId(1).build();
        StreamRecorder<DeleteAttendeeResponse> responseObserver = StreamRecorder.create();
        doThrow(new AttendeeNotFoundException()).when(store).removeAttendee(isA(Integer.class));

        this.attendeesService.deleteAttendee(deleteAttendeeRequest, responseObserver);
        assertThat(responseObserver.getError(), is(notNullValue()));
    }

    @Test
    void delete_a_known_attendee() throws AttendeeNotFoundException {
        DeleteAttendeeRequest deleteAttendeeRequest = DeleteAttendeeRequest.newBuilder().setId(1).build();
        StreamRecorder<DeleteAttendeeResponse> responseObserver = StreamRecorder.create();

        this.attendeesService.deleteAttendee(deleteAttendeeRequest, responseObserver);
        assertThat(responseObserver.getError(), equalTo(null));
        verify(store).removeAttendee(ArgumentMatchers.isA(Integer.class));
    }

    @Test
    void error_when_updating_a_missing_attendee() throws AttendeeNotFoundException {
        UpdateAttendeeRequest updateAttendeeRequest = UpdateAttendeeRequest.newBuilder().setAttendee(testGrpcAttendee()).build();
        StreamRecorder<UpdateAttendeeResponse> responseObserver = StreamRecorder.create();
        doThrow(new AttendeeNotFoundException()).when(store).updateAttendee( isA(Integer.class), isA(Attendee.class));

        this.attendeesService.updateAttendee(updateAttendeeRequest, responseObserver);
        assertThat(responseObserver.getError(), is(notNullValue()));
    }

    @Test
    void updates_a_given_attendee() throws AttendeeNotFoundException {
        UpdateAttendeeRequest updateAttendeeRequest = UpdateAttendeeRequest.newBuilder().setAttendee(testGrpcAttendee()).build();
        StreamRecorder<UpdateAttendeeResponse> responseObserver = StreamRecorder.create();

    }

    private com.masteringapi.attendees.grpc.server.Attendee testGrpcAttendee() {
        return com.masteringapi.attendees.grpc.server.Attendee.newBuilder()
                .setId(1)
                .setEmail("jim@gough")
                .setSurname("Gough")
                .setGivenName("Jim")
                .build();
    }

    private Attendee testAttendee() {
        Attendee attendee = new Attendee();
        attendee.setSurname("Gough");
        attendee.setId(1);
        attendee.setGivenName("Jim");
        attendee.setEmail("jim@gough");
        return attendee;
    }
}
