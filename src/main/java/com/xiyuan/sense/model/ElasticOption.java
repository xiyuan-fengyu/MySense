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
            if (this.body != null) {
                request.send(body.getBytes(StandardCharsets.UTF_8));
            }
            tempResult = request.body();
        }
        else if ("PUT".equals(method)) {
            tempResult = HttpRequest.put(ElasticSearchCfg.root + path).send(body.getBytes(StandardCharsets.UTF_8)).body();
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
                String line = lines[i];
                if (line.startsWith("#")) {
                }
                else if (line.startsWith("GET ")) {
                    findJsonBody(lines, i, out);
                    i = (int) out[0];
                    options.add(new ElasticOption("GET", line.substring(4), (String) out[1]));
                }
                else if (line.startsWith("DELETE ")) {
                    options.add(new ElasticOption("DELETE", line.substring(7), null));
                }
                else if (line.startsWith("PUT ")) {
                    findJsonBody(lines, i, out);
                    i = (int) out[0];
                    options.add(new ElasticOption("PUT", line.substring(4), (String) out[1]));
                }
            }
        }
        return options;
    }

    private static void findJsonBody(String[] lines, int i, Object[] out) {
        out[1] = null;

        int len = lines.length;
        String line;
        if (i + 1 < len && lines[i + 1].equals("{")) {
            StringBuilder builder = new StringBuilder();
            while (i + 1 < len) {
                i++;
                line = lines[i];
                if (line.equals("}")) {
                    builder.append("}");
                    break;
                }
                else if (!line.startsWith("#")) {
                    builder.append(line).append('\n');
                }
            }
            if (builder.length() > 0) {
                out[1] = builder.toString();
            }
        }
        out[0] = i;
    }

}
