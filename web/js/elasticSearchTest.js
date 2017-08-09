/**
 * Created by xiyuan_fengyu on 2017/8/9.
 */
$(document).ready(() => {

    CodeMirror.commands.submit = submit;

    let runKey = (CodeMirror.keyMap.default == CodeMirror.keyMap.macDefault ? "Cmd" : "Ctrl") + "-Enter";
    let extraKeys = {};
    extraKeys[runKey] = "submit";

    let elasticInput = CodeMirror.fromTextArea($("#elasticInput")[0], {
        lineNumbers: true,
        mode: "text/javascript",
        extraKeys: extraKeys
    });

    $(".jsonData").each((i, obj) => {
        let $obj = $(obj);
        $obj.html(new JSONFormat($obj.find("textarea").val(), 4).toString());
    });

    $("#submit").click(e => {
        submit();
    });

    function submit() {
        $.post("/app/sense/test", {
            data: elasticInput.getValue()
        }, (res, status) => {
            if (res.success) {
                let htmlStr = res.data.map(item => {
                    let esBody = "";
                    if (item.body) {
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
                    $obj.html(new JSONFormat($obj.find("textarea").val(), 4).toString());
                });
            }
            else {
                $("#elasticResult").html(res.message);
            }
        });
    }

});

