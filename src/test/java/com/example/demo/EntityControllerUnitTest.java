
package com.example.demo;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.controllers.*;
import com.example.demo.repositories.*;
import com.example.demo.entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(DoctorController.class)
class DoctorControllerUnitTest {

    @MockBean
    private DoctorRepository doctorRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    Doctor doctor1, doctor2;

    @BeforeEach
    void setUp() {
        doctor1 = new Doctor("Juan", "Carlos", 34, "doctor@example.com");
        doctor2 = new Doctor("Carla", "Matas", 42, "doctor2@example.com");
    }

    @Test
    void should_create_a_valid_doctor() throws Exception {
        Doctor doctor = new Doctor("Juan", "Carlos", 34, "doctor@example.com");

        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        mockMvc.perform(post("/api/doctor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctor)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(doctor)));

        verify(doctorRepository, times(1)).save(any(Doctor.class));

    }

    @Test
    void should_return_doctor_by_id() throws Exception {
        Doctor doctor = new Doctor("Juan", "Carlos", 34, "juan@example.com");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        mockMvc.perform(get("/api/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(doctor)));

        verify(doctorRepository, times(1)).findById(1L);
    }

    @Test
    void should_return_not_found_if_doctor_does_not_exist() throws Exception {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/doctors/1"))
                .andExpect(status().isNotFound());

        verify(doctorRepository, times(1)).findById(1L);
    }

    @Test
    void should_return_no_content_if_no_doctors() throws Exception {
        when(doctorRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isNoContent());

        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    void should_return_all_doctors() throws Exception {
        List<Doctor> doctors = Arrays.asList(doctor1, doctor2);

        when(doctorRepository.findAll()).thenReturn(doctors);
        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(doctors)));

        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    void should_delete_doctor_by_id() throws Exception {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor1));

        mockMvc.perform(delete("/api/doctors/1"))
                .andExpect(status().isOk());

        verify(doctorRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).deleteById(1L);
    }

    @Test
    public void should_return_not_found_if_doctor_to_delete_not_exist() throws Exception {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/doctors/1"))
                .andExpect(status().isNotFound());

        verify(doctorRepository, times(1)).findById(1L);
    }

    @Test
    public void should_delete_all_doctors() throws Exception {
        mockMvc.perform(delete("/api/doctors"))
                .andExpect(status().isOk());

        verify(doctorRepository, times(1)).deleteAll();
    }
}

@WebMvcTest(PatientController.class)
class PatientControllerUnitTest {

    @MockBean
    private PatientRepository patientRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Patient patient1, patient2;

    @BeforeEach
    void setUp() {
        patient1 = new Patient("Andrea", "Perez Arroyo", 37, "andrea.arroyo@email.com");
        patient2 = new Patient("Jose Luis", "Olaya", 37, "j.olaya@email.com");
    }

    @Test
    void should_create_a_valid_patient() throws Exception {
        when(patientRepository.save(any(Patient.class))).thenReturn(patient1);

        mockMvc.perform(post("/api/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient1)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(patient1)));

        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void should_return_patient_by_id() throws Exception {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient1));

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(patient1)));

        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void should_return_not_found_if_patient_does_not_exist() throws Exception {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isNotFound());

        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void should_return_no_content_if_no_patients() throws Exception {
        when(patientRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isNoContent());

        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void should_return_all_patients() throws Exception {
        List<Patient> patients = Arrays.asList(patient1, patient2);

        when(patientRepository.findAll()).thenReturn(patients);

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(patients)));

        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void should_delete_patient_by_id() throws Exception {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient1));

        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isOk());

        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    public void should_return_not_found_if_patient_to_delete_not_exist() throws Exception {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isNotFound());

        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    public void should_delete_all_patients() throws Exception {
        mockMvc.perform(delete("/api/patients"))
                .andExpect(status().isOk());

        verify(patientRepository, times(1)).deleteAll();
    }
}

@WebMvcTest(RoomController.class)
class RoomControllerUnitTest {

    @MockBean
    private RoomRepository roomRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Room room1, room2;

    @BeforeEach
    void setUp() {
        room1 = new Room("Cardiology");
        room2 = new Room("Oncology");
    }

    @Test
    void should_create_a_valid_room() throws Exception {
        when(roomRepository.save(any(Room.class))).thenReturn(room1);

        mockMvc.perform(post("/api/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(room1)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(room1)));

        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void should_return_room_by_room_name() throws Exception {
        when(roomRepository.findByRoomName(room1.getRoomName())).thenReturn(Optional.of(room1));

        mockMvc.perform(get("/api/rooms/Cardiology"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(room1)));

        verify(roomRepository, times(1)).findByRoomName(room1.getRoomName());
    }

    @Test
    void should_return_not_found_if_room_does_not_exist() throws Exception {
        when(roomRepository.findByRoomName(any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rooms/Cardiology"))
                .andExpect(status().isNotFound());

        verify(roomRepository, times(1)).findByRoomName("Cardiology");
    }

    @Test
    void should_return_no_content_if_no_rooms() throws Exception {
        when(roomRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isNoContent());

        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void should_return_all_rooms() throws Exception {
        List<Room> rooms = Arrays.asList(room1, room2);

        when(roomRepository.findAll()).thenReturn(rooms);

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(rooms)));

        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void should_delete_room_by_room_name() throws Exception {
        when(roomRepository.findByRoomName(room1.getRoomName())).thenReturn(Optional.of(room1));

        mockMvc.perform(delete("/api/rooms/Cardiology"))
                .andExpect(status().isOk());

        verify(roomRepository, times(1)).findByRoomName(room1.getRoomName());
        verify(roomRepository, times(1)).deleteByRoomName(room1.getRoomName());
    }

    @Test
    public void should_return_not_found_if_room_to_delete_not_exist() throws Exception {
        when(roomRepository.findByRoomName(any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/rooms/Cardiology"))
                .andExpect(status().isNotFound());

        verify(roomRepository, times(1)).findByRoomName("Cardiology");
    }

    @Test
    public void should_delete_all_rooms() throws Exception {
        mockMvc.perform(delete("/api/rooms"))
                .andExpect(status().isOk());

        verify(roomRepository, times(1)).deleteAll();
    }
}
