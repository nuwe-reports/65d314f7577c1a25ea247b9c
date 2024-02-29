package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import com.example.demo.entities.Appointment;
import com.example.demo.entities.Doctor;
import com.example.demo.entities.Patient;
import com.example.demo.entities.Room;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EntityUnitTest {

    @Autowired
    private TestEntityManager entityManager;

    private Doctor d1;
    private Patient p1;
    private Room r1;
    private Appointment a1;

    @BeforeEach
    void setUp() {
        d1 = new Doctor("Perla", "Amalia", 24, "p.amalia@hospital.accwe");
        p1 = new Patient("Jose Luis", "Olaya", 37, "j.olaya@email.com");
        r1 = new Room("Rehabilitation");
        a1 = new Appointment(p1, d1, r1, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
    }

    @Test
    void doctor_UnitTest() {
        d1.setId(2L);
        d1.setFirstName("Carlos");
        d1.setLastName("Perez");
        d1.setAge(45);
        d1.setEmail("email.changed@example.es");

        assertThat(d1.getId()).isEqualTo(2L);
        assertThat(d1.getFirstName()).isEqualTo("Carlos");
        assertThat(d1.getLastName()).isEqualTo("Perez");
        assertThat(d1.getAge()).isEqualTo(45);
        assertThat(d1.getEmail()).isEqualTo("email.changed@example.es");
    }

    @Test
    void doctor_IntegrationTest() {
        entityManager.persistAndFlush(d1);
        Doctor found = entityManager.find(Doctor.class, d1.getId());

        assertThat(found).isEqualTo(d1);
        assertThat(found.getFirstName()).isEqualTo(d1.getFirstName());
        assertThat(found.getLastName()).isEqualTo(d1.getLastName());
        assertThat(found.getAge()).isEqualTo(d1.getAge());
        assertThat(found.getEmail()).isEqualTo(d1.getEmail());

    }

    @Test
    void patient_UnitTest() {
        p1.setId(1L);
        p1.setFirstName("Juan");
        p1.setLastName("Carlos");
        p1.setAge(34);
        p1.setEmail("email.changed@example.es");

        assertThat(p1.getId()).isEqualTo(1L);
        assertThat(p1.getFirstName()).isEqualTo("Juan");
        assertThat(p1.getLastName()).isEqualTo("Carlos");
        assertThat(p1.getAge()).isEqualTo(34);
        assertThat(p1.getEmail()).isEqualTo("email.changed@example.es");

    }

    @Test
    void room_UnitTest() {
        assertThat(r1.getRoomName()).isEqualTo("Rehabilitation");
    }

    @Test
    void room_IntegrationTest() {
        entityManager.persistAndFlush(r1);

        Room found = entityManager.find(Room.class, r1.getRoomName());

        assertThat(found).isEqualTo(r1);

        Assert.assertThrows(javax.persistence.PersistenceException.class, () -> {
            Room room = new Room();
            entityManager.persistAndFlush(room);
        });
    }

    @Test
    void appointment_UnitTest() {
        LocalDateTime newDateTime = LocalDateTime.now();

        a1.setId(1L);
        a1.setDoctor(d1);
        a1.setPatient(p1);
        a1.setRoom(r1);
        a1.setStartsAt(newDateTime);
        a1.setFinishesAt(newDateTime.plusHours(1));

        assertThat(a1.getId()).isEqualTo(1L);
        assertThat(a1.getDoctor()).isEqualTo(d1);
        assertThat(a1.getPatient()).isEqualTo(p1);
        assertThat(a1.getRoom()).isEqualTo(r1);
        assertThat(a1.getStartsAt()).isEqualTo(newDateTime);
        assertThat(a1.getFinishesAt()).isEqualTo(newDateTime.plusHours(1));
    }

    @Test
    void appointment_IntegrationTest() {
        entityManager.persistAndFlush(a1);
        Appointment found = entityManager.find(Appointment.class, a1.getId());
        assertThat(found).isEqualTo(a1);
    }

    @Test
    public void test_non_overlapping_appointments() {
        LocalDateTime dateTime = LocalDateTime.now();

        Appointment appointment1 = new Appointment(p1, d1, r1, dateTime, dateTime.plusMinutes(20));
        Appointment appointment2 = new Appointment(p1, d1, r1, dateTime.plusMinutes(20), dateTime.plusMinutes(40));

        assertThat(appointment1.overlaps(appointment2)).isFalse();
        assertThat(appointment2.overlaps(appointment1)).isFalse();
    }

    @Test
    public void test_non_overlapping_in_distinct_rooms() {
        Room room2 = new Room("Cardiology");

        LocalDateTime dateTime = LocalDateTime.now();

        Appointment appointment1 = new Appointment(p1, d1, r1, dateTime, dateTime.plusMinutes(20));
        Appointment appointment2 = new Appointment(p1, d1, room2, dateTime.plusMinutes(10), dateTime.plusMinutes(30));

        assertThat(appointment1.overlaps(appointment2)).isFalse();
    }

    @Test
    public void test_overlapping_appointments_with_same_start() {

        LocalDateTime dateTime = LocalDateTime.now();

        Appointment appointment1 = new Appointment(p1, d1, r1, dateTime, dateTime.plusMinutes(20));
        Appointment appointment2 = new Appointment(p1, d1, r1, dateTime, dateTime.plusMinutes(30));

        assertThat(appointment1.overlaps(appointment2)).isTrue();
    }

    @Test
    public void test_overlapping_appointments_with_same_end() {
        LocalDateTime dateTime = LocalDateTime.now();

        Appointment appointment1 = new Appointment(p1, d1, r1, dateTime, dateTime.plusMinutes(20));
        Appointment appointment2 = new Appointment(p1, d1, r1, dateTime.plusMinutes(1), dateTime.plusMinutes(20));

        assertThat(appointment1.overlaps(appointment2)).isTrue();
    }

    @Test
    public void test_appointment_overlaps_interval() {
        LocalDateTime dateTime = LocalDateTime.now();

        Appointment appointment1 = new Appointment(p1, d1, r1, dateTime, dateTime.plusMinutes(20));
        Appointment appointment2 = new Appointment(p1, d1, r1, dateTime.plusMinutes(10), dateTime.plusMinutes(30));

        assertThat(appointment1.overlaps(appointment2)).isTrue();
        assertThat(appointment2.overlaps(appointment1)).isTrue();
    }
}
