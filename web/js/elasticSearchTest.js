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

    loadLocals();

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
            let split = str.split('\n').filter(line => line.length > 0);

            //判断是否可以渲染为table
            let tableData = [];
            let len = split.length;
            if (len > 0){
                let lineLen = 0;
                let lastColDivider = 0;
                for (let j = 0; j < len; j++) {
                    lineLen = Math.max(lineLen, split[j].length);
                }

                for (let i = 0; i <= lineLen; i++) {
                    let isColDivider = true;
                    if (i < lineLen) {
                        for (let j = 0; j < len; j++) {
                            if (split[j].length > i && split[j].charAt(i) != ' ') {
                                isColDivider = false;
                                break;
                            }
                        }
                    }
                    if (isColDivider) {
                        for (let j = 0; j < len; j++) {
                            if (tableData.length <= j) {
                                tableData.push([]);
                            }
                            tableData[j].push(split[j].substring(lastColDivider, i).trim());
                        }
                        lastColDivider = i;
                    }
                }
            }

            if (tableData.length > 0 && tableData[0].length > 2) {
                let rowStr = tableData.map(rowData => "<tr>\n" + rowData.map(col => `<td>${col}</td>`).join('\n') + "<tr>").join('\n');
                let tableStr = `
                <table class="table table-hover table-bordered">
                    <tbody>
                    ${rowStr}
                    </tbody>
                </table>
                `;
                $obj.html(tableStr);
            }
            else {
                $obj.html(split.map(item => `<p>${item}</p>`).join('\n'));
            }
        }
    }

    function loadLocals() {
        let locals = $("#locals");
        $.get("/app/sense/locals", (res, status) => {
            if (res.success) {
                res.data.forEach(item => {
                    locals.append(`
                       <li data-value="${item}" class="list-group-item">${item}</li>
                    `);
                });

                let lis = locals.find("li");
                lis.each((i, li) => {
                    let $li = $(li);
                    $li.click(e => {
                        if (!$li.hasClass("active")) {
                            lis.removeClass("active");
                            $li.addClass("active");
                        }
                        loadLocal($li.attr("data-value"));
                    });
                });
            }
        });
    }

    function loadLocal(file) {
        $.get("/data/elastic/" + file, (res, status) => {
            elasticInput.setValue(res);
            submit();
        });
    }

});

