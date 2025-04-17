<!DOCTYPE html>
<html>
<head>
    <title>Register</title>
</head>
<body>
<h2>Login</h2>
<#if error??>
    <p style="color:red">${error}</p>
</#if>
<form action="/register" method="post">
    <label for="email">Email:</label><br>
    <input type="text" id="email" name="email" required><br><br>

    <input type="submit" value="Login">
</form>
</body>
</html>