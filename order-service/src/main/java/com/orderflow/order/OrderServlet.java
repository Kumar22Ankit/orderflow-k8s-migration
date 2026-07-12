package com.orderflow.order;

import com.orderflow.order.model.Order;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(OrderServlet.class);

    @PersistenceContext(unitName = "orderflowPU")
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String orderRef = UUID.randomUUID().toString();
        MDC.put("orderId", orderRef);
        logger.info("Processing new order request");

        try {
            utx.begin();

            Order order = new Order(orderRef, "CREATED");
            em.persist(order);

            utx.commit();

            resp.setContentType("text/plain");
            PrintWriter out = resp.getWriter();
            out.println("Order created and persisted successfully!");
            out.println("Order Ref: " + orderRef);
            out.println("Database ID: " + order.getId());

        } catch (Exception e) {
            logger.error("Failed to persist order - likely a database connectivity issue", e);
            try {
                utx.rollback();
            } catch (Exception rollbackEx) {
                logger.error("Rollback also failed", rollbackEx);
            }

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("text/plain");
            PrintWriter out = resp.getWriter();
            out.println("Order creation failed: " + e.getMessage());
        } finally {
            MDC.remove("orderId");
        }
    }
}
