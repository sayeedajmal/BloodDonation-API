package com.strong.BloodDonation.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.strong.BloodDonation.Model.Appointment;
import com.strong.BloodDonation.Model.Donor;
import com.strong.BloodDonation.Service.AppointmentService;
import com.strong.BloodDonation.Service.DonorService;
import com.strong.BloodDonation.Utils.AppointmentStatus;
import com.strong.BloodDonation.Utils.BloodException;

import jakarta.transaction.Transactional;

/**
 * AppointController class handles HTTP requests related to appointment
 * operations.
 * This controller uses the @RestController annotation to indicate that it's a
 * controller
 * and automatically serializes the returned objects into JSON format.
 */
@RestController
@RequestMapping("/api/v1/appointment")
public class AppointController {

    @Autowired
    private AppointmentService appointService;

    @Autowired
    private DonorService donorService;

    /**
     * `
     * POST endpoint to create a new appointment.
     *
     * @param appointment The appointment object to be created.
     * @param donorId     The unique identifier of the donor associated with the
     *                    appointment.
     * @return A response indicating the success or failure of the operation.
     */
    @PostMapping("createAppointment")
    public ResponseEntity<String> createAppointment(@ModelAttribute Appointment appointment,
            @RequestParam Integer donorId) throws BloodException {
        Donor donor = donorService.findById(donorId);
        if (donor != null) {
            appointment.setStatus(AppointmentStatus.SCHEDULED);
            appointment.setDonor(donor);
            appointService.saveAppointment(appointment);
            return new ResponseEntity<>("Created Successfully", HttpStatus.CREATED);
        } else {
            throw new BloodException("Can't Find Donor with ID: " + donorId);
        }
    }

    /**
     * GET endpoint to retrieve a list of all appointments.
     *
     * @return A list of appointments in JSON format.
     */
    @GetMapping("showAppointment")
    public ResponseEntity<List<Appointment>> showAppointment() throws BloodException {
        List<Appointment> findAll = appointService.findAll();
        return new ResponseEntity<>(findAll, HttpStatus.OK);
    }

    /**
     * GET endpoint to retrieve a specific appointment by ID.
     *
     * @param appointmentId The unique identifier of the appointment.
     * @return The requested appointment in JSON format.
     */
    @GetMapping("{appointmentId}")
    public ResponseEntity<Appointment> showByIdAppointment(@PathVariable("appointmentId") Integer appointmentId)
            throws BloodException {
        Appointment appointment = appointService.findById(appointmentId);
        return new ResponseEntity<>(appointment, HttpStatus.OK);
    }

    /**
     * DELETE endpoint to delete an appointment by ID.
     *
     * @param appointmentId The unique identifier of the appointment to be deleted.
     * @return A response indicating the success or failure of the operation.
     */
    @Transactional
    @DeleteMapping("{appointmentId}")
    public ResponseEntity<?> deleteAppointment(@PathVariable("appointmentId") Integer appointmentId)
            throws BloodException {
        appointService.deleteAppointment(appointmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * PATCH endpoint to update an existing appointment.
     *
     * @param updatedAppointment The updated appointment object.
     * @param appointmentId          The unique identifier of the appointment to be
     *                           updated.
     * @return A response indicating the success or failure of the operation.
     */
    @Transactional
    @PatchMapping("updateAppointment")
    public ResponseEntity<String> updateAppointment(@RequestBody Appointment updatedAppointment,
            @RequestParam("appointmentId") Integer appointmentId) throws BloodException {
        Appointment existAppoint = appointService.findById(appointmentId);
        if (existAppoint != null) {
            existAppoint.setAppointmentDate(updatedAppointment.getAppointmentDate());
            existAppoint.setAppointmentTime(updatedAppointment.getAppointmentTime());
            existAppoint.setLocation(updatedAppointment.getLocation());
            existAppoint.setStatus(updatedAppointment.getStatus());

            appointService.updateAppointment(existAppoint);
            return new ResponseEntity<>("Updated Successfully", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("Appointment not found", HttpStatus.NOT_FOUND);
    }
}
