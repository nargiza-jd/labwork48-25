<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Голосование</title>
  <link rel="stylesheet" href="/static/css/candidates.css">
</head>
<body>
  <div class="container flex flex-col align-center">
    <h1>Голосуйте за нас!</h1>
    <main class="flex flex-wrap align-evenly">
      <#list candidates as candidate>
        <div class="card">
          <form action="/vote" method="post" class="flex flex-col align-center">
            <img src="/static/images/${candidate.image}" alt="${candidate.name}">
            <p>${candidate.name}</p>
            <input name="candidateId" type="hidden" value="${candidate.id}">
            <button type="submit" class="vote flex align-center">Проголосовать</button>
          </form>
        </div>
      </#list>
    </main>
    <p>-------------------------------------------------------</p>
    <a class="back flex align-center" href="/votes">Показать результаты голосования</a>
  </div>
</body>
</html>