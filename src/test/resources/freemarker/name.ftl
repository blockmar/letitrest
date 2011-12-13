<html>
<head>
<title>Example</title>
</head>
<body>
<#if name?has_content>
<h1>Hello ${name}!</h1>
<#else>
<h1>We are strangers!</h1>
</#if>
<p>
<form method="GET" action="/name">
Tell me your name: <input type="text" name="name" value=""/>
<input type="Submit"/>
</form>
</p>
<p><a href="/">Back</a></p>
</body>
</html>