package com.task_management_system.ENTITY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.task_management_system.DTO.CommentDTO;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Entity
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Task task;

    public CommentDTO getCommentDTO()
    {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(this.id);
        commentDTO.setContent(this.content);
        commentDTO.setCreatedAt(this.createdAt);
        commentDTO.setTaskId(this.task.getId());
        commentDTO.setPostedBy(user.getName());
        return commentDTO;
    }


}
