package com.tablebooknow.controller.review;

import com.tablebooknow.dao.ReviewDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;


@WebServlet("/reviews/*")
public class ReviewServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(ReviewServlet.class.getName());

    private ReviewDAO reviewDAO;

    @Override
    public void init() throws ServletException {
        reviewDAO = new ReviewDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String userId = (String) session.getAttribute("userId");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            pathInfo = "/";
        }

        switch (pathInfo) {
            case "/":
            default:
                response.sendRedirect(request.getContextPath() + "/reviews/list");
                break;
        }
    }




}