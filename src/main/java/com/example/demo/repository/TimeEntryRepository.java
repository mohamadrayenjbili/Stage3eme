package com.example.demo.repository;

import com.example.demo.model.TimeEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TimeEntryRepository extends MongoRepository<TimeEntry, String> {
    List<TimeEntry> findByUserId(String userId);
    List<TimeEntry> findByUserIdAndDayBetween(String userId, String startDay, String endDay);
} 