package com.xiyuan.sense.controller;

import com.github.kevinsawicki.http.HttpRequest;
import com.xiyuan.sense.model.ElasticSearch;
import com.xiyuan.sense.util.ResponseUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xiyuan_fengyu on 2017/8/9.
 */
@Controller
public class SenseController {

    @RequestMapping(value = {"/", "app/sense"})
    public String sensePage(String load_from, Model model) {
        if (load_from != null) {
            String search = HttpRequest.get(load_from).body();
            model.addAttribute("search", search);
            model.addAttribute("list", ElasticSearch.parse(null, search));
        }
        else {
            model.addAttribute("search", "");
        }
        return "elasticSearchTest";
    }

    @RequestMapping(value = "app/sense/execute")
    @ResponseBody
    public Map<String, Object> execute(String data, String elastic) {
        List<ElasticSearch> list = ElasticSearch.parse(elastic, data);
        if (list.size() > 0) {
            return ResponseUtil.success("执行成功", list);
        }
        else {
            return ResponseUtil.fail("执行失败");
        }
    }

    @RequestMapping(value = "app/sense/locals")
    @ResponseBody
    public Map<String, Object> locals(HttpServletRequest request) {
        File elasticDir = new File(request.getSession().getServletContext().getRealPath("data/elastic"));
        if (elasticDir.exists() && elasticDir.isDirectory()) {
            List<String> names = new ArrayList<>();
            File[] files = elasticDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    names.add(file.getName());
                }
            }
            names.sort((o1, o2) -> {
                try {
                    return Integer.parseInt(o1.split("_")[0]) - Integer.parseInt(o2.split("_")[0]);
                } catch (Exception e) {
                    return 0;
                }
            });
            return ResponseUtil.success("elasticsearch查询语句文件列表查询成功", names);
        }
        else {
            return ResponseUtil.fail("文件夹data/elastic 不存在");
        }
    }

}
