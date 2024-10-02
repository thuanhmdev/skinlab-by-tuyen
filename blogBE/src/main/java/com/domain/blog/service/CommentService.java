package com.domain.blog.service;

import com.domain.blog.domain.Blog;
import com.domain.blog.domain.Comment;
import com.domain.blog.domain.User;
import com.domain.blog.domain.dto.CommentCreateDTO;
import com.domain.blog.domain.dto.CommentDTO;
import com.domain.blog.domain.dto.MailCommentDTO;
import com.domain.blog.repository.CommentRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Optional;

import static com.domain.blog.constant.RoleEnum.ADMIN;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final BlogService blogService; ;
    private final UserService userService;
    private final EmailService emailService;
    private final SettingService settingService;


    public CommentService(CommentRepository commentRepository, BlogService blogService,  UserService userService, EmailService emailService,  SettingService settingService) {
        this.commentRepository = commentRepository;
        this.blogService = blogService;
        this.userService = userService;
        this.emailService = emailService;
        this.settingService = settingService;
    }

    public boolean checkExistBlogAndUser(CommentCreateDTO commentCreateDTO) {
        boolean isExistUser = this.userService.existsById(commentCreateDTO.getUser().getId());
        boolean isExistBlog = this.blogService.existsById(commentCreateDTO.getBlog().getId());
        return isExistUser && isExistBlog;
    }

    @Async
    public void sendEmailNotification(CommentCreateDTO commentCreateDTO, User commenter) {

        String siteName = this.settingService.getSiteNameSetting();
        String emailAdmin = this.settingService.getEmailSetting();
        Blog blog = this.blogService.getBlogById(commentCreateDTO.getBlog().getId());

        if(commentCreateDTO.getRootId() == null || commentCreateDTO.getRootId().isEmpty()){
            User replyToUser = this.userService.getUserById(commentCreateDTO.getUser().getId());
            if(!replyToUser.getRole().equals(ADMIN)){
                MailCommentDTO mailCommentDTO = new MailCommentDTO("Admin", commenter.getName(), commentCreateDTO.getContent(), blog, commentCreateDTO.getBlogUrl(), siteName);
                this.emailService.sendEmailFromTemplateSync(emailAdmin, "Bạn có một bình luận mới", "comment", mailCommentDTO);
            }
        } else{

            User userWriteComment = this.userService.getUserById(commentCreateDTO.getUser().getId());
            User replyToUser =  this.userService.getUserById(commentCreateDTO.getReplyToUser().getId());
            if(replyToUser.getUsername().equals(userWriteComment.getUsername())){
                return;
            }
            if(!userWriteComment.getRole().equals(ADMIN) && !replyToUser.getRole().equals(ADMIN)){
                String email = replyToUser.getUsername();

                MailCommentDTO mailReplyToUserDTO = new MailCommentDTO(replyToUser.getName(), commenter.getName(), commentCreateDTO.getContent(), blog, commentCreateDTO.getBlogUrl(), siteName);
                MailCommentDTO mailReplyToAdminDTO = new MailCommentDTO("Admin", commenter.getName(), commentCreateDTO.getContent(), blog, commentCreateDTO.getBlogUrl(), siteName);


                this.emailService.sendEmailFromTemplateSync(email, "Bạn có một phản hồi mới", "comment", mailReplyToUserDTO);
                this.emailService.sendEmailFromTemplateSync(emailAdmin, "Bạn có một bình luận mới", "comment", mailReplyToAdminDTO);
            }
        }

    }

    public Comment addComment(CommentCreateDTO commentCreateDTO) {
        Comment comment = new Comment();
        User commenter = this.userService.getUserById(commentCreateDTO.getUser().getId());
        comment.setId(commentCreateDTO.getId());
        comment.setRootId(commentCreateDTO.getRootId());
        comment.setUser(commenter);
        comment.setBlog(commentCreateDTO.getBlog());
        comment.setContent(commentCreateDTO.getContent());

        this.sendEmailNotification(commentCreateDTO, commenter);
        return this.commentRepository.save(comment);
    }

    public List<CommentDTO> getComments() {
        return this.commentRepository.findByOrderByCreatedAtDesc().stream().map(this::converseCommentDTO).toList();
    }

    public List<CommentDTO> getCommentsByBlog(String blogId) {
        return this.commentRepository.findByBlogIdOrderByCreatedAtDesc(blogId).stream().map(this::converseCommentDTO).toList();
    }

    public CommentDTO converseCommentDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setUser(this.userService.convertUserToUserDTO(this.userService.getUserById(comment.getUser().getId())));
        commentDTO.setCreatedAt(comment.getCreatedAt());
        commentDTO.setBlogId(comment.getBlog().getId());
        commentDTO.setRootId(comment.getRootId());
        return commentDTO;
    }

    public Comment getCommentById (String id){
        Optional<Comment> comment = this.commentRepository.findById(id);
        return comment.orElse(null);
    }

    public void deleteComment(String id) {
        List<Comment> comments = this.commentRepository.findCommentsByRootId(id);
        this.commentRepository.deleteById(id);
        this.commentRepository.deleteAll(comments);
    }
}