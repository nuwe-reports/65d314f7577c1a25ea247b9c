package com.example.demo.controllers;

import com.example.demo.repositories.*;
import com.example.demo.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AppointmentController {

    @Autowired
    AppointmentRepository appointmentRepository;

    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();

        appointmentRepository.findAll().forEach(appointments::add);

        if (appointments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable("id") long id) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);

        if (appointment.isPresent()) {
            return new ResponseEntity<>(appointment.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/appointment")
    public ResponseEntity<?> createAppointment(@RequestBody Appointment appointment) {
        if (appointment == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!isIntervalValid(appointment)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (isAppointmentOverlapped(appointment)) return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return new ResponseEntity<>(savedAppointment, HttpStatus.OK);
    }

    private boolean isAppointmentOverlapped(Appointment appointment) {
        for (Appointment a : appointmentRepository.findAll()) {
            if (a.overlaps(appointment))
                return true;
        }
        return false;
    }

    private boolean isIntervalValid(Appointment appointment) {
        if (appointment.getFinishesAt().isBefore(appointment.getStartsAt())
                || appointment.getFinishesAt().isEqual(appointment.getStartsAt())) {
            return false;
        }
        return true;
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<HttpStatus> deleteAppointment(@PathVariable("id") long id) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);

        if (!appointment.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        appointmentRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/appointments")
    public ResponseEntity<HttpStatus> deleteAllAppointments() {
        appointmentRepository.deleteAll();

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
