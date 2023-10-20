# Messenger

```
⠀⠀⠀  ⠀⠀⠀  ⠀⠀   ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣤⣴⣾⣿⣿⣿⡄     
⠀⠀ ⠀⠀⠀  ⠀   ⠀⠀⠀⠀⠀⠀⠀⢀⣠⣴⣶⣿⣿⡿⠿⠛⢙⣿⣿⠃
⠀ ⠀⠀     ⠀⠀⠀⠀⢀⣀⣤⣶⣾⣿⣿⠿⠛⠋⠁⠀⠀⠀⣸⣿⣿     8888ba.88ba   88888888b .d88888b  .d88888b   88888888b 888888ba   .88888.   88888888b  888888ba
      ⠀⣀⣤⣴⣾⣿⣿⡿⠟⠛⠉⠀⠀⣠⣤⠞⠁⠀ ⠀⣿⣿⡇    88  `8b  `8b  88        88.    "' 88.    "'  88        88    `8b d8'   `88  88         88    `8b
  ⣴⣾⣿⣿⡿⠿⠛⠉⠀⠀⠀⢀⣠⣶⣿⠟⠁⠀⠀⠀  ⢸⣿⣿⠀⠀   88   88   88 a88aaaa    `Y88888b. `Y88888b. a88aaaa    88     88 88        a88aaaa    a88aaaa8P'
