package taskmanagerspringmvcportlet.controller;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portletmvc4spring.bind.annotation.ActionMapping;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import taskmanagerservicebuilder.exception.NoSuchTaskException;
import taskmanagerservicebuilder.model.Task;
import taskmanagerservicebuilder.service.TaskLocalServiceUtil;
import taskmanagerspringmvcportlet.dto.TaskDTO;

import javax.portlet.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author pablo
 */
@Controller
@RequestMapping("VIEW")
public class TaskController {

    private static final Logger _logger = LoggerFactory.getLogger(
            TaskController.class);
    @Autowired
    private LocalValidatorFactoryBean _localValidatorFactoryBean;
    @Autowired
    private MessageSource _messageSource;

    @ModelAttribute("taskDTO")
    public TaskDTO getTaskDTOModel() {
        System.out.println("ZZZ ModelAttribute TaskDTO");
        return new TaskDTO();
    }

    @RenderMapping
    public String prepareView(RenderRequest renderRequest, RenderResponse renderResponse, ModelMap modelMap) {
        System.out.println("ZZZ Render Mapping Default");
        User user = (User) renderRequest.getAttribute(WebKeys.USER);
        if (user != null) {
            List<Task> tasks = TaskLocalServiceUtil.getUserTasks(user.getUserId());
            modelMap.put("taskList", tasks);
            modelMap.put("userLogged", true);
        } else {
            modelMap.put("userLogged", false);
        }
        return "taskList";
    }

    private List<TaskDTO> transformTaskList(List<Task> tasks) {
        List<TaskDTO> taskList = new ArrayList<>();
        TaskDTO taskDTO;
        for (Task task : tasks) {
            taskDTO = new TaskDTO();
            taskDTO.setTaskId(task.getTaskId());
            taskDTO.setDone(task.getDone());
            taskDTO.setTitle(task.getTitle());
            taskDTO.setText(task.getText());
            taskList.add(taskDTO);
        }
        return taskList;
    }

    @RenderMapping(params = "javax.portlet.action=success")
    public String showTaskListView(RenderRequest renderRequest, RenderResponse renderResponse, ModelMap modelMap) {
        System.out.println("ZZZ Render Mapping Show Task List");
        return prepareView(renderRequest, renderResponse, modelMap);
    }

    @RenderMapping(params = "javax.portlet.action=editError")
    public String showEditTaskError(RenderRequest renderRequest, RenderResponse renderResponse, ModelMap modelMap) {
        System.out.println("ZZZ Render Mapping Show Edit Task Error");
        return "editTask";
    }

    @RenderMapping(params = "view=createTask")
    public String showCreateTaskView() {
        System.out.println("ZZZ Render Mapping Create Task");
        return "createTask";
    }

    @RenderMapping(params = "javax.portlet.action=createError")
    public String showCreateTaskError(RenderRequest renderRequest, RenderResponse renderResponse, ModelMap modelMap) {
        System.out.println("ZZZ Render Mapping Show Create Task Error");
        return "createTask";
    }

    @RenderMapping(params = "view=editTask")
    public String showEditTaskView(RenderRequest renderRequest, RenderResponse renderResponse, ModelMap modelMap) {
        System.out.println("ZZZ Render Mapping Edit Task");
        TaskDTO taskDTO = null;

        try {
            Long taskId = Long.parseLong(renderRequest.getRenderParameters().getValue("taskId"));
            Task task = TaskLocalServiceUtil.getTask(taskId);
            taskDTO = transformTask(task);
        } catch (PortalException e) {
            e.printStackTrace();
        }
        modelMap.put("task", taskDTO);

        return "editTask";
    }

    private TaskDTO transformTask(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskId(task.getTaskId());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setText(task.getText());
        taskDTO.setDone(task.getDone());
        return taskDTO;
    }

    @RenderMapping(params = "view=deleteTask")
    public String showDeleteTaskView(RenderRequest renderRequest, RenderResponse renderResponse, ModelMap modelMap) {
        System.out.println("ZZZ Render Mapping Delete Task");
        TaskDTO taskDTO = null;

        try {
            Long taskId = Long.parseLong(renderRequest.getRenderParameters().getValue("taskId"));
            Task task = TaskLocalServiceUtil.getTask(taskId);
            taskDTO = transformTask(task);
        } catch (PortalException e) {
            e.printStackTrace();
        }
        modelMap.put("task", taskDTO);

        return "deleteTask";
    }

