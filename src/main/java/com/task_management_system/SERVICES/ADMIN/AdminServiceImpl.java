package com.task_management_system.SERVICES.ADMIN;


import com.task_management_system.DTO.CommentDTO;
import com.task_management_system.DTO.TaskDTO;
import com.task_management_system.DTO.UserDto;
import com.task_management_system.ENTITY.Comment;
import com.task_management_system.ENTITY.Task;
import com.task_management_system.ENTITY.User;
import com.task_management_system.ENUMS.TaskStatus;
import com.task_management_system.ENUMS.UserRole;
import com.task_management_system.REPOSITORY.CommentRepository;
import com.task_management_system.REPOSITORY.TaskRepository;
import com.task_management_system.REPOSITORY.UserRepository;
import com.task_management_system.UTILS.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    private final TaskRepository taskRepository;

    private final JwtUtil jwtUtil;

    private final CommentRepository commentRepository;

    public AdminServiceImpl(UserRepository userRepository, TaskRepository taskRepository, JwtUtil jwtUtil, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.jwtUtil = jwtUtil;
        this.commentRepository = commentRepository;
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().filter(user->user.getUserRole()== UserRole.EMPLOYEE)
                .map(User::getuserDto).collect(Collectors.toList());
    }

    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {
        Optional<User> optionalUser=userRepository.findById(taskDTO.getEmployeeId());
        if (optionalUser.isPresent()) {
            Task task = new Task();
            task.setTitle(taskDTO.getTitle());
            task.setDescription(taskDTO.getDescription());
            task.setPriority(taskDTO.getPriority());
            task.setDueDate(taskDTO.getDueDate());
            task.setTaskStatus(TaskStatus.INPROGRESS);
            task.setUser(optionalUser.get());
            return taskRepository.save(task).getTaskDTO();
        }
        return null;
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .sorted(Comparator.comparing(Task::getDueDate).reversed())
                .map(Task::getTaskDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        return optionalTask.map(Task::getTaskDTO).orElse(null);

    }@Override
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        Optional<User> optionalUser = userRepository.findById(taskDTO.getEmployeeId());

        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setTitle(taskDTO.getTitle());
            task.setDescription(taskDTO.getDescription());
            task.setDueDate(taskDTO.getDueDate());
            task.setPriority(taskDTO.getPriority());
            task.setTaskStatus(mapStringToTaskStatus(taskDTO.getTaskStatus().name()));
            if (optionalUser.isPresent()) {
                task.setUser(optionalUser.get());
            } else {
                throw new EntityNotFoundException("User not found with id: " + taskDTO.getEmployeeId());
            }

            task = taskRepository.save(task);
            return task.getTaskDTO();
        }
        throw new EntityNotFoundException("Task not found with id: " + id);
    }

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
    public List<TaskDTO>  SearchTaskByTitle(String title) {
        return taskRepository.findAllByTitleContaining(title)
                .stream()
                .sorted(Comparator.comparing(Task::getDueDate).reversed())
                .map(Task::getTaskDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDTO createComment(Long taskId, String content) {
        Optional<Task> optionalTask=taskRepository.findById(taskId);
        User user=jwtUtil.getLoggedInUser();
        if((optionalTask.isPresent()) && user!=null){
            Comment comment=new Comment();
            comment.setCreatedAt(new Date());
            comment.setContent(content);
            comment.setTask(optionalTask.get());
            comment.setUser(user);
            return commentRepository.save(comment).getCommentDTO();
        }
        throw new EntityNotFoundException("User or Task not found");
    }

    @Override
    public List<CommentDTO> getCommentsByTaskId(Long taskId) {
        return commentRepository.findAllByTaskId(taskId)
                .stream().map(Comment::getCommentDTO)
                .collect(Collectors.toList());

    }
}
