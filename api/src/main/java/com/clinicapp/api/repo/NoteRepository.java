package com.clinicapp.api.repo;

import com.clinicapp.api.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoteRepository extends MongoRepository<Note, String> {
}
