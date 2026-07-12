package com.orderflow.task;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@WebServlet("/tasks")
public class TaskServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(TaskServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String taskId = UUID.randomUUID().toString();

        // Log4j 2.x's context API (ThreadContext, the MDC equivalent)
        ThreadContext.put("taskId", taskId);

        logger.info("Processing new fulfillment task");

        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();
        out.println("Task created successfully!");
        out.println("Task ID: " + taskId);

        ThreadContext.remove("taskId");
    }
}
