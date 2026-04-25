package com.example.PROTOTYPE2.participant.scheduler;

import com.example.PROTOTYPE2.participant.entity.SurveyToken;
import com.example.PROTOTYPE2.participant.repository.SurveyTokenRepository;
import com.example.PROTOTYPE2.participant.service.EmailService;
import com.example.PROTOTYPE2.study.entity.Enrollment;
import com.example.PROTOTYPE2.study.entity.ScheduleType;
import com.example.PROTOTYPE2.study.entity.Survey;
import com.example.PROTOTYPE2.study.repository.EnrollmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class TokenScheduler {

    private static final Logger log = LoggerFactory.getLogger(TokenScheduler.class);

    private final EnrollmentRepository  enrollmentRepository;
    private final SurveyTokenRepository surveyTokenRepository;
    private final EmailService          emailService;

    public TokenScheduler(EnrollmentRepository enrollmentRepository,
                          SurveyTokenRepository surveyTokenRepository,
                          EmailService emailService) {
        this.enrollmentRepository  = enrollmentRepository;
        this.surveyTokenRepository = surveyTokenRepository;
        this.emailService          = emailService;
    }

    /**
     * Runs at the top of every hour.
     * Loops through every ACTIVE enrollment, checks each survey's schedule type,
     * and generates a new token if one is due.
     *
     * INSTANT surveys are skipped here — they are handled immediately at enrolment time
     * by EnrollmentService calling createAndSendToken().
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void generateScheduledTokens() {
        log.info("TokenScheduler: starting run");

        List<Enrollment> activeEnrollments = enrollmentRepository.findByStatus("ACTIVE");
        int generated = 0;

        for (Enrollment enrollment : activeEnrollments) {
            // Skip enrollments whose study hasn't been deployed yet
            if (!"ACTIVE".equals(enrollment.getStudy().getStatus())) continue;

            for (Survey survey : enrollment.getStudy().getSurveys()) {
                ScheduleType type = survey.getScheduleType();

                // INSTANT / ONE_TIME are handled at deploy/enrol time — skip recurring check
                if (type == ScheduleType.INSTANT || type == ScheduleType.ONE_TIME) continue;

                if (isDue(enrollment, survey, type)) {
                    createAndSendToken(enrollment, survey);
                    generated++;
                }
            }
        }

        log.info("TokenScheduler: done — {} token(s) generated", generated);
    }

    /**
     * Creates a token and sends the survey email.
     * Called by StudyService.deploy() for all surveys on first deployment,
     * by EnrollmentService when a participant joins an already-ACTIVE study,
     * and by the scheduler for recurring (DAILY/WEEKLY/MONTHLY) subsequent sends.
     */
    @Transactional
    public void createAndSendToken(Enrollment enrollment, Survey survey) {
        LocalDateTime expiresAt = switch (survey.getScheduleType()) {
            case INSTANT, ONE_TIME -> LocalDateTime.now().plusDays(7);
            case DAILY             -> LocalDateTime.now().plusDays(1);
            case WEEKLY            -> LocalDateTime.now().plusDays(7);
            case MONTHLY           -> LocalDateTime.now().plusDays(30);
        };

        SurveyToken token = surveyTokenRepository.save(
                new SurveyToken(enrollment, survey, expiresAt));

        emailService.sendSurveyLink(
                enrollment.getParticipant().getEmail(),
                enrollment.getParticipant().getName(),
                token.getToken(),
                survey.getName()
        );

        log.info("Token created for enrollment {} / survey '{}'",
                enrollment.getId(), survey.getName());
    }


    /**
     * Decides whether a new token should be generated for a given enrollment + survey.
     */
    private boolean isDue(Enrollment enrollment, Survey survey, ScheduleType type) {
        Long enrollmentId = enrollment.getId();
        Long surveyId     = survey.getId();

        // Only fire at the researcher's configured send hour (default 9am if not set)
        int sendHour = survey.getSendHour() != null ? survey.getSendHour() : 9;
        if (LocalDateTime.now().getHour() != sendHour) return false;

        // Never stack a second PENDING token on top of an existing one
        if (surveyTokenRepository.existsByEnrollmentIdAndSurveyIdAndStatus(enrollmentId, surveyId, "PENDING")) {
            return false;
        }

        // Check how long ago the last token was prompted
        Optional<SurveyToken> last = surveyTokenRepository
                .findTopByEnrollmentIdAndSurveyIdOrderByPromptedAtDesc(enrollmentId, surveyId);

        if (last.isEmpty()) return true; // never been sent before — send now

        LocalDateTime lastPrompted = last.get().getPromptedAt();
        LocalDateTime now          = LocalDateTime.now();

        return switch (type) {
            case DAILY   -> lastPrompted.isBefore(now.minusDays(1));
            case WEEKLY  -> lastPrompted.isBefore(now.minusDays(7));
            case MONTHLY -> lastPrompted.isBefore(now.minusDays(30));
            default      -> false;
        };
    }
}
