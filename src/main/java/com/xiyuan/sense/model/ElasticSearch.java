package com.xiyuan.sense.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
        this.result = http(elastic + path, method, body);
    }

    public static String http(String urlStr, String method, String data) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.connect();

            if (data != null && data.length() > 0) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }

            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append('\n');
                }
            } catch (Exception e) {
//                e.printStackTrace();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line).append('\n');
                    }
                } catch (Exception ee) {
//                e.printStackTrace();
                }
            }
            return builder.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final Pattern paramPattern = Pattern.compile("(\\$\\{(\\d+)\\})");

    public static List<ElasticSearch> parse(String elastic, String str, String ...params) {
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
            if (line.length() > 0 && !line.matches("( |\\t)*(#|//).*")) {
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
