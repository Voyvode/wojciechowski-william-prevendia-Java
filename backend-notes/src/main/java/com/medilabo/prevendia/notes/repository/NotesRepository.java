package com.medilabo.prevendia.notes.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.medilabo.prevendia.notes.model.Note;

/**
 * Repository interface for managing Note entities in the NoSQL database.
 */
@Repository
public interface NotesRepository extends MongoRepository<Note, String> {

	List<Note> findByPatId(Long patId);

}