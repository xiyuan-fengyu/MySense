package com.xiyuan.sense.controller;

import com.github.kevinsawicki.http.HttpRequest;
import com.xiyuan.sense.model.ElasticOption;
import com.xiyuan.sense.util.ResponseUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by xiyuan_fengyu on 2017/8/9.
 */
@Controller
public class SenseController {

    @RequestMapping(value = "app/sense")
    public String execute(String load_from, Model model) {
        if (load_from != null) {
            String search = HttpRequest.get(load_from).body();
            model.addAttribute("search", search);
            model.addAttribute("list", ElasticOption.parse(search));
        }
        else {
            model.addAttribute("search", "");
        }
        return "elasticSearchTest";
    }

    @RequestMapping(value = "app/sense/test")
    @ResponseBody
    public Map<String, Object> test(String data) {
        List<ElasticOption> list = ElasticOption.parse(data);
        if (list.size() > 0) {
            return ResponseUtil.success("执行成功", list);
        }
        else {
            return ResponseUtil.fail("执行失败");
        }
    }

}
