package com.psp.todo.web;

import com.psp.todo.model.*;
import com.psp.todo.repo.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Controller
@RequiredArgsConstructor
public class TaskController {
    private final TaskRepository tasks;
    private final CategoryRepository cats;
    private final UserRepository users;

    @Data
    public static class TaskForm {
        private Long id;
        @NotBlank(message = "Give the task a title") @Size(max = 150, message = "Keep the title under 150 characters")
        private String title;
        @Size(max = 2000, message = "Keep the description under 2000 characters")
        private String description;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate dueDate;
        @NotNull
        private Priority priority = Priority.MEDIUM;
        private Long categoryId;
    }

    private User me(Principal p) {
        return users.findByUsername(p.getName()).orElseThrow();
    }

    @GetMapping("/dashboard")
    String dashboard(Principal p, Model m,
                     @RequestParam(defaultValue = "all") String view,
                     @RequestParam(required = false) String q,
                     @RequestParam(required = false) Long cat,
                     @RequestParam(required = false) Priority pr,
                     @RequestParam(defaultValue = "due") String sort) {
        User u = me(p);
        List<Task> all = tasks.findByUserId(u.getId());
        LocalDate today = LocalDate.now();
        String needle = q == null ? "" : q.trim().toLowerCase();

        Predicate<Task> byView = switch (view) {
            case "today" -> t -> !t.isDone() && today.equals(t.getDueDate());
            case "upcoming" -> t -> !t.isDone() && t.getDueDate() != null && t.getDueDate().isAfter(today);
            case "overdue" -> Task::isOverdue;
            case "done" -> Task::isDone;
            default -> t -> true;
        };
        Comparator<Task> order = switch (sort) {
            case "priority" -> Comparator.comparing((Task t) -> t.getPriority().ordinal()).reversed();
            case "new" -> Comparator.comparing(Task::getCreatedAt).reversed();
            default -> Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.<LocalDate>naturalOrder()));
        };
        List<Task> list = all.stream().filter(byView)
                .filter(t -> needle.isEmpty() || t.getTitle().toLowerCase().contains(needle)
                        || (t.getDescription() != null && t.getDescription().toLowerCase().contains(needle)))
                .filter(t -> cat == null || (t.getCategory() != null && cat.equals(t.getCategory().getId())))
                .filter(t -> pr == null || t.getPriority() == pr)
                .sorted(order).toList();

        long done = all.stream().filter(Task::isDone).count();
        m.addAttribute("user", u);
        m.addAttribute("list", list);
        m.addAttribute("total", all.size());
        m.addAttribute("done", done);
        m.addAttribute("pending", all.size() - done);
        m.addAttribute("overdue", all.stream().filter(Task::isOverdue).count());
        m.addAttribute("pct", all.isEmpty() ? 0 : done * 100 / all.size());
        m.addAttribute("view", view);
        m.addAttribute("q", q);
        m.addAttribute("cat", cat);
        m.addAttribute("pr", pr);
        m.addAttribute("sort", sort);
        m.addAttribute("cats", cats.findAll());
        m.addAttribute("priorities", Priority.values());
        return "dashboard";
    }

    private String form(Model m) {
        m.addAttribute("cats", cats.findAll());
        m.addAttribute("priorities", Priority.values());
        return "task-form";
    }

    @GetMapping("/tasks/new")
    String newTask(Model m) {
        m.addAttribute("form", new TaskForm());
        return form(m);
    }

    @GetMapping("/tasks/{id}/edit")
    String edit(@PathVariable Long id, Principal p, Model m) {
        Task t = tasks.findByIdAndUserId(id, me(p).getId()).orElseThrow();
        TaskForm f = new TaskForm();
        f.setId(t.getId());
        f.setTitle(t.getTitle());
        f.setDescription(t.getDescription());
        f.setDueDate(t.getDueDate());
        f.setPriority(t.getPriority());
        f.setCategoryId(t.getCategory() == null ? null : t.getCategory().getId());
        m.addAttribute("form", f);
        return form(m);
    }

    @PostMapping("/tasks")
    String save(@Valid @ModelAttribute("form") TaskForm f, BindingResult br, Principal p, Model m) {
        if (br.hasErrors()) return form(m);
        User u = me(p);
        Task t = f.getId() == null ? new Task() : tasks.findByIdAndUserId(f.getId(), u.getId()).orElseThrow();
        t.setUser(u);
        t.setTitle(f.getTitle().trim());
        t.setDescription(f.getDescription() == null || f.getDescription().isBlank() ? null : f.getDescription().trim());
        t.setDueDate(f.getDueDate());
        t.setPriority(f.getPriority());
        t.setCategory(f.getCategoryId() == null ? null : cats.findById(f.getCategoryId()).orElse(null));
        tasks.save(t);
        return "redirect:/dashboard";
    }

    @PostMapping("/tasks/{id}/toggle")
    String toggle(@PathVariable Long id, Principal p) {
        Task t = tasks.findByIdAndUserId(id, me(p).getId()).orElseThrow();
        t.setDone(!t.isDone());
        tasks.save(t);
        return "redirect:/dashboard";
    }

    @PostMapping("/tasks/{id}/delete")
    String delete(@PathVariable Long id, Principal p) {
        tasks.delete(tasks.findByIdAndUserId(id, me(p).getId()).orElseThrow());
        return "redirect:/dashboard";
    }
}
