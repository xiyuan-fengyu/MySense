package com.xiyuan.sense.model;

import com.github.kevinsawicki.http.HttpRequest;
import com.xiyuan.sense.config.ElasticSearchCfg;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiyuan_fengyu on 2017/8/9.
 */
public class ElasticOption {

    public final String method;

    public final String path;

    public final String body;

    public final String result;

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

    private ElasticOption(String method, String path, String body) {
        this.method = method;
        this.path = path;
        this.body = body;

        String tempResult = null;
        if ("GET".equals(method)) {
            HttpRequest request = HttpRequest.get(ElasticSearchCfg.root + path);
            if (body != null) {
                request.send(body.getBytes(StandardCharsets.UTF_8));
            }
            tempResult = request.body();
        }
        else if ("PUT".equals(method)) {
            tempResult = HttpRequest.put(ElasticSearchCfg.root + path).send(body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8)).body();
        }
        else if ("POST".equals(method)) {
            HttpRequest request = HttpRequest.post(ElasticSearchCfg.root + path);
            if (body != null) {
                request.send(body.getBytes(StandardCharsets.UTF_8));
            }
            tempResult = request.body();
        }
        else if ("DELETE".equals(method)) {
            tempResult = HttpRequest.delete(ElasticSearchCfg.root + path).body();
        }
        this.result = tempResult;

        if (!"GET".equals(method)) {
            try {
                Thread.sleep(ElasticSearchCfg.wait_after_modify);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<ElasticOption> parse(String str) {
        List<ElasticOption> options = new ArrayList<>();
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
                        options.add(new ElasticOption(method, line.substring(pathIndex), (String) out[1]));
                    }
                    else if (line.startsWith("DELETE ")) {
                        options.add(new ElasticOption("DELETE", line.substring(7), null));
                    }
                }
            }
        }
        return options;
    }

    private static void findJsonBody(String[] lines, int i, Object[] out) {
        out[1] = null;

        int len = lines.length;
        String line;
        boolean isBodyFind = false;

        StringBuilder builder = new StringBuilder();
        while (i + 1 < len) {
            i++;
            line = lines[i].trim();
            if (line.length() > 0 && !line.startsWith("#") && !line.startsWith("//")) {
                if (line.matches("^(GET|POST|PUT|DELETE) .*")) {
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
