package com.planify.planify.service;

import com.planify.planify.dto.CommentDTO;
import com.planify.planify.dto.CommentMapper;
import com.planify.planify.dto.CreateCommentDTO;
import com.planify.planify.entity.Comment;
import com.planify.planify.entity.Plan;
import com.planify.planify.entity.User;
import com.planify.planify.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PlanService planService;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository, UserService userService, PlanService planService, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.planService = planService;
        this.commentMapper = commentMapper;
    }

    @Override
    public CommentDTO createComment(CreateCommentDTO createCommentDTO, Long id) {
        User user = userService.getUserById(id);
        Plan plan = planService.getPlanById(createCommentDTO.planId());

        Comment comment = new Comment(plan, user, createCommentDTO.content());

        commentRepository.save(comment);

        return commentMapper.toCommentDTO(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalStateException("Comment not found with id: " + commentId));
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDTO> getCommentsByPlanId(Long planId) {
        List<CommentDTO> comments = commentRepository.findCommentsWithCommentersByPlanId(planId);

        // Ensure a non-null list is always returned
        return comments != null ? comments : Collections.emptyList();
    }


}
