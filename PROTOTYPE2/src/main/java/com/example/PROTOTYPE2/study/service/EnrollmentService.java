package com.example.PROTOTYPE2.study.service;

import com.example.PROTOTYPE2.study.dto.EnrollmentRequest;
import com.example.PROTOTYPE2.study.dto.EnrollmentResponse;
import com.example.PROTOTYPE2.study.entity.Enrollment;
import com.example.PROTOTYPE2.study.entity.Participant;
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

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             ParticipantRepository participantRepository,
                             StudyService studyService) {
        this.enrollmentRepository = enrollmentRepository;
        this.participantRepository = participantRepository;
        this.studyService = studyService;
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

        Enrollment enrollment = new Enrollment(study, participant);
        return EnrollmentResponse.from(enrollmentRepository.save(enrollment));
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getByStudy(Long studyId) {
        studyService.findOrThrow(studyId);
        return enrollmentRepository.findByStudyId(studyId).stream()
                .map(EnrollmentResponse::from)
                .toList();
    }
}
