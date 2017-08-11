<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
    String ctx = request.getContextPath();
%>

<!DOCTYPE html>
<html>
<head>
    <title>ElasticSearch Sense</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="ElasticSearch Sense">
    <meta http-equiv="description" content="ElasticSearch Sense">

    <!-- 新 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet" href="//cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <link rel="stylesheet" href="<%=ctx%>/css/font-awesome.min.css">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="//cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="//cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

    <link rel="stylesheet" href="<%=ctx%>/css/json.css">

    <link href="//cdn.bootcss.com/codemirror/5.28.0/codemirror.min.css" rel="stylesheet">
    <link href="//cdn.bootcss.com/codemirror/5.28.0/addon/scroll/simplescrollbars.min.css" rel="stylesheet">

    <style>
        .mt12 {
            margin-top: 12px;
        }

        .mt24 {
            margin-top: 24px;
        }

        .mb24 {
            margin-bottom: 24px;
        }

        .CodeMirror {
            min-height: 640px;
        }

    </style>
</head>

<body>
<div class="mt24" style="position: fixed; width: 50%;">
    <div class="col-md-offset-2 col-md-10">
        <div class="form-group mb24">
            <label>elasticsearch查询</label>
            <input type="url" class="form-control" id="elasticUrl" placeholder="elasticsearch服务器，默认 http://localhost:9200">
        </div>
        <textarea id="elasticInput" class="form-control mb24" rows="15">${search}</textarea>
        <button id="submit" type="button" class="btn btn-primary mt12">Submit</button>
    </div>
</div>

<div class="mt24 mb24" style="position: absolute; width: 50%; right: 0">
    <div class="col-md-10">
        <label>查询结果</label>
        <div id="elasticResult">
            <c:if test="${list != null}">
                <c:forEach items="${list}" var="item">
                    <div class="mb24">
                        <pre>${item.method} ${item.path}</pre>
                        <c:if test="${item.body != null}">
                            <label>Body</label>
                            <div class="jsonData mb24">
                                <textarea style="display: none;">${item.body}</textarea>
                            </div>
                        </c:if>
                        <label>Result</label>
                        <div class="jsonData mb24">
                            <textarea style="display: none;">${item.result}</textarea>
                        </div>
                    </div>
                </c:forEach>
            </c:if>
        </div>
    </div>

</div>
</body>

<script src="//cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>
<script src="//cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="//cdn.bootcss.com/codemirror/5.28.0/codemirror.min.js"></script>
<script src="//cdn.bootcss.com/codemirror/5.28.0/mode/javascript/javascript.min.js"></script>
<script src="//cdn.bootcss.com/codemirror/5.28.0/addon/comment/comment.min.js"></script>
<script src="//cdn.bootcss.com/codemirror/5.28.0/keymap/sublime.js"></script>
<script src="//cdn.bootcss.com/codemirror/5.28.0/addon/scroll/simplescrollbars.min.js"></script>
<script src="<%=ctx%>/js/jquery.json.js"></script>
<script src="<%=ctx%>/js/elasticSearchTest.js"></script>
</html>
