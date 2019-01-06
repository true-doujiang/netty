<html>

<head>
<title>  TEST</title>


<link rel="stylesheet" href="../resources/base.css" /> 
<#--
-->
</head>

<body>


</body>

<h1>
${test}
</h1>

<div>
    aa
		<a href="/test">get</a> <br/>
		<a href="/test/2">get/id</a><br/>
		<a href="/test?_method=POST">post</a><br/>
		<a href="/test?_method=PUT">put</a><br/>
		<a href="/test/2?_method=DELETE">delete</a><br/>
		<a href="/test/testMethod">自定义方法</a><br/>
		<a href="/test/testRedirect">自定义方法(重定向到自定义方法)</a><br/>
		<a href="/test/testAddCookie">添加cookie</a><br/>
		<a href="/test/testRemoveCookie">移除cookie</a><br/>
</div>

</html>