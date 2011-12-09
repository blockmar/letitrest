<html>
<head>
<title>Example</title>
</head>
<body>
<h1>This is page ${page}</h1>
<#if subpage?has_content>
<h2>Subpage is ${subpage}!</h1>
</#if>
<p><a href="/">Back</a></p>
</body>
</html>