    @ActionMapping
    public void submitTask(
            @ModelAttribute("task") TaskDTO task, BindingResult bindingResult,
            ModelMap modelMap, Locale locale, ActionRequest actionRequest, ActionResponse actionResponse,
            SessionStatus sessionStatus) {
        System.out.println("ZZZ Submit Task Action");
        _localValidatorFactoryBean.validate(task, bindingResult);

        if (!bindingResult.hasErrors()) {
            User user = (User) actionRequest.getAttribute(WebKeys.USER);
            TaskLocalServiceUtil.createTask(task.getTitle(), task.getText(), user.getUserId());

            MutableRenderParameters mutableRenderParameters = actionResponse.getRenderParameters();
            mutableRenderParameters.setValue("javax.portlet.action", "success");

            SessionMessages.add(actionRequest, "createSuccess");
            sessionStatus.setComplete();
        } else {
            MutableRenderParameters mutableRenderParameters = actionResponse.getRenderParameters();
            mutableRenderParameters.setValue("javax.portlet.action", "createError");

            SessionErrors.add(actionRequest, "createError");
            bindingResult.addError(new ObjectError("task",
                    _messageSource.getMessage("please-correct-the-following-errors", null, locale)));
        }
    }

    @RenderMapping(params = "action=updateTaskDone")
    public String updateTaskDone(
            RenderRequest renderRequest, RenderResponse renderResponse, ModelMap modelMap) {
        System.out.println("ZZZ Update Task Done Render?");
        Long taskId = Long.parseLong(renderRequest.getRenderParameters().getValue("taskId"));
        Boolean done = Boolean.parseBoolean(renderRequest.getRenderParameters().getValue("done"));
        try {
            if (done) {
                TaskLocalServiceUtil.doTask(taskId);
            } else {
                TaskLocalServiceUtil.undoTask(taskId);
            }
        } catch (NoSuchTaskException e) {
            e.printStackTrace();
        }

        SessionMessages.add(renderRequest, "updateDoneSuccess");
        return prepareView(renderRequest, renderResponse, modelMap);
    }

    @ActionMapping(params = "action=editTask")
    public void editTask(
            @ModelAttribute("task") TaskDTO task, BindingResult bindingResult,
            ModelMap modelMap, Locale locale, ActionRequest actionRequest, ActionResponse actionResponse,
            SessionStatus sessionStatus) {
        System.out.println("ZZZ Edit task Action");
        _localValidatorFactoryBean.validate(task, bindingResult);
        if (!bindingResult.hasErrors()) {
            try {
                TaskLocalServiceUtil.editTask(task.getTaskId(), task.getTitle(), task.getText(), task.isDone());
            } catch (NoSuchTaskException e) {
                e.printStackTrace();
            }
            MutableRenderParameters mutableRenderParameters = actionResponse.getRenderParameters();
            mutableRenderParameters.setValue("javax.portlet.action", "success");

            SessionMessages.add(actionRequest, "editSuccess");
            sessionStatus.setComplete();
        } else {
            MutableRenderParameters mutableRenderParameters = actionResponse.getRenderParameters();
            mutableRenderParameters.setValue("javax.portlet.action", "editError");


            SessionErrors.add(actionRequest, "editError");
            bindingResult.addError(new ObjectError("task",
                    _messageSource.getMessage("please-correct-the-following-errors", null, locale)));
        }

    }

    @ActionMapping(params = "action=deleteTask")
    public void deleteTask(
            ActionRequest actionRequest, ActionResponse actionResponse, SessionStatus sessionStatus) {
        System.out.println("ZZZ Delete task Action");
        Long taskId = Long.parseLong(actionRequest.getActionParameters().getValue("taskId"));
        try {
            TaskLocalServiceUtil.deleteTask(taskId);
        } catch (NoSuchTaskException e) {
            e.printStackTrace();
        } catch (PortalException e) {
            e.printStackTrace();
        }
        MutableRenderParameters mutableRenderParameters = actionResponse.getRenderParameters();
        mutableRenderParameters.setValue("javax.portlet.action", "success");

        SessionMessages.add(actionRequest, "deleteSuccess");
        sessionStatus.setComplete();
    }

}