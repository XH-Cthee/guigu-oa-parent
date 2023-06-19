package com.xzh.auth.activity;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:Xzh_
 * @Date:2023/3/24
 */
@SpringBootTest
public class ProcessTestAllocation {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    // uel-value
    //部署流程定义
    @Test
    public void deployProcess() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process/jiaban.bpmn20.xml")
                .name("加班申请流程")
                .deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    //启动流程实例
    @Test
    public void startProcessInstance() {
        Map<String,Object> map = new HashMap<>();
        //设置任务人
        map.put("assignee1","lucy");
        map.put("assignee2","mary");

        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("jiaban", map);

        System.out.println(processInstance.getProcessDefinitionId());
        System.out.println(processInstance.getId());
    }
    //3 查询待办任务
    @Test
    public void findTaskList(){
        //String assign = "zhao6";
        String assign = "lucy";
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(assign).list();
        for(Task task:list){
            System.out.println("流程实例ID："+task.getParentTaskId());
            System.out.println("任务ID："+task.getId());
            System.out.println("任务负责人："+task.getAssignee());
            System.out.println("任务名称:"+task.getName());
        }
    }
    //4 执行待办任务
    @Test
    public void completeTask(){
        Task task = taskService.createTaskQuery()
                .taskAssignee("lucy")
                .singleResult();//返回一条
        //完成任务参数：任务ID
        taskService.complete(task.getId());
    }


    //5 结束流程
}
