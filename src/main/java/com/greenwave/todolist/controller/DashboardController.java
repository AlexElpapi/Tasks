package com.greenwave.todolist.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenwave.todolist.model.Task;
import com.greenwave.todolist.service.TaskService;
import com.greenwave.todolist.service.UserService;

@Controller
public class DashboardController {

    private final TaskService taskService;
    private final UserService userService;

    public DashboardController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    // DASHBOARD
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam(value = "filter", defaultValue = "all") String filter,
                            @RequestParam(value = "sort", defaultValue = "deadline") String sort,
                            Model model) {
    
        String username = userDetails.getUsername();
        boolean isManager = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER"));
    
        // 👨‍💼 Manager vede tutto - gli altri solo i propri task assegnati
        List<Task> allTasks = isManager
                ? taskService.getAllTasks()
                : taskService.getAllTasks().stream()
                    .filter(t -> t.getAssegnato() != null && t.getAssegnato().getUsername().equals(username))
                    .toList();
    
        // Statistiche e struttura view
        int total = allTasks.size();
        int completed = (int) allTasks.stream().filter(Task::isCompleted).count();
        int active = total - completed;
        int urgent = (int) allTasks.stream()
                .filter(t -> t.getDeadline() != null && !t.isCompleted()
                        && t.getDeadline().isBefore(LocalDateTime.now().plusHours(24)))
                .count();
    
        model.addAttribute("totalTasks", total);
        model.addAttribute("completedTasks", completed);
        model.addAttribute("activeTasks", active);
        model.addAttribute("urgentTasks", urgent);
        model.addAttribute("utenti", userService.findAll());
        model.addAttribute("username", username);

    
    
        // Preparazione struttura task per dashboard
       List<Map<String, Object>> calendarEvents = allTasks.stream()
        .filter(task -> task.getDeadline() != null)
        .map(task -> {
            Map<String, Object> map = new HashMap<>();
            map.put("title", task.getTitle());
            map.put("start", task.getDeadline().toString()); // ISO 8601
            return map;
        })
        .collect(Collectors.toList());

    ObjectMapper objectMapper = new ObjectMapper();
    try {
        String calendarEventsJson = objectMapper.writeValueAsString(calendarEvents);
        model.addAttribute("calendarEventsJson", calendarEventsJson);
    } catch (Exception e) {
        model.addAttribute("calendarEventsJson", "[]");
    }

    return "dashboard";
}
    


    // AGGIUNTA TASK
    @PostMapping("/dashboard")
public String addTask(@AuthenticationPrincipal UserDetails userDetails,
                      @RequestParam String taskTitle,
                      @RequestParam String deadline,
                      @RequestParam(required = false) Long assegnatoId,
                      RedirectAttributes redirectAttributes) {

    LocalDateTime parsedDeadline = LocalDateTime.parse(deadline);

    // Ricava l'utente assegnato dal servizio
    taskService.addTask(userDetails.getUsername(), taskTitle, parsedDeadline, assegnatoId);

    redirectAttributes.addFlashAttribute("successMessage", "Task aggiunto con successo!");
    return "redirect:/dashboard";
}

    // COMPLETA TASK
    @PostMapping("/task/complete/{id}")
    public String completeTask(@PathVariable("id") Long taskId,
                               RedirectAttributes redirectAttributes) {
        taskService.completeTask(taskId);
        redirectAttributes.addFlashAttribute("successMessage", "Task completato!");
        return "redirect:/dashboard";
    }

    // ELIMINA TASK
    @PostMapping("/task/delete/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public String deleteTask(@PathVariable("id") Long taskId,
                             RedirectAttributes redirectAttributes) {
        taskService.deleteTask(taskId);
        redirectAttributes.addFlashAttribute("successMessage", "Task eliminato!");
        return "redirect:/dashboard";
    }

    // MODIFICA TASK
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/task/update/{id}")
    public String updateTask(@PathVariable Long id,
                         @RequestParam String title,
                         @RequestParam String deadline,
                         @RequestParam(required = false) String tag,
                         RedirectAttributes redirectAttributes) {
    taskService.updateTask(id, title, deadline, tag);
    redirectAttributes.addFlashAttribute("successMessage", "Task aggiornato con successo!");
    return "redirect:/dashboard";
}


    // AGGIUNTA COMMENTO
    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    @PostMapping("/task/{id}/comment")
    public String addComment(@PathVariable("id") Long taskId,
                             @RequestParam String content,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        taskService.addComment(taskId, content, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Commento aggiunto!");
        return "redirect:/dashboard";
    }
}
