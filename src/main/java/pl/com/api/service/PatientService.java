package pl.com.api.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.com.api.model.Patient;
import pl.com.api.repository.PatientRepository;

import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public Patient addPatient(String name, String surname, String pesel) {
        Patient patient = new Patient(name, surname, pesel);
        return patientRepository.save(patient);
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Patient getPatientByPesel(String pesel) {
        return patientRepository.findByPesel(pesel);
    }
}
