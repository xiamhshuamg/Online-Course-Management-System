package org.example.end.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.example.end.domain.Assignment;
import org.example.end.domain.Course;
import org.example.end.domain.Question;
import org.example.end.domain.Submission;
import org.example.end.domain.TeacherProfile;
import org.example.end.domain.User;
import org.example.end.domain.enums.EnrollmentStatus;
import org.example.end.domain.enums.SubmissionStatus;
import org.example.end.domain.enums.TeacherVerificationStatus;
import org.example.end.dto.learning.AssignmentCreateRequest;
import org.example.end.dto.learning.AssignmentResponse;
import org.example.end.dto.learning.GradeRequest;
import org.example.end.dto.learning.QuestionCreateRequest;
import org.example.end.dto.learning.QuestionResponse;
import org.example.end.dto.learning.SubmissionCreateRequest;
import org.example.end.dto.learning.SubmissionResponse;
import org.example.end.exception.ApiException;
import org.example.end.repository.AssignmentRepository;
import org.example.end.repository.CourseRepository;
import org.example.end.repository.EnrollmentRepository;
import org.example.end.repository.QuestionRepository;
import org.example.end.repository.SubmissionRepository;
import org.example.end.repository.TeacherProfileRepository;
import org.example.end.repository.UserRepository;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
// 学习服务
public class LearningService {

    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;
    private final QuestionRepository questionRepository;
    private final SubmissionRepository submissionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final TeacherProfileRepository teacherProfileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public LearningService(
            CourseRepository courseRepository,
            AssignmentRepository assignmentRepository,
            QuestionRepository questionRepository,
            SubmissionRepository submissionRepository,
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository,
            TeacherProfileRepository teacherProfileRepository
    ) {
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.questionRepository = questionRepository;
        this.submissionRepository = submissionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.teacherProfileRepository = teacherProfileRepository;
    }

    @Transactional
    // 创建作业
    public AssignmentResponse teacherCreateAssignment(Long teacherId, Long courseId, AssignmentCreateRequest req) {
        requireVerifiedTeacher(teacherId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your course");
        }

        Assignment a = assignmentRepository.save(Assignment.builder()
                .course(course)
                .type(req.getType())
                .title(req.getTitle())
                .description(req.getDescription())
                .dueAt(req.getDueAt())
                .maxScore(req.getMaxScore())
                .build());

        return toAssignmentResponse(a);
    }

