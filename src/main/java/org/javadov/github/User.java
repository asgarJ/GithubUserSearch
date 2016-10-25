package org.javadov.github;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by asgar on 10/23/16.
 */

@WebServlet("/user")
public class User extends HttpServlet {
    private static final long serialVersionUID = 5L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        String userName = request.getParameter("name");
        if (userName == null || userName.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("errorMessage", "Please don't leave the username field empty!");
            response.getWriter().write(jsonObject.toJSONString());
            return;
        }

        RepositoryService service = new RepositoryService();
        List<Repository> repositories = null;
        try {
            repositories = service.getRepositories(userName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (repositories != null) {
            repositories.sort((Repository r1, Repository r2) -> r2.getSize() - r1.getSize());
            List<Repository> repos = repositories.subList(0, Math.min(5, repositories.size()));

            JSONArray list = new JSONArray();
            for (Repository repo : repos) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", repo.getName());
                jsonObject.put("url", repo.getUrl());
                list.add(jsonObject);
            }

            try (PrintWriter printWriter = response.getWriter()) {
                printWriter.write(list.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("errorMessage", "Please enter a valid username!");
            response.getWriter().write(jsonObject.toJSONString());
        }
    }
}