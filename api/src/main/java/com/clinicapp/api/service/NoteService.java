package com.clinicapp.api.service;

import com.clinicapp.api.model.Note;
import com.clinicapp.api.repo.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    public Note getNoteById(String id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Нотатку не знайдено"));
    }

    public Note createNote(Note note) {
        note.setId(null);
        return noteRepository.save(note);
    }

    public void deleteNote(String id) {
        if (!noteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Нотатку не знайдено");
        }
        noteRepository.deleteById(id);
    }

    public Note updateNote(String id, Note updatedNote) {
        return noteRepository.findById(id).map(existingNote -> {
            existingNote.setTitle(updatedNote.getTitle());
            existingNote.setContent(updatedNote.getContent());
            existingNote.setPriority(updatedNote.getPriority());
            existingNote.setCategory(updatedNote.getCategory());
            existingNote.setIsFavorite(updatedNote.getIsFavorite());
            existingNote.setEstimatedTime(updatedNote.getEstimatedTime());
            existingNote.setSourceUrl(updatedNote.getSourceUrl());

            return noteRepository.save(existingNote);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Нотатку не знайдено"));
    }
}