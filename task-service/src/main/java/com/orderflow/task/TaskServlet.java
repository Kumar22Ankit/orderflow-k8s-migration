package com.orderflow.task;

import com.orderflow.task.model.Task;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.UserTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@WebServlet("/tasks")
public class TaskServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(TaskServlet.class);

    @PersistenceContext(unitName = "taskflowPU")
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String taskRef = UUID.randomUUID().toString();
        ThreadContext.put("taskId", taskRef);
        logger.info("Processing new fulfillment task");

        try {
            utx.begin();

            Task task = new Task(taskRef, "PENDING");
            em.persist(task);

            utx.commit();

            resp.setContentType("text/plain");
            PrintWriter out = resp.getWriter();
            out.println("Task created and persisted successfully!");
            out.println("Task Ref: " + taskRef);
            out.println("Database ID: " + task.getId());

        } catch (Exception e) {
            logger.error("Failed to persist task - likely a database connectivity issue", e);
            try {
                utx.rollback();
            } catch (Exception rollbackEx) {
                logger.error("Rollback also failed", rollbackEx);
            }

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("text/plain");
            PrintWriter out = resp.getWriter();
            out.println("Task creation failed: " + e.getMessage());
        } finally {
            ThreadContext.remove("taskId");
        }
    }
}
