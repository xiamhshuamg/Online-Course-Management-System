package org.example.end.web;

import jakarta.validation.Valid;
import java.util.List;
import org.example.end.dto.learning.AssignmentResponse;
import org.example.end.dto.learning.QuestionResponse;
import org.example.end.dto.learning.SubmissionCreateRequest;
import org.example.end.dto.learning.SubmissionResponse;
import org.example.end.security.SecurityUtils;
import org.example.end.service.LearningService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
// 学生学习控制器：负责作业、考试、提交
public class StudentLearningController {

    private final LearningService learningService;

    public StudentLearningController(LearningService learningService) {
        this.learningService = learningService;
    }

    @GetMapping("/courses/{courseId}/assignments")
    // 列出课程作业
    public List<AssignmentResponse> listAssignments(@PathVariable Long courseId) {
        return learningService.listAssignmentsForCourse(courseId);
    }

    // [王某新增] 获取学生所有课程的作业 (从 StudentController 迁移过来)
    @GetMapping("/assignments")
    public List<AssignmentResponse> listAllAssignments() {
        return learningService.listAllAssignmentsForStudent(SecurityUtils.currentUserId());
    }

    @GetMapping("/assignments/{assignmentId}/questions")
    // 列出作业问题
    public List<QuestionResponse> questions(@PathVariable Long assignmentId) {
        return learningService.listQuestions(assignmentId);
    }

    @PostMapping("/assignments/{assignmentId}/submit")
    // 学生提交作业
    public SubmissionResponse submit(@PathVariable Long assignmentId, @Valid @RequestBody SubmissionCreateRequest req) {
        return learningService.studentSubmit(SecurityUtils.currentUserId(), assignmentId, req);
    }

    @GetMapping("/assignments/{assignmentId}/my-submission")
    // 我的提交
    public SubmissionResponse mySubmission(@PathVariable Long assignmentId) {
        return learningService.mySubmission(SecurityUtils.currentUserId(), assignmentId);
    }
}