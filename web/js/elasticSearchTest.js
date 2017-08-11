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
        scrollbarStyle: "simple",
        keyMap: "sublime",
        extraKeys: extraKeys
    });
    let elasticUrl = $("#elasticUrl");
    let elasticResult = $("#elasticResult");

    $(".jsonData").each((i, obj) => {
        jsonDataDivFormat(obj);
    });

    $("#submit").click(e => {
        submit();
    });

    function submit() {
        elasticResult.html("");
        $.post("/app/sense/execute", {
            data: elasticInput.getValue(),
            elastic: elasticUrl.val()
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
                elasticResult.html(htmlStr);
                $(".jsonData").each((i, obj) => {
                    jsonDataDivFormat(obj);
                });
            }
            else {
                elasticResult.html(res.message);
            }
        });
    }

    function jsonDataDivFormat(obj) {
        let $obj = $(obj);
        let str = $obj.find("textarea").val();
        try {
            $obj.html(new JSONFormat(str, 4).toString());
        }
        catch (e) {
            $obj.html(str.split('\n').map(item => `<p>${item}</p>`).join('\n'));
        }
    }

});

