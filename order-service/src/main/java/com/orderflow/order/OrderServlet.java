package com.orderflow.order;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(OrderServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String orderId = UUID.randomUUID().toString();

        // This is the Log4j 1.x specific API call.
        // Log4j 1.x MDC.put(String, Object) signature.
        // If Log4j 2.x's classes load instead (classloader conflict),
        // this line throws NoSuchMethodError at runtime.
        MDC.put("orderId", orderId);

        logger.info("Processing new order request");

        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();
        out.println("Order created successfully!");
        out.println("Order ID: " + orderId);

        MDC.remove("orderId");
    }
}
