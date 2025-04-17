package com.greenwave.todolist.repository;

import com.greenwave.todolist.model.Comment;
import com.greenwave.todolist.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTask(Task task); // opzionale, se ti serve filtrare per task
}
