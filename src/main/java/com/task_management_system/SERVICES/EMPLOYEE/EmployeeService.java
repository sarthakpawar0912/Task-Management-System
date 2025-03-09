package com.task_management_system.SERVICES.EMPLOYEE;

import com.task_management_system.DTO.CommentDTO;
import com.task_management_system.DTO.TaskDTO;
import java.util.List;

public interface EmployeeService {

    List<TaskDTO> getTasksByUserId();

    TaskDTO  updateTask(Long id, String status);

    TaskDTO getTaskById(Long id);

    CommentDTO createComment(Long taskId, String content);

    List<CommentDTO> getCommentsByTaskId(Long taskId);

}
