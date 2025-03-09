package com.task_management_system.SERVICES.ADMIN;

import com.task_management_system.DTO.CommentDTO;
import com.task_management_system.DTO.TaskDTO;
import com.task_management_system.DTO.UserDto;

import java.util.List;
public interface AdminService {

    List<UserDto> getUsers();

    TaskDTO createTask(TaskDTO taskDTO);

    List<TaskDTO>getAllTasks();
    void deleteTask(Long id);
    TaskDTO updateTask(Long id,TaskDTO taskDTO);
    List<TaskDTO>  SearchTaskByTitle(String title);
    TaskDTO getTaskById(Long id);
    CommentDTO createComment(Long taskId, String content);
    List<CommentDTO> getCommentsByTaskId(Long taskId);
}
