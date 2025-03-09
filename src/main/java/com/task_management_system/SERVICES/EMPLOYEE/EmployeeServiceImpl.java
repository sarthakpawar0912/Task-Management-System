package com.task_management_system.SERVICES.EMPLOYEE;


import com.task_management_system.DTO.CommentDTO;
import com.task_management_system.DTO.TaskDTO;
import com.task_management_system.ENTITY.Comment;
import com.task_management_system.ENTITY.Task;
import com.task_management_system.ENTITY.User;
import com.task_management_system.ENUMS.TaskStatus;
import com.task_management_system.REPOSITORY.CommentRepository;
import com.task_management_system.REPOSITORY.TaskRepository;
import com.task_management_system.UTILS.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements  EmployeeService {

    private final TaskRepository taskRepository;
    private final JwtUtil jwtUtil;
    private final CommentRepository commentRepository;

    public EmployeeServiceImpl(TaskRepository taskRepository, JwtUtil jwtUtil, CommentRepository commentRepository) {
        this.taskRepository = taskRepository;
        this.jwtUtil = jwtUtil;
        this.commentRepository = commentRepository;
    }

    @Override
    public List<TaskDTO> getTasksByUserId() {
        User user = jwtUtil.getLoggedInUser();
        if (user != null) {
            return taskRepository.findAllByUserId(user.getId())
                    .stream()
                    .sorted(Comparator.comparing(Task::getDueDate).reversed())
                    .map(Task::getTaskDTO)
                    .collect(Collectors.toList());
        }
        throw new EntityNotFoundException("User not found");
    }

    @Override
    public TaskDTO updateTask(Long id, String status) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setTaskStatus(mapStringToTaskStatus(status));
            // ✅ Save updated task in the database
            taskRepository.save(task);
            return task.getTaskDTO();
        }
        throw new EntityNotFoundException("Task not found");
    }

    // ✅ Convert string status to Enum
    private TaskStatus mapStringToTaskStatus(String status) {
        return switch (status) {
            case "PENDING" -> TaskStatus.PENDING;
            case "INPROGRESS" -> TaskStatus.INPROGRESS;
            case "COMPLETED" -> TaskStatus.COMPLETED;
            case "DEFERRED" -> TaskStatus.DEFERRED;
            default -> TaskStatus.CANCELLED;
        };
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        return optionalTask.map(Task::getTaskDTO).orElse(null);
    }

    @Override
    public CommentDTO createComment(Long taskId, String content) {
        System.out.println("Looking for Task ID: " + taskId);
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        User user = jwtUtil.getLoggedInUser();
        System.out.println("Logged-in User: " + (user != null ? user.getEmail() : "No user found"));
        if (optionalTask.isEmpty()) {
            throw new EntityNotFoundException("Task with ID " + taskId + " not found.");
        }
        if (user == null) {
            throw new EntityNotFoundException("User not found or not authenticated.");
        }
        Comment comment = new Comment();
        comment.setCreatedAt(new Date());
        comment.setContent(content);
        comment.setTask(optionalTask.get());
        comment.setUser(user);
        return commentRepository.save(comment).getCommentDTO();
    }


    @Override
    public List<CommentDTO> getCommentsByTaskId(Long taskId) {
        return commentRepository.findAllByTaskId(taskId)
                .stream()
                .map(Comment::getCommentDTO)
                .collect(Collectors.toList());
    }
}
