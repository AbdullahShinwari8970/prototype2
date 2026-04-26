package com.example.PROTOTYPE2.study.service;

import com.example.PROTOTYPE2.participant.scheduler.TokenScheduler;
import com.example.PROTOTYPE2.study.dto.EnrollmentRequest;
import com.example.PROTOTYPE2.study.dto.EnrollmentResponse;
import com.example.PROTOTYPE2.study.entity.Enrollment;
import com.example.PROTOTYPE2.study.entity.Participant;
import com.example.PROTOTYPE2.study.entity.ScheduleType;
import com.example.PROTOTYPE2.study.entity.Study;
import com.example.PROTOTYPE2.study.repository.EnrollmentRepository;
import com.example.PROTOTYPE2.study.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final ParticipantRepository participantRepository;
    private final StudyService studyService;
    private final TokenScheduler tokenScheduler;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             ParticipantRepository participantRepository,
                             StudyService studyService,
                             TokenScheduler tokenScheduler) {
        this.enrollmentRepository  = enrollmentRepository;
        this.participantRepository = participantRepository;
        this.studyService          = studyService;
        this.tokenScheduler        = tokenScheduler;
    }

    @Transactional
    public EnrollmentResponse enroll(Long studyId, EnrollmentRequest request) {
        Study study = studyService.findOrThrow(studyId);

        // Reuse existing participant if email already exists, otherwise create new
        Participant participant = participantRepository.findByEmail(request.getEmail())
                .orElseGet(() -> participantRepository.save(
                        new Participant(request.getName(), request.getEmail())));

        // Prevent duplicate enrollment
        if (enrollmentRepository.existsByStudyIdAndParticipantId(studyId, participant.getId())) {
            throw new IllegalArgumentException("Participant is already enrolled in this study");
        }

        Enrollment enrollment = enrollmentRepository.save(new Enrollment(study, participant));

        // If study is already live, send ONE_TIME tokens immediately.
        // DAILY/WEEKLY/MONTHLY are picked up by the scheduler on their next run.
        if ("ACTIVE".equals(study.getStatus())) {
            study.getSurveys().stream()
                    .filter(s -> s.getScheduleType() == ScheduleType.ONE_TIME)
                    .forEach(s -> tokenScheduler.createAndSendToken(enrollment, s));
        }

        return EnrollmentResponse.from(enrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getByStudy(Long studyId) {
        studyService.findOrThrow(studyId);
        return enrollmentRepository.findByStudyId(studyId).stream()
                .map(EnrollmentResponse::from)
                .toList();
    }

    @Transactional
    public EnrollmentResponse withdraw(Long studyId, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found: " + enrollmentId));

        if (!enrollment.getStudy().getId().equals(studyId)) {
            throw new IllegalArgumentException("Enrollment does not belong to this study");
        }

        if ("WITHDRAWN".equals(enrollment.getStatus())) {
            throw new IllegalArgumentException("Participant is already withdrawn");
        }

        enrollment.setStatus("WITHDRAWN");
        return EnrollmentResponse.from(enrollmentRepository.save(enrollment));
    }
}
