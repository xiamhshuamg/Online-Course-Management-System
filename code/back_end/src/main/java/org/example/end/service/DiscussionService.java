package org.example.end.service;

import java.util.List;
import org.example.end.domain.Course;
import org.example.end.domain.DiscussionPost;
import org.example.end.domain.DiscussionReply;
import org.example.end.domain.TeacherProfile;
import org.example.end.domain.User;
import org.example.end.domain.enums.TeacherVerificationStatus;
import org.example.end.dto.discussion.PostCreateRequest;
import org.example.end.dto.discussion.PostResponse;
import org.example.end.dto.discussion.ReplyCreateRequest;
import org.example.end.dto.discussion.ReplyResponse;
import org.example.end.exception.ApiException;
import org.example.end.repository.CourseRepository;
import org.example.end.repository.DiscussionPostRepository;
import org.example.end.repository.DiscussionReplyRepository;
import org.example.end.repository.TeacherProfileRepository;
import org.example.end.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
// 讨论服务
public class DiscussionService {

    private final CourseRepository courseRepository;
    private final DiscussionPostRepository postRepository;
    private final DiscussionReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final TeacherProfileRepository teacherProfileRepository;

    public DiscussionService(
            CourseRepository courseRepository,
            DiscussionPostRepository postRepository,
            DiscussionReplyRepository replyRepository,
            UserRepository userRepository,
            TeacherProfileRepository teacherProfileRepository
    ) {
        this.courseRepository = courseRepository;
        this.postRepository = postRepository;
        this.replyRepository = replyRepository;
        this.userRepository = userRepository;
        this.teacherProfileRepository = teacherProfileRepository;
    }

    @Transactional(readOnly = true)
    // 列出帖子
    public List<PostResponse> listPosts(Long courseId) {
        return postRepository.findByCourseIdAndDeletedFalseOrderByPinnedDescUpdatedAtDesc(courseId).stream()
                .map(this::toPostResponse)
                .toList();
    }

    @Transactional
    // 创建帖子
    public PostResponse createPost(Long authorId, Long courseId, PostCreateRequest req) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        DiscussionPost p = postRepository.save(DiscussionPost.builder()
                .course(course)
                .author(author)
                .title(req.getTitle())
                .content(req.getContent())
                .pinned(false)
                .deleted(false)
                .build());

        return toPostResponse(p);
    }

    @Transactional(readOnly = true)
    // 列出回复
    public List<ReplyResponse> listReplies(Long courseId, Long postId) {
        DiscussionPost p = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Post not found"));
        if (!p.getCourse().getId().equals(courseId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Post not found");
        }
        return replyRepository.findByPostIdAndDeletedFalseOrderByUpdatedAtAsc(postId).stream()
                .map(this::toReplyResponse)
                .toList();
    }

    @Transactional
    // 创建回复
    public ReplyResponse createReply(Long authorId, Long courseId, Long postId, ReplyCreateRequest req) {
        DiscussionPost p = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Post not found"));
        if (p.isDeleted() || !p.getCourse().getId().equals(courseId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Post not found");
        }

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        DiscussionReply r = replyRepository.save(DiscussionReply.builder()
                .post(p)
                .author(author)
                .content(req.getContent())
                .deleted(false)
                .build());
        return toReplyResponse(r);
    }

    @Transactional
    // 置顶帖子
    public PostResponse teacherPinPost(Long teacherId, Long postId, boolean pinned) {
        requireVerifiedTeacher(teacherId);
        DiscussionPost p = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Post not found"));
        if (!p.getCourse().getTeacher().getId().equals(teacherId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your course");
        }
        p.setPinned(pinned);
        p = postRepository.save(p);
        return toPostResponse(p);
    }

    @Transactional
    // 删除帖子
    public void teacherDeletePost(Long teacherId, Long postId) {
        requireVerifiedTeacher(teacherId);
        DiscussionPost p = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Post not found"));
        if (!p.getCourse().getTeacher().getId().equals(teacherId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your course");
        }
        p.setDeleted(true);
        p.setPinned(false);
        postRepository.save(p);
    }

    // 获取已验证教师
    private void requireVerifiedTeacher(Long teacherId) {
        TeacherProfile p = teacherProfileRepository.findByUserId(teacherId)
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "Teacher profile not found"));
        if (p.getVerificationStatus() != TeacherVerificationStatus.APPROVED) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Teacher is not verified yet");
        }
    }

    // 转换为帖子响应
    private PostResponse toPostResponse(DiscussionPost p) {
        // [新增] 统计回复数
        long count = replyRepository.countByPostIdAndDeletedFalse(p.getId());

        return PostResponse.builder()
                .id(p.getId())
                .courseId(p.getCourse().getId())
                .authorId(p.getAuthor().getId())
                .authorName(p.getAuthor().getName())
                .title(p.getTitle())
                .content(p.getContent())
                .pinned(p.isPinned())
                .createdAt(p.getCreatedAt())
                // [新增] 设置回复数
                .replyCount(count)
                .build();
    }

    // 转换为回复响应
    private ReplyResponse toReplyResponse(DiscussionReply r) {
        return ReplyResponse.builder()
                .id(r.getId())
                .postId(r.getPost().getId())
                .authorId(r.getAuthor().getId())
                .authorName(r.getAuthor().getName())
                .content(r.getContent())
                .createdAt(r.getCreatedAt())
                .build();
    }
}