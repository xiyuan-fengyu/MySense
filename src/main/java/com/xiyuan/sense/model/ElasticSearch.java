package com.xiyuan.sense.model;

import com.github.kevinsawicki.http.HttpRequest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiyuan_fengyu on 2017/8/9.
 */
public class ElasticSearch {

    public final String method;

    public final String path;

    public final String body;

    private String result;

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }

    public String getResult() {
        return result;
    }

    private ElasticSearch(String method, String path, String body) {
        this.method = method;
        this.path = path;
        this.body = body;
    }

    private void execute(String elastic) {
        String tempResult = null;
        if ("GET".equals(method)) {
            HttpRequest request = HttpRequest.get(elastic + path);
            if (body != null) {
                request.send(body.getBytes(StandardCharsets.UTF_8));
            }
            tempResult = request.body();
        }
        else if ("PUT".equals(method)) {
            tempResult = HttpRequest.put(elastic + path).send(body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8)).body();
        }
        else if ("POST".equals(method)) {
            HttpRequest request = HttpRequest.post(elastic + path);
            if (body != null) {
                request.send(body.getBytes(StandardCharsets.UTF_8));
            }
            tempResult = request.body();
        }
        else if ("DELETE".equals(method)) {
            tempResult = HttpRequest.delete(elastic + path).body();
        }
        this.result = tempResult;
    }

    private static final Pattern paramPattern = Pattern.compile("(\\$\\{(\\d+)\\})");

    public static List<ElasticSearch> parse(String str, String elastic, String ...params) {
        if (elastic == null || elastic.isEmpty()) {
            elastic = "http://localhost:9200";
        }

        if (params != null) {
            Matcher matcher = paramPattern.matcher(str);
            while (matcher.find()) {
                int paramIndex = Integer.parseInt(matcher.group(2));
                str = matcher.replaceFirst(params[paramIndex]);
                matcher.reset(str);
            }
        }

        List<ElasticSearch> actions = new ArrayList<>();
        if (str != null) {
            String[] lines = str.split("\n");
            Object[] out = new Object[2];
            for (int i = 0, len = lines.length; i < len; i++) {
                String line = lines[i].trim();
                if (line.startsWith("#") || line.startsWith("//")) {
                }
                else {
                    String method = line.split(" ")[0];
                    if (method.equals("GET")
                            || method.equals("PUT")
                            || method.equals("POST")) {
                        findJsonBody(lines, i, out);
                        i = (int) out[0];
                        int pathIndex = method.length() + 1;
                        while (pathIndex < line.length() && line.charAt(pathIndex) == ' ') {
                            pathIndex++;
                        }
                        actions.add(new ElasticSearch(method, line.substring(pathIndex), (String) out[1]));
                    }
                    else if (line.startsWith("DELETE ")) {
                        actions.add(new ElasticSearch("DELETE", line.substring(7), null));
                    }
                    else if (line.startsWith("WAIT ")) {
                        try {
                            long wait = Long.parseLong(line.substring(5));
                            Thread.sleep(wait);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            for (int i = 0, size = actions.size(); i < size; i++) {
                ElasticSearch action = actions.get(i);
                action.execute(elastic);
                if (!"GET".equals(action.method)) {
                    if (i + 1 < size && "GET".equals(actions.get(i + 1).method)) {
                        try {
                            Thread.sleep(1250);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return actions;
    }

    private static void findJsonBody(String[] lines, int i, Object[] out) {
        out[1] = null;

        int len = lines.length;
        String line;
        StringBuilder builder = new StringBuilder();
        while (i + 1 < len) {
            i++;
            line = lines[i];
            if (line.length() > 0 && !line.matches("( |\t)*(#|//)")) {
                if (line.matches("^(GET|POST|PUT|DELETE|WAIT) .*")) {
                    i--;
                    break;
                }
                else {
                    builder.append(line).append('\n');
                }
            }
        }
        if (builder.length() > 0) {
            out[1] = builder.toString();
        }

        out[0] = i;
    }

}
