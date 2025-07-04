package com.example.demo.service;

import com.example.demo.model.TimeEntry;
import com.example.demo.repository.TimeEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TimeEntryService {
    @Autowired
    private TimeEntryRepository timeEntryRepository;

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public TimeEntry addTimeEntry(TimeEntry entry) {
        // Calcul automatique des heures travaillées
        LocalTime start = LocalTime.parse(entry.getStartTime(), TIME_FORMAT);
        LocalTime end = LocalTime.parse(entry.getEndTime(), TIME_FORMAT);
        double hours = Duration.between(start, end).toMinutes() / 60.0;
        entry.setHoursWorked(hours);
        return timeEntryRepository.save(entry);
    }

    public List<TimeEntry> getEntriesForUser(String userId) {
        return timeEntryRepository.findByUserId(userId);
    }

    public List<TimeEntry> getEntriesForUserBetween(String userId, String startDay, String endDay) {
        return timeEntryRepository.findByUserIdAndDayBetween(userId, startDay, endDay);
    }
} 