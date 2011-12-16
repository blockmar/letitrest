<html>
<head>
<title>Example</title>
</head>
<body>
<#if name?has_content>
<h1>Hello ${name}!</h1>
<#if age?has_content>
<p>${age} is a good age.</p>
</#if>
<#else>
<h1>We are strangers!</h1>
</#if>
<p>
<form method="GET" action="#">
Tell me your name <input type="text" name="name" value=""/> and age <input type="age" name="age" value=""/><br/>
<input type="Submit"/>
</form>
</p>
<p><a href="/">Back</a></p>
</body>
</html>