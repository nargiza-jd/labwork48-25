<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Результаты голосования</title>
    <link rel="stylesheet" href="/static/css/candidates.css">
</head>
<body>
<h1>Кто побеждает?</h1>

<#list candidateResults as c>
    <div class="card">
        <img src="${c.image}" alt="${c.name}" width="150">
        <p>Имя: ${c.name}</p>
        <p>Голосов: ${c.votes}</p>
        <p>Процент: ${c.percentage}%</p>
        <#if c.isWinner?? && c.isWinner>
            <strong>Победитель!</strong>
        </#if>
    </div>
</#list>

<a href="/">Вернуться к выбору</a>
</body>
</html>