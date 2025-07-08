<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Спасибо!</title>
    <link rel="stylesheet" href="/static/css/candidates.css">
</head>
<body>
    <div class="container flex flex-col align-center">
        <h1>Спасибо за ваш голос!</h1>
        <main class="flex flex-wrap align-center">
            <div class="card">
              <div class="flex flex-col align-center">
                <img src="/static/images/${candidate.image}" alt="${candidate.name}">
                <p>Вы проголосовали за: ${candidate.name}</p>
              </div>
            </div>
            <a class="back flex align-center" href="/">Вернуться на главную</a>
        </main>
    </div>
</body>
</html>