package com.codistrib.alertservice.grpc;

import com.codistrib.alertservice.domain.enums.AlertStatus;
import com.codistrib.alertservice.domain.model.Alert;
import com.codistrib.alertservice.service.AlertDomainService;
import com.codistrib.proto.alert.*; // classes générées depuis protos/alert.proto
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@GrpcService
public class AlertGrpcService extends AlertServiceGrpc.AlertServiceImplBase {
    private final AlertDomainService service;
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_INSTANT;

    public AlertGrpcService(AlertDomainService service) {
        this.service = service;
    }

    @Override
    public void createAlert(AlertProto.CreateAlertRequest req, StreamObserver<AlertProto.CreateAlertResponse> res) {
        try {
            var a = service.create(
                    req.getPersonId(),
                    req.getType(),
                    req.getMessage(),
                    req.getLat(),
                    req.getLon()
            );
            res.onNext(AlertProto.CreateAlertResponse.newBuilder().setAlert(toProto(a)).build());
            res.onCompleted();
        } catch (RuntimeException e) {
            res.onError(map(e));
        }
    }

    @Override
    public void assignAlert(AlertProto.AssignAlertRequest req, StreamObserver<AlertProto.AssignAlertResponse> res) {
        try {
            var a = service.assign(req.getAlertId(), req.getHelperId());
            res.onNext(AlertProto.AssignAlertResponse.newBuilder().setAlert(toProto(a)).build());
            res.onCompleted();
        } catch (RuntimeException e) {
            res.onError(map(e));
        }
    }

    @Override
    public void resolveAlert(AlertProto.ResolveAlertRequest req, StreamObserver<AlertProto.ResolveAlertResponse> res) {
        try {
            var a = service.resolve(req.getAlertId(), req.getHelperId());
            res.onNext(AlertProto.ResolveAlertResponse.newBuilder().setAlert(toProto(a)).build());
            res.onCompleted();
        } catch (RuntimeException e) {
            res.onError(map(e));
        }
    }

    @Override
    public void getAlert(AlertProto.GetAlertRequest req, StreamObserver<AlertProto.GetAlertResponse> res) {
        try {
            var a = service.getById(req.getAlertId());
            res.onNext(AlertProto.GetAlertResponse.newBuilder().setAlert(toProto(a)).build());
            res.onCompleted();
        } catch (RuntimeException e) { res.onError(map(e)); }
    }

    @Override
    public void listAlertsByPerson(AlertProto.ListByPersonRequest req, StreamObserver<AlertProto.ListAlertsResponse> res) {
        try {
            var list = service.listByPerson(req.getPersonId());
            AlertProto.ListAlertsResponse.Builder rb = AlertProto.ListAlertsResponse.newBuilder();
            list.forEach(a -> rb.addAlerts(toProto(a)));
            res.onNext(rb.build());
            res.onCompleted();
        } catch (RuntimeException e) { res.onError(map(e)); }
    }

    @Override
    public void listAlertsByHelper(AlertProto.ListByHelperRequest req, StreamObserver<AlertProto.ListAlertsResponse> res) {
        try {
            var list = service.listByHelper(req.getHelperId());
            AlertProto.ListAlertsResponse.Builder rb = AlertProto.ListAlertsResponse.newBuilder();
            list.forEach(a -> rb.addAlerts(toProto(a)));
            res.onNext(rb.build());
            res.onCompleted();
        } catch (RuntimeException e) { res.onError(map(e)); }
    }

    @Override
    public void listAlertsByStatus(AlertProto.ListByStatusRequest req, StreamObserver<AlertProto.ListAlertsResponse> res) {
        try {
            String s = req.getStatus();
            if (s == null || s.isBlank()) throw new IllegalArgumentException("status is required");
            AlertStatus st = AlertStatus.valueOf(s.toUpperCase(Locale.ROOT));
            var list = service.listByStatus(st);
            AlertProto.ListAlertsResponse.Builder rb = AlertProto.ListAlertsResponse.newBuilder();
            list.forEach(a -> rb.addAlerts(toProto(a)));
            res.onNext(rb.build());
            res.onCompleted();
        } catch (IllegalArgumentException e) {
            // Inclut le cas valueOf() invalide
            res.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asException());
        } catch (RuntimeException e) { res.onError(map(e)); }
    }


    private StatusException map(RuntimeException e) {
        String msg = e.getMessage() == null ? "error" : e.getMessage();
        if (e instanceof IllegalArgumentException) {
            return Status.INVALID_ARGUMENT.withDescription(msg).asException();
        }
        // On inspecte le message pour détecter les cas courants
        String lower = msg.toLowerCase();
        if (lower.contains("not found")) return Status.NOT_FOUND.withDescription(msg).asException();
        if (lower.contains("only")) return Status.FAILED_PRECONDITION.withDescription(msg).asException();
        return Status.INTERNAL.withDescription(msg).asException();
    }

    private AlertProto.Alert toProto(Alert a) {
        AlertProto.Alert.Builder b = AlertProto.Alert.newBuilder()
                .setId(nvl(a.getId()))
                .setPersonId(nvl(a.getPersonId()))
                .setHelperId(nvl(a.getHelperId()))
                .setType(nvl(a.getType()))
                .setMessage(nvl(a.getMessage()))
                .setStatus(a.getStatus() == null ? "" : a.getStatus().name())
                .setLat(a.getLat() == null ? 0.0 : a.getLat())
                .setLon(a.getLon() == null ? 0.0 : a.getLon())
                .setCreatedAt(a.getCreatedAt() == null ? "" : ISO.format(a.getCreatedAt()))
                .setUpdatedAt(a.getUpdatedAt() == null ? "" : ISO.format(a.getUpdatedAt()))
                .setResolvedAt(a.getResolvedAt() == null ? "" : ISO.format(a.getResolvedAt()));
        return b.build();
    }

    private String nvl(String s) { return s == null ? "" : s; }
}