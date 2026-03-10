package org.example.end.web;

import jakarta.validation.Valid;
import java.util.List;
import org.example.end.dto.learning.AssignmentCreateRequest;
import org.example.end.dto.learning.AssignmentResponse;
import org.example.end.dto.learning.GradeRequest;
import org.example.end.dto.learning.QuestionCreateRequest;
import org.example.end.dto.learning.QuestionResponse;
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
@RequestMapping("/api/teacher")
// 教师学习控制器       
public class TeacherLearningController {

    private final LearningService learningService;

    public TeacherLearningController(LearningService learningService) {
        this.learningService = learningService;
    }

    @PostMapping("/courses/{courseId}/assignments")
    // 创建作业
    public AssignmentResponse createAssignment(@PathVariable Long courseId, @Valid @RequestBody AssignmentCreateRequest req) {
        return learningService.teacherCreateAssignment(SecurityUtils.currentUserId(), courseId, req);
    }

    @PostMapping("/assignments/{assignmentId}/questions")
    // 添加问题
    public QuestionResponse addQuestion(@PathVariable Long assignmentId, @Valid @RequestBody QuestionCreateRequest req) {
        return learningService.teacherAddQuestion(SecurityUtils.currentUserId(), assignmentId, req);
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    // 列出指定作业的提交
    public List<SubmissionResponse> submissions(@PathVariable Long assignmentId) {
        return learningService.teacherListSubmissions(SecurityUtils.currentUserId(), assignmentId);
    }

    // [新增] 列出该教师所有课程的提交记录（大聚合）
    // [王某注释] 这是一个聚合接口，前端 TeacherGrading.vue 不传 ID 时会调用此接口
    // 对应 LearningService.teacherListAllSubmissions
    @GetMapping("/submissions")
    public List<SubmissionResponse> allSubmissions() {
        return learningService.teacherListAllSubmissions(SecurityUtils.currentUserId());
    }

    @PostMapping("/submissions/{submissionId}/grade")
    // 评分作业
    public SubmissionResponse grade(@PathVariable Long submissionId, @Valid @RequestBody GradeRequest req) {
        return learningService.teacherGrade(SecurityUtils.currentUserId(), submissionId, req);
    }

    @GetMapping("/courses/{courseId}/assignments")
    // 列出课程作业
    public List<AssignmentResponse> listAssignments(@PathVariable Long courseId) {
        return learningService.listAssignmentsForCourse(courseId);
    }
}