⠸⣿⣿⣿⣧⣄⣀⠀⠀⣀⣴⣾⣿⣿⠟⠁⠀⠀⠀⠀⠀  ⣼⣿⡿⠀⠀   88   88   88  88              `8b       `8b  88        88     88 88   YP88  88         88   `8b.
⠀⠈⠙⠻⠿⣿⣿⣿⣿⣿⣿⣿⠟⠁⠀⠀⠀⠀⠀⠀  ⢠⣿⣿⠇⠀⠀  88   88   88  88        d8'   .8P d8'   .8P  88        88     88 Y8.   .88  88         88     88
⠀⠀⠀⠀⠀⠀⠘⣿⣿⣿⣿⡇⠀⣀⣄⡀⠀⠀⠀⠀  ⢸⣿⣿⠀⠀⠀  dP   dP   dP  88888888P  Y88888P   Y88888P   88888888P dP     dP  `88888'   88888888P  dP     dP
⠀⠀⠀⠀⠀⠀⠀⠸⣿⣿⣿⣠⣾⣿⣿⣿⣦⡀⠀⠀ ⣿⣿⡏⠀⠀⠀  oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
⠀⠀⠀⠀⠀⠀⠀⠀ ⢿⣿⣿⣿⡿⠋⠈⠻⣿⣿⣦⣸⣿⣿⠁⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀ ⠙⠛⠁ ⠀⠀⠀⠀⠈⠻⣿⣿⣿⠏⠀⠀⠀⠀
```

## Выполненные задания:

- ****Регистрация и авторизация****
    - Есть функционал регистрации в системе
        - *есть API для регистрации, сохраняющий пользователя в хранилище (**обязательно**);*
        - *есть хэширование паролей;*
        - *есть подтверждение почты через ссылку в письме, отправленном на указанную почту.*
    - Есть функционал входа в систему
        - *есть API, позволяющий залогиниться в системе и сохраняющее информацию о сессии любым способом (В JWT токене) (**обязательно**);*
        - *есть поддержка Spring Security;*
        - *информация о сессии хранится в JWT токенах и передается в HTTP хэдерах.*
    - Есть функционал выхода из системы
        - *есть API, позволяющий завершить текущую сессию и разлогиниться (**обязательно**);*
        - *есть механизмы защиты от обхода разлогина.*
- **Профиль пользователя**
    - Есть функционал для обновления данных пользователя
        - *есть API, позволяющий изменять базовую информацию профиля (**обязательно**);*
        - *при изменении email есть подтверждение изменения ссылкой на указанный новый email.*
    - Есть функционал для обновления пароля пользователя
        - *есть API, позволяющий обновить пароль (**обязательно**).*
    - Есть функционал для удаления аккаунта пользователя
        - *есть API, позволяющий удалить аккаунт пользователя (**обязательно**);*
        - *реализован перевод профиля в статус “Не активен” с дальнейшей возможностью восстановить профиль в течение некоторого времени.*
- **Социальная часть**
    - Есть функционал для отправления сообщений другому пользователю по его никнейму. Если другого пользователя не существует (неправильно указан никнейм), отправить сообщение невозможно
        - *есть API, позволяющий отправить другому пользователю сообщение, реализована проверка на существование пользователя (**обязательно**).*
    - Есть функционал для просмотра истории общения с другим пользователем по его никнейму
        - *есть API, позволяющий просматривать историю сообщений с конкретным пользователем (**обязательно**);*
        - *обмен и просмотр сообщений реализован с помощью веб-сокетов.*
- **ДОПОЛНИТЕЛЬНО: Есть функционал для добавления других пользователей в друзья, а также просмотра списка своих друзей.**
    - *есть API, позволяющий просматривать друзей, а также добавлять в друзья другого пользователя;*
    - *есть возможность ограничивать получение сообщений только своим кругом друзей;*
    - *есть возможность просматривать друзей другого пользователя, и, соответственно, возможность скрывать свой список друзей.*
- **Дополнительное**
    - использование базы данных (PostgreSQL) для хранения данных о пользователях и переписках между ними;
    - документирование запросов через Swagger;
    - написаны тесты;
- **От себя**
    - созданы файлы Dockerfile, docker-compose.yml, все переменные среды вынесены в .env файл, собран docker образ и залит на dockerhub (https://hub.docker.com/r/spyr000/messenger-spring-boot);
    - подключен Liquibase;
    - добавлена возможность выбирать формат возвращаемых данных при отправлении запросов при помощи передачи параметра format в url запроса
    - продвинутый перехват исключений;

## Регистрация
![Postman reg](https://github.com/spyr000/Messenger/assets/93257337/6a6592d0-ef0f-4091-aafa-739a6aefd991)

### Сообщение пришедшее на почту
![Email регистрация](https://github.com/spyr000/Messenger/assets/93257337/3fb6624c-16d5-4d39-abfb-de5ac5a234a9)

### Регистрация прошла успешно
![image](https://github.com/spyr000/Messenger/assets/93257337/65ef5e13-791c-47f5-b30c-0552b7a6a565)

## Хэширование паролей
![image](https://github.com/spyr000/Messenger/assets/93257337/be627afb-f1c9-4fd1-aa94-0d0dc3d97829)

## Авторизация
![Auth](https://github.com/spyr000/Messenger/assets/93257337/f309c6d6-5890-4811-9ed4-515b6525b569)

### Генерация access токена при отправлении запроса с валидным refresh токеном
![access token](https://github.com/spyr000/Messenger/assets/93257337/5e5857cc-252c-44bd-b6ac-91b03623e9f8)

### Генерация access и refresh токена при отправлении защищенного запроса с валидными access и refresh токенами
![image](https://github.com/spyr000/Messenger/assets/93257337/f8383ba1-1232-4bc8-85ab-8b176eed3cf2)

### Информация об id сессии, юзернейме пользователя и датах генерации и истечения хранится в jwt токене
![image](https://github.com/spyr000/Messenger/assets/93257337/9b018b9d-c141-4a6f-8d06-e266f886e040)

## Выход из системы
![logout](https://github.com/spyr000/Messenger/assets/93257337/49271f86-95dd-4a22-88f5-ef0a4b2e18a0)

### После выхода токен не валидный
![refresh token](https://github.com/spyr000/Messenger/assets/93257337/a92b7478-9e47-45c0-90c9-789de84ed8aa)

## Для обхода разлогина в токен записывается id сессии
![image](https://github.com/spyr000/Messenger/assets/93257337/4a3c84a9-1902-4503-a584-a03d10a29d9b)

## Смена дополнительной информации о нашем аккаунте
![image](https://github.com/spyr000/Messenger/assets/93257337/b1a8bb3d-fdaf-4ed3-b2a4-8a73b2c517ac)

## Смена юзернейма
![edit username ](https://github.com/spyr000/Messenger/assets/93257337/222cb1f7-d713-48f9-88cd-1721935cd1e2)

## Смена почты
![image](https://github.com/spyr000/Messenger/assets/93257337/9a369ef9-105f-4102-b4f0-72776f99026b)

### Сообщение пришедшее на почту
![click on link to verify email](https://github.com/spyr000/Messenger/assets/93257337/4d43b2d2-a626-4a9d-9502-25cd62ef3d91)

### Смена почты прошла успешно
![account email changed](https://github.com/spyr000/Messenger/assets/93257337/70b9d809-d417-4581-85a5-26f64daf5bd9)

### Если что-то пошло не так
![the link is invalid or broken](https://github.com/spyr000/Messenger/assets/93257337/47fbd427-67f6-49a8-8c94-eb0a51c711ff)

## Смена пароля
![edit password ](https://github.com/spyr000/Messenger/assets/93257337/66139815-1d3e-46cb-8b24-af14ce1a5ad7)

## Получение информации о своем аккаунте
![my info](https://github.com/spyr000/Messenger/assets/93257337/17c991e8-a5b5-451a-a4be-d8147bb364e9)

### Получение информации о чужом аккаунте (не в друзьях у нас)
![user info rest](https://github.com/spyr000/Messenger/assets/93257337/9bf54e82-ecc1-47db-b696-e98f7a8a180d)

## Удаление аккаунта
![delete user rest](https://github.com/spyr000/Messenger/assets/93257337/1fa6b521-7d42-43df-bef5-1772a6bd2ddd)

Логи приложения:
```2023-10-15T21:03:02.141+03:00  INFO 17280 --- [ TERMINATOR T-2] c.s.m.u.s.i.UserTerminatorServiceImpl    : Deleting request for user User(id=1, username=alex1337rus, email=1z.zed.z1@gmail.com, firstName=Alexandr, lastName=Matyukhin, role=USER, additionalInfo=null, restrictions=com.spyro.messenger.user.entity.UserRestrictions@67e9c2de) is registered at 2023-10-15T21:03:02.140593800 with deleting time at 2023-10-15T21:04:02.140593800```

После удаления:
```2023-10-15T21:04:02.325+03:00  INFO 17280 --- [   scheduling-1] c.s.m.u.s.i.UserTerminatorServiceImpl    : User alex1337rus has been deleted```

### Если работа приложения во время выполнения удаления пользователя была приостановлена то при перезапуске сервера удаление возобновится (При запуске приложения все пользователи на которых ссылаются в таблице users_to_delete удаляются планировщиком в назначенное время удаления)
![image](https://github.com/spyr000/Messenger/assets/93257337/5d2f737d-9982-4a3c-84cd-1921c7a677c0)

### После деактивации аккаунта токены перестают работать
![no Auth when delete](https://github.com/spyr000/Messenger/assets/93257337/092eefd7-f862-421f-b6f7-8a04c7d74199)

## Восстановление аккаунта
![recover account](https://github.com/spyr000/Messenger/assets/93257337/495830ed-a058-4afa-9a9c-5de59c22c9fb)

Логи приложения:
```2023-10-15T21:05:59.704+03:00  INFO 17280 --- [   scheduling-1] c.s.m.u.s.i.UserTerminatorServiceImpl    : User alex1337rus deletion request canceled```

## Отправление сообщений (через вебсокет)
![hi websocket](https://github.com/spyr000/Messenger/assets/93257337/4c2c5689-4250-47af-87b5-0d1dec675a05)

### Отправление сообщений (через Rest)
![send message rest](https://github.com/spyr000/Messenger/assets/93257337/b2c8c3bb-d672-4aa3-a12f-088c9c3eef33)

### Сообщение отправленное через Rest появилось у пользователей пользующихся соединением по WebSocket
![beseda websocket](https://github.com/spyr000/Messenger/assets/93257337/a540ce60-9cfa-40a9-8979-57a37d9e52f8)

## Установление ограничений (на получение сообщений с посторонних аккаунтов и на получение доступа к списку друзей с других аккаунтов)
![user_edit](https://github.com/spyr000/Messenger/assets/93257337/16806076-e194-4c5a-80ed-4d54d6f6a606)

### Если пользователя, которому отправляем сообщение не существует или он ограничил получение сообщений только своим кругом друзей
![unable to write message (websocket)](https://github.com/spyr000/Messenger/assets/93257337/dee03192-15a8-43b3-a172-9a2d47397f60)

## Получение истории общения двух пользователей
![history](https://github.com/spyr000/Messenger/assets/93257337/9948e781-700e-4b8b-8bd1-fe59d09c05ee)

## Отправление запроса на дружбу
![sent request](https://github.com/spyr000/Messenger/assets/93257337/f51b4e6d-d94e-4740-a35e-ee08cb50137d)

### Удаление отправленного запроса на дружбу
![delete request postman](https://github.com/spyr000/Messenger/assets/93257337/d861fc5a-b4c1-480d-bfc8-9ea4ad44bdd9)

### Принятие полученного запроса на дружбу
![approve request postman](https://github.com/spyr000/Messenger/assets/93257337/1a3383e0-c3ce-4ac5-b020-9f475f8ae225)

### Отклонение полученного запроса на дружбу
![deny request](https://github.com/spyr000/Messenger/assets/93257337/50084356-a838-4cfe-b7b5-04cd32727521)

### Удаление друга который когда-то прислал вам запрос на дружбу из списка друзей
![reject friends](https://github.com/spyr000/Messenger/assets/93257337/21e4b8d8-e6da-4efc-a0dc-428b31a1e068)

## Получение списка друзей
![friends](https://github.com/spyr000/Messenger/assets/93257337/797baac3-fd8b-446a-911a-6b2bd819661e)

### Попытка получения списка друзей у пользователя с ограничением доступа к нему
![friends restricted](https://github.com/spyr000/Messenger/assets/93257337/eca70569-5c0f-4542-a41e-04fcc9573ec4)

## Получение всех отправленных запросов на дружбу и их статуса
![get sent requests](https://github.com/spyr000/Messenger/assets/93257337/1b46e8e1-0361-4117-8571-cafa1c100eab)

## Получение всех полученных запросов на дружбу и их статуса
![get received requests](https://github.com/spyr000/Messenger/assets/93257337/68a93843-808e-4bb7-bb64-ab96d70b2869)

## Swagger
![image](https://github.com/spyr000/Messenger/assets/93257337/537e4a69-a7ee-4c21-af8b-e6609375c9a0)

## Выбор формата возвращаемых данных
![image](https://github.com/spyr000/Messenger/assets/93257337/a8301b58-2121-4467-8883-a4500198e577)

## ER-диаграмма базы данных
![active_sessions](https://github.com/spyr000/Messenger/assets/93257337/bb93a1c6-fef8-4d15-9ad4-a797f5d3f787)

