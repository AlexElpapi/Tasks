package com.greenwave.todolist.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.greenwave.todolist.model.Comment;
import com.greenwave.todolist.model.Task;
import com.greenwave.todolist.model.User;
import com.greenwave.todolist.repository.CommentRepository;
import com.greenwave.todolist.repository.TaskRepository;
import com.greenwave.todolist.repository.UserRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       CommentRepository commentRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    // 🔹 Task condivise
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // 🔹 Aggiungi un nuovo task
    public void addTask(String creatoreUsername, String titolo, LocalDateTime deadline, Long assegnatoId) {
        Task task = new Task();
        task.setTitle(titolo);
        task.setDeadline(deadline);
    
        User creatore = userRepository.findByUsername(creatoreUsername);
        task.setUser(creatore); // chi crea
    
        if (assegnatoId != null) {
            userRepository.findById(assegnatoId).ifPresent(task::setAssegnato);
        }
    
        taskRepository.save(task);
    }
    

    // 🔹 Modifica task (solo Manager)
    public void updateTask(Long id, String title, String deadline, String tag) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setTitle(title);
            task.setDeadline(LocalDateTime.parse(deadline));
            task.setTag(tag);
            taskRepository.save(task);
        }
    }

    // 🔹 Completa task
    public void completeTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null) {
            task.setCompleted(true);
            taskRepository.save(task);
        }
    }

    // 🔹 Elimina task
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    // 🔹 Aggiungi un commento (User o Manager)
    public void addComment(Long taskId, String content, String username) {
        Task task = taskRepository.findById(taskId).orElse(null);
        User user = userRepository.findByUsername(username);
        if (task != null && user != null) {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setCreatedAt(LocalDateTime.now());
            comment.setTask(task);
            comment.setUser(user);
            commentRepository.save(comment);
        }
    }
}
