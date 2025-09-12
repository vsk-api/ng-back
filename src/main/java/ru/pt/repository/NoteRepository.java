package ru.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pt.domain.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
}


