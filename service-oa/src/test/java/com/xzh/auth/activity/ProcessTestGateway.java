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

/**
 * @Author:Xzh_
 * @Date:2023/3/24
 */
@SpringBootTest
public class ProcessTestGateway {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    //1 部署流程定义
    @Test
    public void deployProcess(){
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("process/completeqingjia.bpmn20.xml")
                .name("完整请假过程")
                .deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }
    //2 启动流程实例
    @Test
    public void startProcessInstance(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("day",2);
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("completeqingjia",map);
        System.out.println(processInstance.getProcessDefinitionId());
        System.out.println(processInstance.getId());
    }
    //3 查询待办任务
    @Test
    public void findTaskList(){
        //String assign = "zhao6";
        String assign = "xiaocui";
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
                .taskAssignee("zhao6")
                .singleResult();//返回一条
        //完成任务参数：任务ID
        taskService.complete(task.getId());
    }


    //5 结束流程
}