    @Transactional
    // 添加问题
    public QuestionResponse teacherAddQuestion(Long teacherId, Long assignmentId, QuestionCreateRequest req) {
        requireVerifiedTeacher(teacherId);
        Assignment a = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Assignment not found"));
        if (!a.getCourse().getTeacher().getId().equals(teacherId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your course");
        }

        Question q = questionRepository.save(Question.builder()
                .assignment(a)
                .type(req.getType())
                .prompt(req.getPrompt())
                .optionsJson(normalizeToJsonOrNull(req.getOptionsJson()))
                .answerKeyJson(normalizeToJsonOrNull(req.getAnswerKeyJson()))
                .score(req.getScore())
                .build());

        return toQuestionResponse(q);
    }

    @Transactional(readOnly = true)
    // 列出课程作业
    public List<AssignmentResponse> listAssignmentsForCourse(Long courseId) {
        return assignmentRepository.findByCourseIdOrderByUpdatedAtDesc(courseId)
                .stream()
                .map(this::toAssignmentResponse)
                .toList();
    }

    // 列出学生所有课程的作业
    @Transactional(readOnly = true)
    public List<AssignmentResponse> listAllAssignmentsForStudent(Long studentId) {
        List<Long> courseIds = enrollmentRepository.findByStudentIdAndStatusOrderByUpdatedAtDesc(studentId, EnrollmentStatus.ENROLLED)
                .stream()
                .map(e -> e.getCourse().getId())
                .toList();

        if (courseIds.isEmpty()) {
            return List.of();
        }
        return assignmentRepository.findByCourseIdInOrderByDueAtAsc(courseIds)
                .stream()
                .map(this::toAssignmentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    // 列出作业问题
    public List<QuestionResponse> listQuestions(Long assignmentId) {
        return questionRepository.findByAssignmentIdOrderByIdAsc(assignmentId)
                .stream()
                .map(this::toQuestionResponse)
                .toList();
    }

    @Transactional
    // 学生提交作业
    public SubmissionResponse studentSubmit(Long studentId, Long assignmentId, SubmissionCreateRequest req) {
        Assignment a = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Assignment not found"));

        enrollmentRepository.findByCourseIdAndStudentId(a.getCourse().getId(), studentId)
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "Not enrolled"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        Submission s = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElseGet(() -> Submission.builder()
                        .assignment(a)
                        .student(student)
                        .status(SubmissionStatus.SUBMITTED)
                        .submittedAt(Instant.now())
                        .contentJson(req.getContentJson())
                        .build());

        s.setContentJson(req.getContentJson());
        s.setSubmittedAt(Instant.now());
        s.setStatus(SubmissionStatus.SUBMITTED);
        // 如果是重新提交，重置评分状态
        s.setScore(null);
        s.setFeedback(null);
        s.setGradedAt(null);
        s.setGradedBy(null);

        s = submissionRepository.save(s);
        return toSubmissionResponse(s);
    }

    @Transactional(readOnly = true)
    // 我的提交
    public SubmissionResponse mySubmission(Long studentId, Long assignmentId) {
        Optional<Submission> s = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId);
        return s.map(this::toSubmissionResponse).orElse(null);
    }

    @Transactional(readOnly = true)
    // 列出单个作业的提交
    public List<SubmissionResponse> teacherListSubmissions(Long teacherId, Long assignmentId) {
        requireVerifiedTeacher(teacherId);
        Assignment a = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Assignment not found"));
        if (!a.getCourse().getTeacher().getId().equals(teacherId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your course");
        }
        return submissionRepository.findByAssignmentIdOrderByUpdatedAtDesc(assignmentId)
                .stream()
                .map(this::toSubmissionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> teacherListAllSubmissions(Long teacherId) {
        requireVerifiedTeacher(teacherId);
        return submissionRepository.findByAssignment_Course_Teacher_IdOrderBySubmittedAtDesc(teacherId)
                .stream()
                .map(this::toSubmissionResponse)
                .toList();
    }

    @Transactional
    // 评分作业
    public SubmissionResponse teacherGrade(Long teacherId, Long submissionId, GradeRequest req) {
        requireVerifiedTeacher(teacherId);
        Submission s = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Submission not found"));
        if (!s.getAssignment().getCourse().getTeacher().getId().equals(teacherId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your course");
        }

        User grader = userRepository.findById(teacherId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        s.setScore(req.getScore());
        s.setFeedback(req.getFeedback());
        s.setStatus(SubmissionStatus.GRADED);
        s.setGradedAt(Instant.now());
        s.setGradedBy(grader);
        s = submissionRepository.save(s);
        return toSubmissionResponse(s);
    }

    private void requireVerifiedTeacher(Long teacherId) {
        TeacherProfile p = teacherProfileRepository.findByUserId(teacherId)
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "Teacher profile not found"));
        if (p.getVerificationStatus() != TeacherVerificationStatus.APPROVED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Teacher is not verified yet");
        }
    }

    private AssignmentResponse toAssignmentResponse(Assignment a) {
        return AssignmentResponse.builder()
                .id(a.getId())
                .courseId(a.getCourse().getId())
                .type(a.getType().name())
                .title(a.getTitle())
                .description(a.getDescription())
                .dueAt(a.getDueAt())
                .maxScore(a.getMaxScore())
                .build();
    }

    private QuestionResponse toQuestionResponse(Question q) {
        return QuestionResponse.builder()
                .id(q.getId())
                .assignmentId(q.getAssignment().getId())
                .type(q.getType().name())
                .prompt(q.getPrompt())
                .optionsJson(q.getOptionsJson())
                .score(q.getScore())
                .build();
    }

    private SubmissionResponse toSubmissionResponse(Submission s) {
        String studentName = "未知学生";
        if (s.getStudent() != null) {
            studentName = s.getStudent().getName();
        }
        String assignmentTitle = "未知作业";
        if (s.getAssignment() != null) {
            assignmentTitle = s.getAssignment().getTitle();
        }

        return SubmissionResponse.builder()
                .id(s.getId())
                .assignmentId(s.getAssignment().getId())
                .assignmentTitle(assignmentTitle)
                .studentId(s.getStudent() != null ? s.getStudent().getId() : 0L)
                .studentName(studentName)
                .status(s.getStatus().name())
                .score(s.getScore())
                .feedback(s.getFeedback())
                .submittedAt(s.getSubmittedAt())
                .gradedAt(s.getGradedAt())
                // [关键修复] 将实体类中的 contentJson 映射到 DTO
                // 如果这里不赋值，前端收到的就是 null
                .contentJson(s.getContentJson())
                .build();
    }

    private String normalizeToJsonOrNull(String raw) {
        if (raw == null) return null;

        String s = raw.trim();
        if (s.isEmpty()) return null;

        try {
            objectMapper.readTree(s);
            return s;
        } catch (Exception ignore) {
            try {
                return objectMapper.writeValueAsString(s);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
    }
}