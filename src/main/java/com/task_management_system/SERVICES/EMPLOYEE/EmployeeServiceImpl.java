package com.task_management_system.SERVICES.EMPLOYEE;


import com.task_management_system.DTO.TaskDTO;
import com.task_management_system.ENTITY.Task;
import com.task_management_system.ENTITY.User;
import com.task_management_system.ENUMS.TaskStatus;
import com.task_management_system.REPOSITORY.TaskRepository;
import com.task_management_system.UTILS.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements  EmployeeService {

    private final TaskRepository taskRepository;
    private final JwtUtil jwtUtil;

    public EmployeeServiceImpl(TaskRepository taskRepository, JwtUtil jwtUtil) {
        this.taskRepository = taskRepository;
        this.jwtUtil = jwtUtil;
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
            return taskRepository.save(task).getTaskDTO();
        }
        throw new EntityNotFoundException("Task not found");
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

}
