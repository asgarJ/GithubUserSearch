package org.javadov.github;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.json.simple.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        ObjectMapper mapper = new ObjectMapper();

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
            List<Repository> result = repositories.subList(0, Math.min(5, repositories.size()));

            List<Repo> list = new ArrayList<>();
            for (Repository repo : result) {
                list.add(new Repo(repo.getName(), repo.getUrl()));
            }

            String json = mapper.writeValueAsString(list);
            try (PrintWriter printWriter = response.getWriter()) {
                printWriter.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("errorMessage", "Please enter a valid username!");
            response.getWriter().write(jsonObject.toJSONString());
        }
    }

    private List<Repo> getRepos(int top) {
        return null;
    }
}

final class Repo {
    private String name;
    private String url;

    public Repo(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public String toString() {
        return "name: " + name + ", url: " + url;
    }
}