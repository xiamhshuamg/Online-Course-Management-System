package org.example.end.web;

import jakarta.validation.Valid;
import java.util.List;
import org.example.end.dto.discussion.PinRequest;
import org.example.end.dto.discussion.PostCreateRequest;
import org.example.end.dto.discussion.PostResponse;
import org.example.end.dto.discussion.ReplyCreateRequest;
import org.example.end.dto.discussion.ReplyResponse;
import org.example.end.security.SecurityUtils;
import org.example.end.service.DiscussionService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// 讨论控制器       
public class DiscussionController {

    private final DiscussionService discussionService;                           // 讨论服务

    public DiscussionController(DiscussionService discussionService) {
        this.discussionService = discussionService;
    }

    @GetMapping("/api/courses/{courseId}/posts")
    // 列出帖子
    public List<PostResponse> posts(@PathVariable Long courseId) {
        return discussionService.listPosts(courseId);
    }

    @PostMapping("/api/courses/{courseId}/posts")
    // 创建帖子
    public PostResponse createPost(@PathVariable Long courseId, @Valid @RequestBody PostCreateRequest req) {
        return discussionService.createPost(SecurityUtils.currentUserId(), courseId, req);
    }

    @GetMapping("/api/courses/{courseId}/posts/{postId}/replies")
    // 列出回复
    public List<ReplyResponse> replies(@PathVariable Long courseId, @PathVariable Long postId) {
        return discussionService.listReplies(courseId, postId);
    }

    @PostMapping("/api/courses/{courseId}/posts/{postId}/replies")
    // 创建回复
    public ReplyResponse createReply(@PathVariable Long courseId, @PathVariable Long postId, @Valid @RequestBody ReplyCreateRequest req) {
        return discussionService.createReply(SecurityUtils.currentUserId(), courseId, postId, req);
    }

    @PostMapping("/api/teacher/posts/{postId}/pin")
    // 置顶帖子
    public PostResponse pin(@PathVariable Long postId, @Valid @RequestBody PinRequest req) {
        return discussionService.teacherPinPost(SecurityUtils.currentUserId(), postId, req.getPinned());
    }

    @DeleteMapping("/api/teacher/posts/{postId}")
    // 删除帖子
    public void delete(@PathVariable Long postId) {
        discussionService.teacherDeletePost(SecurityUtils.currentUserId(), postId);
    }
}


