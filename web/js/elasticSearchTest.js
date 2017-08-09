/**
 * Created by xiyuan_fengyu on 2017/8/9.
 */
$(document).ready(() => {

    $(".jsonData").each((i, obj) => {
        let $obj = $(obj);
        $obj.JSONView($obj.find("textarea").val());
    });

    $("#submit").click(e => {
        $.post("/app/sense/test", {
            data: $("#elasticInput").val()
        }, (res, status) => {
            if (res.success) {
                let htmlStr = res.data.map(item => {
                    let esBody = "";
                    if (item.method == "PUT") {
                        esBody = `
                            <label>Body</label>
                            <div class="jsonData mb24">
                                <textarea style="display: none;">${item.body}</textarea>
                            </div>
                            `;
                    }
                    return `
                        <div class="mb24">
                            <pre>${item.method} ${item.path}</pre>
                            ${esBody}
                            <label>Result</label>
                            <div class="jsonData mb24">
                                <textarea style="display: none;">${item.result}</textarea>
                            </div>
                        </div>
                        `;
                }).join("\n");
                $("#elasticResult").html(htmlStr);
                $(".jsonData").each((i, obj) => {
                    let $obj = $(obj);
                    $obj.JSONView($obj.find("textarea").text());
                });
            }
            else {
                $("#elasticResult").html(res.message);
            }
        });
    });

});