package org.clarksnut.services.servlets;

import org.jboss.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/auth/authorize_offline")
public class OAuth2LinkOfflineToken extends HttpServlet {

    private static final Logger logger = Logger.getLogger(OAuth2LinkOfflineToken.class);

    public static final String CALLBACK = "/api/auth/authorize_offline_callback?scope=offline_access";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String redirect = req.getParameter("redirect");
        if (redirect == null) {
            redirect = req.getRemoteHost();
        }
        resp.sendRedirect(CALLBACK + "&redirect=" + redirect);
    }

}
