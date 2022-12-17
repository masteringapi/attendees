package com.masteringapi.attendees.grpc;

import com.google.rpc.Code;
import com.google.rpc.Status;
import com.masteringapi.attendees.grpc.server.*;
import com.masteringapi.attendees.model.AttendeeNotFoundException;
import com.masteringapi.attendees.service.AttendeeStore;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class AttendeesServiceImpl extends AttendeesServiceGrpc.AttendeesServiceImplBase {

    private final Logger logger = LoggerFactory.getLogger(AttendeesServiceImpl.class);

    private final AttendeeStore store;

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
                    .setSpeaker(attendee.isSpeaker())
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
            var attendee = this.store.getAttendee(request.getId());
            Attendee grpcAttendee = Attendee.newBuilder()
                    .setId(attendee.getId())
                    .setGivenName(attendee.getGivenName())
                    .setSurname(attendee.getSurname())
                    .setEmail(attendee.getEmail())
                    .setSpeaker(attendee.isSpeaker())
                    .build();
            responseBuilder.setAttendee(grpcAttendee);
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (AttendeeNotFoundException e) {
            Status status = Status.newBuilder().setCode(Code.NOT_FOUND.getNumber())
                            .setMessage("Attendee Not Found")
                                    .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
            logger.error("Could not find attendee", e);
        }
    }

    @Override
    public void deleteAttendee(DeleteAttendeeRequest request, StreamObserver<DeleteAttendeeResponse> responseObserver) {
        DeleteAttendeeResponse.Builder responseBuilder = DeleteAttendeeResponse.newBuilder();

        try {
            this.store.removeAttendee(request.getId());
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (AttendeeNotFoundException e) {
            Status status = Status.newBuilder().setCode(Code.NOT_FOUND.getNumber())
                    .setMessage("Attendee Not Found")
                    .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
            logger.error("Could not find attendee to delete", e);
        }
    }

    @Override
    public void updateAttendee(UpdateAttendeeRequest request, StreamObserver<UpdateAttendeeResponse> responseObserver) {
        UpdateAttendeeResponse.Builder responseBuilder = UpdateAttendeeResponse.newBuilder();

        try {
            this.store.updateAttendee(request.getAttendee().getId(),
                    new com.masteringapi.attendees.model.Attendee(request.getAttendee()));

            responseBuilder.setAttendee(request.getAttendee());
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (AttendeeNotFoundException e) {
            Status status = Status.newBuilder().setCode(Code.NOT_FOUND.getNumber())
                    .setMessage("Attendee Not Found")
                    .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
            logger.error("Unable to update attendee", e);
        }
    }
}
