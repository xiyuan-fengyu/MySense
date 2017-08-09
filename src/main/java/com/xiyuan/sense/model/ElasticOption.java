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
            tempResult = HttpRequest.get(ElasticSearchCfg.root + path).body();
        }
        else if ("PUT".equals(method)) {
            tempResult = HttpRequest.put(ElasticSearchCfg.root + path).send(body.getBytes(StandardCharsets.UTF_8)).body();
        }
        else if ("DELETE".equals(method)) {
            tempResult = HttpRequest.delete(ElasticSearchCfg.root + path).body();
        }
        this.result = tempResult;
    }

    public static List<ElasticOption> parse(String str) {
        List<ElasticOption> options = new ArrayList<>();
        if (str != null) {
            String[] lines = str.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (line.startsWith("#")) {
                }
                else if (line.startsWith("GET ")) {
                    options.add(new ElasticOption("GET", line.substring(4), null));
                }
                else if (line.startsWith("DELETE ")) {
                    options.add(new ElasticOption("DELETE", line.substring(7), null));
                }
                else if (line.startsWith("PUT ")) {
                    String path = line.substring(4);
                    StringBuilder builder = new StringBuilder();
                    while (i < lines.length) {
                        i++;
                        line = lines[i];
                        if (line.equals("}")) {
                            builder.append("}");
                            options.add(new ElasticOption("PUT", path, builder.toString()));
                            break;
                        }
                        else if (!line.startsWith("#")) {
                            builder.append(line).append('\n');
                        }
                    }
                }
            }
        }
        return options;
    }

}
