package com.example.PROTOTYPE2.study.repository;

import com.example.PROTOTYPE2.study.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudyId(Long studyId);
    boolean existsByStudyIdAndParticipantId(Long studyId, Long participantId);
}
