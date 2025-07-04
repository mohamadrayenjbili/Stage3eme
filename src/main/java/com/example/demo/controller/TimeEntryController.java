package com.example.demo.controller;

import com.example.demo.model.TimeEntry;
import com.example.demo.service.TimeEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/time-entry")
public class TimeEntryController {
    @Autowired
    private TimeEntryService timeEntryService;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Ajouter une saisie d'heure
    @PostMapping
    public ResponseEntity<?> addTimeEntry(@RequestBody TimeEntry entry, HttpSession session) {
        // Récupérer l'utilisateur connecté
        var user = (com.example.demo.model.User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        entry.setUserId(user.getId());
        TimeEntry saved = timeEntryService.addTimeEntry(entry);
        return ResponseEntity.ok(saved);
    }

    // Heures de la journée
    @GetMapping("/day/{date}")
    public ResponseEntity<?> getDay(@PathVariable String date, HttpSession session) {
        var user = (com.example.demo.model.User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).body("Not authenticated");
        List<TimeEntry> entries = timeEntryService.getEntriesForUserBetween(user.getId(), date, date);
        double total = entries.stream().mapToDouble(TimeEntry::getHoursWorked).sum();
        return ResponseEntity.ok(new DaySummary(date, total, entries));
    }

    // Heures de la semaine
    @GetMapping("/week/{startDay}/{endDay}")
    public ResponseEntity<?> getWeek(@PathVariable String startDay, @PathVariable String endDay, HttpSession session) {
        var user = (com.example.demo.model.User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).body("Not authenticated");
        List<TimeEntry> entries = timeEntryService.getEntriesForUserBetween(user.getId(), startDay, endDay);
        double total = entries.stream().mapToDouble(TimeEntry::getHoursWorked).sum();
        return ResponseEntity.ok(new PeriodSummary(startDay, endDay, total, entries));
    }

    // Heures du mois
    @GetMapping("/month/{yearMonth}")
    public ResponseEntity<?> getMonth(@PathVariable String yearMonth, HttpSession session) {
        var user = (com.example.demo.model.User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).body("Not authenticated");
        // yearMonth format: yyyy-MM
        String start = yearMonth + "-01";
        String end = yearMonth + "-31";
        List<TimeEntry> entries = timeEntryService.getEntriesForUserBetween(user.getId(), start, end);
        double total = entries.stream().mapToDouble(TimeEntry::getHoursWorked).sum();
        return ResponseEntity.ok(new PeriodSummary(start, end, total, entries));
    }

    // Heures totales
    @GetMapping("/total")
    public ResponseEntity<?> getTotal(HttpSession session) {
        var user = (com.example.demo.model.User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).body("Not authenticated");
        List<TimeEntry> entries = timeEntryService.getEntriesForUser(user.getId());
        double total = entries.stream().mapToDouble(TimeEntry::getHoursWorked).sum();
        return ResponseEntity.ok(new TotalSummary(total, entries));
    }

    // Classes internes pour les réponses
    static class DaySummary {
        public String day;
        public double totalHours;
        public List<TimeEntry> entries;
        public DaySummary(String day, double totalHours, List<TimeEntry> entries) {
            this.day = day;
            this.totalHours = totalHours;
            this.entries = entries;
        }
    }
    static class PeriodSummary {
        public String startDay, endDay;
        public double totalHours;
        public List<TimeEntry> entries;
        public PeriodSummary(String startDay, String endDay, double totalHours, List<TimeEntry> entries) {
            this.startDay = startDay;
            this.endDay = endDay;
            this.totalHours = totalHours;
            this.entries = entries;
        }
    }
    static class TotalSummary {
        public double totalHours;
        public List<TimeEntry> entries;
        public TotalSummary(double totalHours, List<TimeEntry> entries) {
            this.totalHours = totalHours;
            this.entries = entries;
        }
    }
} 