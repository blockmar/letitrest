<html>
<head>
<title>Example</title>
</head>
<body>
<#if name?has_content>
<h1>Hello ${name}!</h1>
</#if>
<p>
<form method="GET" action="/page">
Enter your name: <input type="text" name="name" value=""/>
<input type="Submit"/>
</form>
</p>
</body>
</html>