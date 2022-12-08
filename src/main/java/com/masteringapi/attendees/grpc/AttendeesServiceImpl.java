package com.masteringapi.attendees.grpc;

import com.masteringapi.attendees.grpc.server.*;
import com.masteringapi.attendees.model.AttendeeNotFoundException;
import com.masteringapi.attendees.service.AttendeeStore;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class AttendeesServiceImpl extends AttendeesServiceGrpc.AttendeesServiceImplBase {

    private Logger logger = LoggerFactory.getLogger(AttendeesServiceImpl.class);

    private AttendeeStore store;

    public AttendeesServiceImpl(@Autowired AttendeeStore store) {
        this.store = store;
    }

    @Override
    public void getAttendees(GetAttendeesRequest request, StreamObserver<GetAttendeesResponse> responseObserver) {
        GetAttendeesResponse.Builder responseBuilder = com.masteringapi.attendees.grpc.server.GetAttendeesResponse.newBuilder();

        for(com.masteringapi.attendees.model.Attendee attendee: store.getAttendees()) {
            Attendee grpcAttendee = Attendee.newBuilder()
                    .setId(attendee.getId())
                    .setGivenName(attendee.getGivenName())
                    .setSurname(attendee.getSurname())
                    .setEmail(attendee.getEmail())
                    .build();
            responseBuilder.addAttendees(grpcAttendee);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void createAttendee(CreateAttendeeRequest request, StreamObserver<CreateAttendeeResponse> responseObserver) {
        CreateAttendeeResponse.Builder responseBuilder = CreateAttendeeResponse.newBuilder();

        int id = this.store.addAttendee(new com.masteringapi.attendees.model.Attendee(request.getAttendee()));

        Attendee attendee = Attendee.newBuilder().mergeFrom(request.getAttendee())
                .setId(id)
                .build();
        responseBuilder.setAttendee(attendee);
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getAttendee(GetAttendeeRequest request, StreamObserver<GetAttendeeResponse> responseObserver) {
        GetAttendeeResponse.Builder responseBuilder = GetAttendeeResponse.newBuilder();

        try {
            com.masteringapi.attendees.model.Attendee attendee = this.store.getAttendee(request.getId());
            Attendee grpcAttendee = Attendee.newBuilder()
                    .setId(attendee.getId())
                    .setGivenName(attendee.getGivenName())
                    .setSurname(attendee.getSurname())
                    .setEmail(attendee.getEmail())
                    .build();
            responseBuilder.setAttendee(grpcAttendee);
            responseObserver.onNext(responseBuilder.build());
        } catch (AttendeeNotFoundException e) {
            responseObserver.onError(e);
            logger.error("Could not find attendee", e);
        }
        responseObserver.onCompleted();
    }
}
