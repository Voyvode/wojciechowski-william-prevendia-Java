package com.medilabo.prevendia.patients.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medilabo.prevendia.patients.model.Patient;

/**
 * Repository interface for managing Patient entities in the database.
 */
@Repository
public interface PatientsRepository extends JpaRepository<Patient, Long> {

}
