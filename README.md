# Schedule

This is a simple program that shows the schedule and the rest of the time of the lesson or recess. 
Made for students of "College of Electronics and Instrumentation"

APK: https://github.com/deVDem/Schedule/tree/master/app/release
The website of the College: http://pl130.ru

Это простая программа, которая показывает расписание и остаток времени урока или перемены.
Сделано для студентов "Колледжа Электроники и Приборостроения"

APK: https://github.com/deVDem/Schedule/tree/master/app/release
Сайт колледжа: http://pl130.ru


## API
  API is available here: https://api.devdem.ru/schedule/nameofscript.php
  Only 3 of the script.
  
  API доступно по ссылке: https://api.devdem.ru/schedule/nameofscript.php
  Всего 3 скрипта.
### Groups
  Returns a list of groups as a JSONArray.
  
  Возвращает список групп в виде JSONArray.
### Info
  Logs information about the user.
  Accepts 4 POST-variables: <b>name, email, group, spam</b>.
  Returns success = true if the user is registered and the user id is from the database.

  Регистрирует информацию о пользователе.
  Принимает 4 POST-переменных: <b>name, email,group,spam</b>.
  Возвращает success = true если пользователь зарегистрирован и id пользователя из БД.
### Timings
  Issuance of a list of items for the day.
  Accepts 3 POST variables: <b>group, day, id</b>.
  Returns a list as JSON from JSONObject.

  Выдача списка предметов на день.
  Принимает 3 POST-переменных: <b>group, day, id</b>.
  Возвращает список в виде JSON из JSONObject.
