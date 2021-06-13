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
import java.util.List;
import java.util.Locale;

/**
 * @author pablo
 */
@Controller
@RequestMapping("VIEW")
public class TaskController {

    private static final Logger _logger = LoggerFactory.getLogger(TaskController.class);
    @Autowired
    private LocalValidatorFactoryBean _localValidatorFactoryBean;
    @Autowired
    private MessageSource _messageSource;

    @RenderMapping
    public String showTaskListView(RenderRequest renderRequest, RenderResponse renderResponse, ModelMap modelMap) {
        debugLog("showTaskListView");
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

    @RenderMapping(params = "javax.portlet.action=success")
    public String showTaskListViewAfterSuccess(RenderRequest renderRequest, RenderResponse renderResponse, ModelMap modelMap) {
        debugLog("showTaskListViewAfterSuccess");
        return showTaskListView(renderRequest, renderResponse, modelMap);
    }

    @RenderMapping(params = "view=createTask")
    public String showCreateTaskView(ModelMap modelMap) {
        debugLog("showCreateTaskView");
        modelMap.put("task", new TaskDTO());
        return "createEditTask";
    }

    @RenderMapping(params = "view=editTask")
    public String showEditTaskView(RenderRequest renderRequest, RenderResponse renderResponse, ModelMap modelMap) {
        debugLog("showEditTaskView");
        TaskDTO taskDTO = null;
        try {
            Long taskId = Long.parseLong(renderRequest.getRenderParameters().getValue("taskId"));
            Task task = TaskLocalServiceUtil.getTask(taskId);
            taskDTO = transformTask(task);
        } catch (PortalException e) {
            e.printStackTrace();
        }
        modelMap.put("task", taskDTO);

        return "createEditTask";
    }

    private TaskDTO transformTask(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskId(task.getTaskId());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setText(task.getText());
        taskDTO.setDone(task.getDone());
        return taskDTO;
    }

    @RenderMapping(params = "javax.portlet.action=createEditError")
    public String showCreateEditTaskErrorView(RenderRequest renderRequest, RenderResponse renderResponse, ModelMap modelMap) {
        debugLog("showCreateEditTaskErrorView");
        return "createEditTask";
    }

    @RenderMapping(params = "view=deleteTask")
    public String showDeleteTaskView(RenderRequest renderRequest, RenderResponse renderResponse, ModelMap modelMap) {
        debugLog("showDeleteTaskView");

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

    @ActionMapping(params = "action=createTask")
    public void createTaskAction(
            @ModelAttribute("task") TaskDTO task, BindingResult bindingResult,
            ModelMap modelMap, Locale locale, ActionRequest actionRequest, ActionResponse actionResponse,
            SessionStatus sessionStatus) {
        debugLog("createTaskAction");

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
            mutableRenderParameters.setValue("javax.portlet.action", "createEditError");

            SessionErrors.add(actionRequest, "createError");
            bindingResult.addError(new ObjectError("task",
                    _messageSource.getMessage("please-correct-the-following-errors", null, locale)));
        }
    }

    @ActionMapping(params = "action=editTask")
    public void editTaskAction(
            @ModelAttribute("task") TaskDTO task, BindingResult bindingResult,
            ModelMap modelMap, Locale locale, ActionRequest actionRequest, ActionResponse actionResponse,
            SessionStatus sessionStatus) {
        debugLog("editTaskAction");

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
            mutableRenderParameters.setValue("javax.portlet.action", "createEditError");


            SessionErrors.add(actionRequest, "editError");
            bindingResult.addError(new ObjectError("task",
                    _messageSource.getMessage("please-correct-the-following-errors", null, locale)));
        }

    }

    @RenderMapping(params = "action=updateTaskDone")
    public String updateTaskDoneAction(
            RenderRequest renderRequest, RenderResponse renderResponse, ModelMap modelMap) {
        debugLog("updateTaskDoneAction");

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
        return showTaskListView(renderRequest, renderResponse, modelMap);
    }

    @ActionMapping(params = "action=deleteTask")
    public void deleteTaskAction(
            ActionRequest actionRequest, ActionResponse actionResponse, SessionStatus sessionStatus) {
        debugLog("deleteTaskAction");

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

    private void debugLog(String message) {
        if (_logger.isDebugEnabled()) {
            _logger.debug(message);
        }
    }

}