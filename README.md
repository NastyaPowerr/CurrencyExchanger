## Обменник валют

Добавляйте валюты, создавайте новые обменные курсы и получайте результаты их конвертации!

### Деплой (до 14.02.2026):
[62.113.111.226:8080/CurrencyExchanger/](http://62.113.111.226:8080/CurrencyExchanger/)

Репозиторий содержит **backend** (org.roadmap.currencyexchanger) и **фронтенд** (webapp) часть, **настройки** (application.properties) а также готовую **базу данных** с начальными валютами (currency_db.sqlite).

### Требования для запуска:
* Java 17
* Apache Tomcat 11.0.18
* База данных SQLite (уже включена в проект)

### Для локального запуска:
1. Клонировать проект.
2. В `resources/application.properties` изменить `database.url` на вашу ссылку базы данных (напр. `jdbc:sqlite:D:/idea_projects/CurrencyExchanger/currency_db.sqlite`).
3. Собрать проект с расширением `.war`.
4. Развернуть `.war` в Tomcat.
5. Открыть в браузере http://localhost:8080/CurrencyExchanger/.
6. Вы великолепны!

### Для запуска на сервер:
1) Клонировать проект.
2) В `resources/application.properties` изменить `database.url` на вашу ссылку базы данных (напр. `jdbc:sqlite:/CurrencyExchanger/currency_db.sqlite`)
3) В `src/main/webapp/js/app.js` изменить `const host` на вашу `http://server_ip:8080/НАЗВАНИЕ_СБОРКИ` (напр. `http://62.113.111.226:8080/CurrencyExchanger/`)
4) Собрать проект с расширением `.war`.
5) На сервере установить jdk 17 и apache-tomcat-11.0.18.
6) В папку `apache.tomcat-11.0.18/webapps` добавить файл с расширением `.war`.
7) Запустить `./bin/startup.sh`.
8) Проект будет развернут по ссылке, указанной в пункте 3.
