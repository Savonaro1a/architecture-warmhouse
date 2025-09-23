# Задание 1. Анализ и планирование

### 1. Описание функциональности монолитного приложения

**Управление отоплением:**
- Пользователи могут управлять удаленно отоплением в своих домах

**Мониторинг температуры:**
- Пользователи могут просмотривать текущую температуру через веб-интерфейс
- Система может получать данные о температуре через запрос от сервера к датчику 

**Особенности эксплуатации**

- Каждая установка требует выезда специалиста для подключения системы отопления к приложению.

- Пользователь не может самостоятельно подключить датчик или модуль.

### 2. Анализ архитектуры монолитного приложения

**Язык и технология:**

- Приложение реализовано на языке Go.

**База данных:**

- Используется реляционная СУБД PostgreSQL.

**Архитектурный стиль:**

- Архитектура — монолитное приложение.

- Все функции (управление отоплением, получение температуры, работа с пользователями) объединены внутри одного блока.

**Взаимодействие между компонентами:**

- Взаимодействие строго синхронное.

- Сервер напрямую обращается к датчику:

    - для получения данных о температуре,

    - для управления отоплением.

- Все операции выполняются в режиме «запрос → ответ».
- Масштабируемость осложнена, изменение одного компонента может повлиять на работу другого
- Установка приложения на стенд требует оставноки всего приложения

### 3. Определение доменов и границы контекстов
1) Identity / Account

   - Управляет пользователями, их домами, ролями.

   - Используется всеми остальными сервисами для авторизации.

2) Device Catalog & Types

   - Хранит список поддерживаемых типов устройств и их capabilities.

   - Используется при регистрации устройства в Lifecycle.

3) Device Lifecycle (Provisioning)

   - Регистрирует новое устройство, выдаёт ключи, привязывает к дому.

   - Использует Catalog для понимания, какие capabilities у устройства.

   - Обновляет Device Management, когда устройство добавлено/удалено.

4) Device Management / Status

   - Ведёт список всех устройств пользователя: модель, версия ПО, статус (online/offline).
 
   - Даёт данные для Control и Monitoring.

5) Control / Actuation

   - Отправляет команды на устройства.

   - Использует данные из Management (адрес/ключ).

   - Логирует результаты для Automation.

6) Telemetry & Monitoring

   - Сохраняет показатели (температура, энергия, события).

   - Отдаёт их пользователю и сервису Automation для правил.

7) Automation / Scenarios

   - Управляет пользовательскими правилами («если X → то Y»).

   - Подписывается на события из Telemetry.

   - Отправляет команды в Control.

### **4. Проблемы монолитного решения**

- Монолитная структура - все компоненты работают в одном процессе
- Синхронная обработка - блокирующие вызовы функций
- Тесная связанность - компоненты напрямую вызывают друг друга
- Единая точка отказа - сбой в одном компоненте влияет на всю систему
- Нет изоляции - ошибки в одном компоненте влияют на другие
- Сложность масштабирования - нельзя масштабировать компоненты независимо
- Отсутствие аутентификации - нет контроля доступа
- Ограниченная функциональность - только управление отоплением

### 5. Визуализация контекста системы — диаграмма С4
[Ссылка на диаграмму](https://www.plantuml.com/plantuml/png/ZLLTQnj757tVNp7LIsNQM1H27_gKLaxYWWsAfJml8scGbkw7s9sjOqh1jcYIue1foKCffMrQ-hvH8Ymxil8NPlvHpznTIxjQBIZ14dFtwfmpvzuZFOWa3sNikQnvtsu6lkIEBq9xp-63a7jsCmv3uKlxCF03rv53wnF7xxbnNx2XbAFegqOZvCUx0po9kt4amXuGqB_R2xp6oEM-GRuNoRxhT1lD-_TQflJqyA3XyKYAi97dt1sXrxAa8rt1rEzgeMxrkPggQtMhfleKhuc-PEgzcgb4lKCXKNFyJnXrtUYn_XdlxzI2eOeFu--3ck5Xmchg7_sB-gW-w0jr3GWycjSigoN2AF3hmy0JmJ4KV8x6N_6X11rJl4_rywnP9JNpy5_raNWdGBh4utEc5Y1O01hTe9ofAP1tBAjzWljvzZT1xFTvU5BtCl-0qlPWEDi7ARjpbTKV1pkr7VQ3nV1lwKXzuFXs6CIIf3rsV7P4XrfsUeG9YUCW_CuKzZkT5ZDAzQb-YRynXCtK5SxFuS8bH1Mn8-57GMWFkTztuOCHHXMsdrM89J2lpQEdUypSakP0W6h6z1dCmUbSly3vXiOnAz70XP48kOn3OMC_ldTw0eYTLPMrq-gAJlsMJPw6dCedjWbjmaloUSw-RdrROkjtRPUVa5rxNF8kZmGxC8KKdNt6HeDdnF4tzBwbwIK6knL4SX2AzfE3TSozRdsKFZGUvVeA9_R5bmJx7-t7xTqom24wtkeB_LFHheMQWEh7vQeyV2OBPl6HKpIABamhchFX6k9fAXEuT9P6OPuEYhZ6s8GByEH8Xe9Bnn_O4PK0IAkudrPPsaWyRmnaXfYKDfrMga9xkbjHQT1K9UpNEITcrRAjJvdwomHPlr9NnZALK2_E30TiDn2GTWhiDQcTw34bKv-jVmyWnzQHSFDvpmTiwtXdOMjd-Z0E4pPzIYtKLAdDs8Q7wygcf2ilffnEzKTLj1bnGJhKdj5UeVrHxFUa4_YioLqNIWr12NHpE5F7mV9YunfNJMjRqbn6rP_RVATK8MzKNqH_IoPZdXjlKWMymiWb7o4l64qtDjNb6b0gjrY0Ql6b_DEYbMcft3XwsEw0V7rrIlcctz8vNVkzlc1wl5ppPPINc8TFEXdcva0N0_FwpairhTwAF30vXR6aBTbFEfPsWhnTwLV4kww_IFb6dsVeM9MRrSNMYHu8lnzxxly0)

**Диаграмма контекста показывает текущую монолитную архитектуру системы "Теплый дом":**

***🏗️ Внутренние компоненты монолита***
1. Gin Router (Gin Framework)
   Роль: HTTP маршрутизация и обработка запросов
   Функции:
   Принимает HTTPS запросы от пользователя
   Маршрутизирует запросы к соответствующим обработчикам
   Возвращает HTTP ответы
2. Sensor Handlers (Go)
   Роль: CRUD операции с датчиками
   Функции:
   Обработка запросов на создание, чтение, обновление и удаление датчиков
   Валидация данных
   Координация с другими компонентами
3. Temperature Service (Go)
   Роль: Интеграция с внешним API температуры
   Функции:
   Получение актуальных данных о температуре
   Обработка ошибок внешнего API
   Кэширование данных (если реализовано)
4. Database Layer (Go + pgx)
   Роль: Абстракция для работы с базой данных
   Функции:
   Выполнение SQL запросов
   Управление соединениями с БД
   Обработка ошибок БД
5. PostgreSQL (PostgreSQL 16)
   - Роль: Хранение данных датчиков
   Функции:
   Хранение метаданных датчиков
   Хранение исторических данных температуры
   Обеспечение ACID транзакций

**🌐Внешние системы**

1) Temperature API
   Внешний сервис, предоставляющий актуальные данные о температуре
   Взаимодействие через HTTP/REST 
2) Home Heating System
   Физическая система отопления в доме
   Получает команды управления через HTTP/MQTT 
3) Temperature Sensors
   Физические датчики температуры
   Отправляют данные через HTTP/MQTT

**🔄 Потоки взаимодействия**
     - Синхронные потоки (Function Call)
     - Все внутренние взаимодействия между компонентами монолита происходят через прямые вызовы функций. 
Это означает, что компоненты тесно связаны и работают в одном процессе 

**Внешние потоки**

- HTTP/REST - для взаимодействия с внешним Temperature API
- HTTP/MQTT - для команд системе отопления и получения данных от датчиков
- 
# Задание 2. Проектирование микросервисной архитектуры

[container diagram](https://www.plantuml.com/plantuml/png/nLZBRjj65zxhAVXxamHGpCs_wYg-kAaRErLijrqQ53MMsL2aG8uI6qK0MsukW9euoQe8WgP95bqLO68hLdnHNs7cZNhc3CqXDHpPQWkqXHtnY7Ckt_dcp3U-4LCdefsMN_cV5xX-fq6iJKhRyHUs7JdtfviUtUpKEp69t32W9A3JRjYosxuJY3NNOzhmlRezz__hDM7wTdd9RZan9H6OrkTWXUC599fk2_yLwb6VMEmL6x8ptcLzzfcTiJxVWTy9txVO8HkmX1s08M6dy7zYyQVic0tvBXlmNLnnodlM57l7Nx0_sGdliSzYrP2TJbKgDHB5ON1Lf5g5Tzw0-OJ_p8xWtuyimTNmF2M-UybE8CIXi43yHwaFbbGj22LY3p7SaE-VBuCKXnR47CAhns1unX-p1DPsBRudteMSX-mphe9WKzSgbTLjWA6rFXjsWeOJRL-DMm3pPjWYwyGDO_nIfF97iK2i-af9WLUchba_LZ94bL-dxQtxpZOMFLDRj9R4v-BB-4xJeUI-ivs-TLC-2JHkXK7JljtqWYt4vZLWjSEVmCyU93HWH_3y2DBRhreiGLjVT0yiFy6tn-hR25nQljVog1Sq0G4L_ZwfhpljDdZ_djIjcNRRzro7Uc4W0guGnwNsTnqo_KCyDQPnV1U23GGU0umFl84C-WBqGh1MMFTyaiPRneVIaDPjUBv7x2_z3WM-fhKFsGVmdpQSzQ7bV4SOuE6JA1m-zo7SWqh5WlzAsk4rI00ytrvlX2tu2hmkfXPh7YsYDRYuu42j0_hUyroINxWgJIB5cw7zjNFFmMpVOt89W7GYKyDyIz7R1ruD0BcCwU8Pwj3ocA_dAgWNidUeKtTYpA8MnhGPaTKxI-QEiK4rlwzsn3jPp0VusuHXWuXo5OBp-3mUltID2rdwOLCjcfC64uQlCDq46tnGifS5PeBiYEj7oDuOsVSsYBlj-aJ5NZetcQB_1a0Ti44QDDr9679Fxsr9TggFfgnQJk0qIGjwgD9QpcoclDuAJ20K14JId8f9XnCpWLpEUAyqct8-PTsigmp6SEczNcCrxG3lYVciKkEzIx791LCK-efESz8mba_PgdGxgcKcp7xbtQndC1Kj-0NSHy0EBN72cKuAFJObFhI7HlbXi7PkC_OC9jUGxo1x7c8UPuevSaVsPIDnefbslugD3LDXyprR33RypjRyhBtcjKZiEZwPdvK7A_Z6A8ecEp1y3ddFd8EvUjTnDukrpmcB7E4DBvOxc3_bJyIaEH5RvmFlPI0Vi25E83J0XcB9fVZYT6ZOmhD2KMOciurdJMwjQfrQRDvlZn2oHC8YUYXunBkIGiLpk1Iqg0Dd7WcQy23GWIThGJnXgFae3FtO_gPDWYN9LS0968ACwI9RC-h2MMTWHXwNUg4iytxMIgiM8gMAQuXjws9nb-cG5r3IZ31q9dqMbKrA84E7WAynJB_rUXJUnONBqc3Dea7aFDCMBBDkEHjt7Nl5gTSzkdm7YtdETr5zF4H-dQMwDTtffy2nixma6ZUNORm4nALXb3kfpasZx7YJLvtgf3ys70YbOOEGUXkUEqhDsyfi8kVgychL8i1vlsgrE_5crg47cSPTsA9Nsw1kHPDaVR5Xb4FtKLvs0SGN-DL7BF6Ta7EPxWR2Yral-04sdFli23yV8uEEyJEim1CWbmJPeYGA71_KduSflCGBnfCKCX2-6Q072AhWtzFq4d8WvXBs3_Nx5Ui29TelKQAyLraXVde7KRANlVqtRXMZme3luwkfo88VKTnNQskrLSXVfQ44yN-J2I2Pkt5OyBxevK2yY5Umr2lumt2x9PfRwbUiAGDLlzEWNrYBrVMo5EoLXTMr5165nUGEj4AE8Lba3imoUM_460M_9RAUFJVVeivZfY5rVvhClL31NjAbqgz6OVetdMiobBsVGDnU644JQkEbs8N-oiINd0IGukEnakf2txeyCCAncpyt-RuE1V9nz8g88NNE5Qzqx7Vms9NRsvQSnrRt80CqYa3Y_COGwUcK1eWnmYWBvVEatj6Lphv_xaonRN9lgl2StbULQspJptFadpuRSnoKisNvpjgQFHDl1swQVGdpb6cY0cmfqA4IisTDPKib3Vvsqb6Sl-vbcjkgHI4DtT2N8EdQ5WEM4RqeKPK65VDBLJ2Y3qSuRtIkR1EssknIKuhi3T-3T4_4m4uXAefNuHDLumX8x3LivK-5mmB_kcGy2-K8U2pU1-EHLTIe9gfFdd3XEiqIFNypVouLJ9kH_9aUOx8M850ZlOJF-tZ5laG_j5rykMgq7MpoRL3QarUo1xP-ggZmN1ZBi5-a4cK7-UDw5mYc133-2R3ub-PCLUbJh4pFQqFDV6Zdnviz5yxKndHPK-tgj4LGYbNqjRcKdm_c3e0ODWvdCzXm-x4NnEN7hi7T9nlNDqZGwBJylm00)

[device component diagram](https://www.plantuml.com/plantuml/png/hLXFRnj55B_xKmovb0fQNpZrrDOjRKKYkR7XQetNKtlLzQwrEqwnKAGq4JHIIWiL9uGeK75td5Xn7UnyXTbln9kNoUnE_6msBK6iDczstk_z-SsyNtCxaJoM_MvG-iGFlQ3V4gmZPI-vLIx7_DdDjYyx_MO_4R4NXLA4ygONTSkzW8Twpur4jWA_MQvySQEgJLzlh9UxF94Y1bEZ4dLxKQZtzBJ_alHb89ZwHKtKK1sbE_3y1_uRCZLLStWvW-TCZTKitOMtqIrsJspvdc0RFEHjqGL7h2PYRIgLgY9Eel1J7TZdR4szXGsdwKjreZUg8JWOwz_NzDexTOPO8x2yLkDqbwLxwVDq5unpzLw_wIs0FrcxVk6tXSYuU-7JENhz7TuN_2Ai6eElCMwQfA-OcZ3Cx1HGNwWXVcWI7aAwn-a1m9Pg0wXQjt4tweSj7WzC08skJRq1cPgObfPbxJhxhdIDtQa-Okjy8E9I1Nh6_L34cMFUynk1NWH7zaFSQDjr0Qux7aT18E8Cj69jkWeF8lOPU-27-lrXlLv5Q7Mi2m95pkgZcwecO5nU-KLu8ARi9sGKDLnQ4Kmk3eG1HcXeDIGYeB2xGl8Mbvn4tp2BnV3FoG1f7cE_P-b1-ZqTnNQfnE3d6hlRJw1fISBMexRlcLxg9QAVJVEjRIgr6VlBpC_bP1UOPUc4II7RVuOpUw8FP_hy1hHphex0C5N3AtVP1a5tcuW5xKH8xou3gx7RQpXXH2Hq36xNYRDcLeh5GVJylzBl21x8JiFh2EzfdciFqSugv_OSx5jCQWGuEzW8dR3E49wldJOKJNwB1pwa6EN0lp6cFEuRDOH49pgpz0U48OXDO6xZqn3yKIX5E-RIZyBLxFQp3ot1SvkBi3iMlIZn8Ob1bjYcjUKo-qaTcjY7QfhbXbNLDCT1nV8jBS1d0jrGcWxY3_1-29YxfgKkfUbJH32OmhKaBe9y5GftHPB096hq-it0JphOf8rp6wiQcqlTFK3I99fQ4k5vFGGAxOBvFGcJJQLGU0vFAyRaW82_Om0uJL-bBxA3ELroEL4ilGUrMarFNKtBo-npcf3tcXSfj9esTWHfyWHlb6gKo7OiQe_NCOM_aOep6tonWqPZscdAuOfP7AJQgZ4tHSjF4FpNzCTq__A5Eheoh2Ka0XDSN0PDpatRfMshcEv_Ar7JjBaKp_Z0pDu7vx-XkzyWWPrq7_xVGtQTMDBfLxozvvWTf99vpMZTZAEd3gVleW77V4z7pxxYJvvoTxD9F_2V26_W1SIGNJTBphY5KpZMizKKsGHALTr1Coeb1sAaIURRbQpwv3agrFKdHI2WS_4Wvx5-ONDy_ebtnWuXVV6o03MwJ9j3oQUQrauNqeFIfWYCh7VQUYKTR-He3IBGxXn7XBX5ohXXNBWeRzwlrKqYRu24UneFJwGvaoFSDm5OVT-CRI3Gjao8OnG4i2l0i4Tm2UxOcnYwj4XI-EpBVkZX_Alm8319BbVDxifAiEoy5m6ZPJ8jfcX0-fGJe5Z_vQMc9NCMQHO4eNPMQneg7D8pfJSWupEKQEXOJN2oEXErM3jT45fT5D4EHVlfuj6weh1cM8P9ArzYp1SGkny6GXu9S3XV_g-AuGUmaz2xEZQ2C7c103A0k9J0IZ64ykAEV3DNrSJKSdYUdHszh1f7ClAYPPGW0EpQnq7OybD2ieXQf7jF24RxPvsDn_LwMT_lb_yKar_BhdqWthuX0pncECAEibEDQ4Jmhe8edWpg2Q9OjA8WfaXX5U5CESHPr1B4WLmj9U23-riYb4dvJZ88FUENq1GVUGfT4EoL6J3mvGoUMDiHN14dwKkcvgHYoDMMJlMlIy86nyF42STb3Ks-__tEkIt2LhyR_0i0)

[automation_code diagram](https://www.plantuml.com/plantuml/png/SYWkIImgAStDuIh9BCb9LRWmvSAE2nikRBxO02Jt0E46XGkxBko-iE5YuyN62_ikRB9ISCxFpKtCIr5mhKXDBYt9BrBGkB0Bg7p7nIOWRh-mzsBtXPqWJVk0klS3oRqNjW85cpIvk0eIaxCJqrCLb9ppyqgAydCK0ErPprFRE1I-yrTuzjAK5auxUyXXPo598Ji4qc26dUdq8Em7qM9RHf9PigN_-qf-aNcAbBXqfxToI6AzJyzFt_lqGuzBa8yyWr-IBtk9-Mi_tfWNI4Z6ecIyIcOYXqArXgnWIdLljptmxo2PZeU9qf9NTwHn3DPrr7IKQIwgY67E92j1WrJ3f6Jr9mHyvfLfkcKPVFbAmP8BoVLgsFqYnalDTACwil2Hs4SIc8RyI0yJpKiuJCq7xPuj8MyA1Ep74CZ-3i02j5oDH5CP0AyqiSpOapLoK9iMwq8qBgPn8vabgWEyIqv0JyU3juGwOa76jzkHzctM5-uDeeTzxE5ejtZtsqSWxTOUvW51KxK0bhSkReGeY6N4AijIp3HZyjxnRysFWkSRvW-C5r7ZjCeab51fT4u1EBnGwmjGJQ6fufqbgkxB9ij0AHerAaPA8OrOm7vxGb6rg1Jqaav55vUjKyns0KtLEZn2AqbO6wCrOPI-qGNwhVfmhxVu81xAzXkt42LdR0Kop42trD4SvAtSHwAg8DEMsR7VGA3Ivdh6AdO71hSWJmIa9SlkGNPQJV6P8Ehcfk1gQJ4pKChqdsEBxoSwYGcWP4ycoAoH4gfijRa6A33SBeKqUiYMhBBwQZ9DkNKoMCSvoF2x2zpGFTZtjFi-QTFvDlHnp7IsT1wCPZzz9axCGu-NIxjxNGjfU9eBKGp-QN_YkkRFFKIBce03W1NDnmOQU5ato2FX23m2ZjIgoa8Mq3jDdnHM-JjeyraZNHzPmNEsa_BDQQt0I4YW9b-P8dzke8i4TMsbH6P9D_SsnPzyeRoRK5pa-SQs_3xvBlbC5RxmeYfu1O7NwEAo3f7Lx8ONN7DGGJzK7VwEoQsvKPdaD-28dJn2fLLaK4iJV64dQeiunISlI-ZngbBEvmSfofrUQVTmivXzJDFjIJWmA0xsB50-Q7quQX-8zKQAUv1NYxF8Xj8ea1Kh8MAgcL9cpkKHquEmMz1AWxyECbu5EvEZOFFSHRdxKNBwAIUg4LHEAevREGny2Ds3UlZlMPfUnfsAccS3RJzVEzg3DGu7wGu7cxvgjYe4o-tEOiMuyxnrcgRpNLDegNKzMa-ZVViGcKTC0Pb5_TloZvVrJrcNx1viRXVmq83I_uzTakk65xb9TiGVyswO_es7Rgvu5apmC6Xl1KIDv9Zeox0CxczKVMCes6hduQy7SSEoUt5xEpE3vdN1RNfk2y7qRxyIqDzSQv21VYwNeYbolDg4d8PNh29h2gOnve7qRwnlTrv3p-49hr2fmOJlelSJEX_QBfEd9MkKXhmlHhiakXWdJxMeeDAS5SV8K5_0n0lBypzRnWPvXFp--uchUhHt0Ykau8hIMZQ48g4MYciXLzq05rsJDyPi2GPYlrvvtNY78VMUIybShl4WI-xZhXkTGIfMG27kY1HwVD6vZ9XWBbCtfrnPUYAJkk0bnqnxbvE8jxctIROBrG3ivp2XRR4TkAqBKP3FARxFjgUh6iWGQPT0Fov_BZ1FGK5oFGtLKkHDPZLmpsxlsU4SdxsOrmga6ozLORj-XjRlfyhlILVf0RvicD3M_iue2rJcwyNumxFNDYVjzkXdZcoR_ethb9oCF4v94pqAqLxzXExewkw8I55a4edsA8BBR6_nor7IN-sbVFRNoVeAn7RRK9gLzPOo5zFisqfGDgnj1KyNstVNKsqOFRKmAYEiPOHd3K8s8lmj9ET6-FljTtZSVR0TphVS3TSbsTQfmZ1TcVPpC9v9rVhwbXra30Yb2pjTX7tODTTBzzgTNMZzX_-BxWsVFR3ZLUUy3luXiDt5g00cMoUnIzOTlg1bDkvsstaIork9hM-HzBuKlRpamhgiEToiSn8CgQW5YHzwkY5MfKKzMh8gMa94jtV3n9RD3gyProTIDFLcLMyGhzz5ce_o70SOiIsW58yGc4acqTQUuYp07oQEVj1NowC0Fn-u3YO6qsNFIBqfLMi7yylVL4X-vfFpyVJyf7kU_3KPNQMqPNGncvzDqicuQ_bmD3qZZwEZyz7aZBQaqzdauYhr0yL0kW1936SNrztlyul5xEYiUpoTdfo-l6WnJQSZQmsgX-c58aeazLHFYgdGh5Wp4OXj3bLkrBxDZAhT0J3jTG4Qycq0_6_DDYjHY6T2_NYHMo4423_yE0R7q8SA1X95aT5GI8_eCNDVz_RifRHexo5goMkrv-uUpyy71y_F6_-K6he38kB86hB61uNXOTAexMXwwUs1qejF7pWuupfuh6fwUFZyUoHiRhct-wRQYnMA-dlv0Rl8WeiPXXR0UmRYxU2-QmCfBQC6HJO2WY5yeRieaBp0eO9WV2oUtOPuB3PmsXp6AE4M95HMIdz0bJTbyHy0)

[device_code diagram](https://www.plantuml.com/plantuml/png/SYWkIImgAStDuIh9BCb9LRWmvSAE2nikRBxO02Jt0E46XGkxBko-iE5YuyN62_ikRB9IS4ajoqnELGXEBGBJ6XTs0HNlkDW4nEqNzbxilh0Jf6KVKEz-8BdtmbQWO9Cc5rT1Od9sOdggWXBK0D_4F20tDpMl16QMaa8rbm8GW0br1UVyebG5G3jMizos3OJlUGhUohYn7q27JcL9ZZcHAKLcacapES0aP6420Ym0EgE9_Uub2Asq502vwgqpzS4YiTzYV_1niS1KreI9v4vMbEjUK7AYDNXX5TpVPP9DHuasYeb7D9YJcWQ6Ysr38HJxZ4HpMHB3f0XetHjYMWrwxWs9xwWX5J440F2E81D5YQ7Ls8mImsfwLR0Qs_xKL77nhybxQh9f-XPvXYGsqfZCXcXbE60iXvXZ-YvKc052HnZd-0wc0sdgLAbIKdawaAIqZcGle9dfXU1Cz24zICcHw3FXp4wR3jHU-l-OFf28Vq-X2Md80-FCCDh5-lNR_dsBG4ibZImb7-sVC5-8QDUaDAsYAfntR2Dzwra1v7tRD58PFP5rJKILbkuNvXna2QO6V-oqy1pF5IpZ85jkA92QET7cdb8H9laj9TniHhjVxokw9iPGjPtJ9zgbZGc3BR97GVYzKLGycWsInca18cTjwyc0X-2p1Jn9LaLpWQY9i-5AL20PDqpLFuYYeNf25ICyR-i7H00qutWohYf5TIZ7sJ8YErt2LbBMWU4_fV0TZ9G6FtK0uKTs8wdyPRzvpCcMAY-z8CNPhNh9YZOIweDzrin8XRytLtempF4h28QAW-6mj4659rTSf0BqPnX0eAPzu4nlx8hUKQt98rt2249jUZfEDhJyJlVJtU91b6QtQpXCwZ81AbsAqmlt-CuLlBV3zTjfCLmDeXgmefxIkwJj1HhiEjpfkQQKqy1SWO8-Pthdg4xNZ9j3IcxwDsldwxVNd4RhFN3zv7xa9GiGePI1BEh3a4G3B-BxMzmJm8Hz0Md06qVCA6SyYpkAEldiYGgJKlj_bA0XqFE_vyFNWZvf9WnLQr9IxuDDVdhaFfVMrzjiUXwfXtIEculn-7K6dqrT2ANODf00-uZClAhgrzupb8Vzscb5VpFqbZMh6puisW-Zv4B_nQ-crsYq9EMcYt5YV_xdmN-D1x2A3otZrSUMgcqiNA_9g4cpaZyAwN2faZzszEX6Ovd7PGQg6fCYs3cW_NX0GzLorw8Vz5VTqFdbCeBtbcq_7DSz_-03kkSVjCPLPUDDRHv6oJ099xlT8HJbD7ueag9YZdxhLfJfBeF3bliI6gD0Hpbx8hmDrW7_2EDcGOTUdp6keqhwbihdZDzq3M8ifH2qHEtU10OGo9vmP0i7zcch-dnXzb6ZjXFP2dDqNd5UEVD-kA7J4UYCEOTkgKFMfFdgulyMKUe6b5bInMJ5odw6jD-SkrmV-Otf0Bgo6QeY3BIyLAsbgb4cJyVZhHCG0lgGVYrXdRmsUh18ZP9LMzhoyvsilvEhfNiVB95iMEbpxSocnzDpiRWzPHTlDZstwuJ9uAFtfpkZVGJUsfqK1mIRINZBmz56Dte0l7vTWesXOJT7M8FLL1jID-7xWgcD6hDytrZ-V0bVSuNEiM0_9hTMh2ninsVij5ZeX8WgSVkTVpVfLZ9GD2_YvtMG1IyLZxf-_o864Dxtd-bPtPZGtSUuQVYsa6c44wVeuEA6-63fpuuFV0T9-7eiE2khcJc-8Lgirvh2Q8WR5wSlEI80vsLKZ5sDWdf_hTJppJaD8ezgCz7MRZbfznIxZrlayooV7LvlRWRlT-CiBsRvE9ySncQhrMArV_kKVyWNNt9i7spYwerjtix6y-BsZ_txb_4gp_Bt-_V9AYkooNZ-Zsp0BIIQ_-vZKHoC5SNoCFDYl3oOdLtVBoOVPiLktfStR-oPbFThgZUiqMzSqfBdvylBvsTy_JjADYGeXWp70V0b0WGkhtmaElgEabRJE1WtiQUm_gPu2kntEXZl7-Akhfv_2xdEEY6jgJ1E3_jgBScVnotZo3PHXZ012WCVi7lGNQ6ulH01ZhQZaTlj812ekWEadQyR1X2KBW-qtbNotwcesfh_3G00)

[rule_engine_code diagram](https://www.plantuml.com/plantuml/png/SYWkIImgAStDuIh9BCb9LRWmvSAE2nikRBxO02Jt0E46XGkxBko-iE5YuyN62_ikRB9I22e5gdFDIy_CIrNGkB0Bg7B7nIOWRh-mzsBtXPqWzVk06lS3oRqNjW85cpIvk0eIaxCJqrCLb1nBI_9p4qioy_CKWbEBoZAJKvKKghaK0ErQpMxRFXA_vobqgvkcEItsuCDY5Lj91VWZjPMsYo8868cnYSgYbgIJ64tUVKMD9S-OOjPDWiKU_ZcuuioFm_aOZePKgx_wvqCma8e7mmeZYc0iCvxhcf7cJ6jOsyBXxtCG3_k1DaeK2qISi1Nl80-vJfKexTeTt84iCc5vkX_yl6v7216cBHiU4UzI2Qc4sVG3KHX4Zmfsc_Ei7znAcUFL56U6Pw7f1qQi-6bI_I3sLPczn1unRQ97dgwtcECk400gDGToNHYgsAVWWfjusFk8784ure4-tivF5e4yHM5XYZ0mG81ORnrZ-TOrY1VTitpD3E-biZ3yqK363M3WmC5CFveh9HLUkKWLN_72KFTOFUwbo10IOe1TJK3FzI_ApTOH_pr1aqt9-xi9TeYuvuBdMSUiQSaLCrBrsoV4_5OvZTT1vkgEfVptCrvlgRXUIYlr-2wNpFpzRvWhLloxA3Bvq6JISAtgBNfelbW3B8zQH22rKRxvZJsMtpnJH6sUPIC3NvypbJIHMJXE43UYOVeduGIqVSOifXRSTCovP8gjkE5AzuCLAty2uBexVaEUiuqdE9O_uqPjy3PlEMFsQ9c2QyGvFA3O9im7itopTxPPXCUwL-zW6ZpA6x2YzWKjAtJZjm3lLiOAaEWFkNrXqHmWBCW4XpcIAIEl7Rhvj66hihlI3vbX1oJ8c1kMlGZ5yG5T_T7VsU93D0Rv-ArL5e3jGXXhcZy_A1RiEn0_tyh-nJUDTYV1lLCVKGPGfQ3MeDdUilE5wVsLKEBXlFf0sYRRu-ob6t18jgcAFEEbrC98LIqm8sC4J9ko2sMmVaKC71gQm5BBWNrdmI4UOw1EbpohbbMm-hmP8aYvlis5Nbh8c6lD5lmIA0IriaKerI1c30F4tcxzixN8isQn0Q5r57iBqhwgqVjvRGadK28-kkN6SdlMnVq0W0UM7pHTr_FhXl2FHCnjpztw4fUY5Y1BBnyI5Vcn0dZScakMWQAkHn3iJkI6gx0iSv7Qhknmsngsa-we6u9msKUKycvwajJdMshpTYIDnRKx-NnTf1qPqW9xxHP1EuFQvEyMPdmXjE6gNMqdpMtah7_zkn0qfG75xxIN5SQfH1t5eMI_tLuiqvRizvLeY-NxIemU3LS5ozygrTlDqyYrIOgpxErBVcXQJIWkTAlGuaot2H2RNRBhrgxH9BGzKBLLCX_aWjkESO2726PupctTtZJ0X1A88X26y39km14XKNWXZRWJq48q-8b3SuikMDLZAJWRXWULNRoF6wzivP3k7JQmvyNuqjxrA4VLG4637Vi6Yv0exBft4OXYzoxowlFBQoKvMzCD2jsMiEQh5qBjslOTB8e5MXutGdGxaZSctaWY_SLLUqrJZn1R6raEbgnIDQr-gw5QfuQqzm-BVg0tHNhwdGbpeUIwV5tl12f2gmb0UDxlJNfrkrkLjtv0ZfpTdKhF9OTPjjTVkPWPNybxti3GgSft5UELYDgY9Z1jsuEZGNjQG60fm2-LJBdM_jLeeZJdnDhNRgRGI91qoUeAqDG_g0V_ioBy2HJUgjVBzaws9y5BnqdRJHuZwqXFwKe_Lt8LtLUhz_WzihKclK9Kwu5Uwnpa5jjZ-8HYPnLAfCl6_0FBsQ8v6pVdKSTlTkLkdo4Ta3YizjkbDU5-Yp2t2kwTETk9UmTIKeJXVflk7mW9MWpjsxDDFEo93Elged4nGE2JuB1bXZpdP697Zp0IslJWF0G2p-jdE3xvrOVRR7hMye9hrDa6CiaDqs6pgqu5t6iV_7DzzriuSF6gp6bXkgtULVhV-Udm355JbYxjOSV-yrUa_pyZ3P6wjGNpwvghJQU1pYMahV8p-P18UEV9XstuAnhsigT6RAyT0bPamLWMDgNSrs03mccMICFosaZFLxDE7hHt7kOv4xvvuJrNRC7R4vOztUwrDyuNDWJrNcFkhDX-dl3bYmU0xF4Wm2W_lyio2tpXKuvZ7hcdzNykundjcbmWqTRNoT6biwsA_cQJ9bnKh5UwVkRL4xrzGGasZBx5W-XcdeJ9rNoV-YqSNKKjCOd7qSrPE8-6EroOX7kaw4SIpIRXwARgfbhYO3g-dCxZ9C9wjhg5gL-nQkOudEp4JwP9V1uFmYIUJlnhWe1H-AzsD9fUxE3ZC1wre_butgLL-uaDwHHzlGf7SwpBpHxfOXQ5bIutoPTmibixciynfON1R8gZfC5qaeJnXAxf4EC9uQFXBBg8VY0_nVE4oa8aaZx3PAffTOBA2EwW_uVWsFEj3zFffo7A8-MiOktTOet4IZYVxAPtTvfJAlqgV-pvOkTdDifv0UXhFD4HkBMQdQK7TCMivwFdewEgSUHvlOdrKfJwg6uZdvu-VvQ_qSLmlpwwCb5GG9XwkKzFvDDSlxq6r4VkVPsp8Bt9wmThNSr3bmbe3gPIV7jenXhXBjYBS_NmO-dwVXo-Y_33d8Cemc8165u1LgnW2p_6Bz9fBazFd_xX-abKhFgL0NEejEvufuhNqSOj1LNQA9R-qaV_v4MsNkN_0G00)

# Задание 3. Разработка ER-диаграммыc
[er_diagram_code](https://www.plantuml.com/plantuml/png/b93TIW9168RFvog-3Fev29HS-i6aTTsb9T8GD-XmR8SSt9sHsV508iXEfODkeIiGCh0IkuLlxgZ93AG8wXsOW-zzU0xUmqnJfNjfGbPqcwKCkWdbWcGTBhfKqHGIBfZkTnb8fTjogMZHYywbaZqHUpAH2bQ3UPO89ZJNVRWYO5FvZlk5m0dypqwgcAd5FLzmTlOgv7f955DDMpHZlwc2RTy9d9-gebiexdfMHJJN2GE_iOLZdE087ysD_QVsZS3SuGlEp00dPc1kyGcdPWWvV33t-8QlPeZFW6ESuJH7o3eS_3aM3gj-hHasps4DNC-hHxKcLEidRbZxduWibknbJ6MB4NWC-n15OHLAfTFZSdb-_004JPcjpjo6T-GsDfpyfYMYBuAbbByNPpKxRjj0-BvFaGT8W2JejaLFTHrdwy8_mQvID2U1fiOoknIf32axJhVlNacC5XfdQ6Sj1pqPvZSaPpxEpv3IFVZrzJN1I-7SpcAMReJR7CYr0ck5pgS7cxnw-JAWMXm1ouqrq5_poxZpmbUE0II2y92bmjTWCfrFxfBH_5smjCmuwDz7hZQsSd2IhBIFlEsGc04Yon2SQtQS_9K45UkzE55fA-EHXKhhlD2fj1b7XXPU-Qg5tap6q_beLizy_lCNq9hyA_HLGByFZu4bjFfSBfkfr1sRuN0y_8mSXMbKMkrZ86yAV2eLWkCWLOzAhxPG8xyjbxF9Q56R-CHehXd_SpgUF1kFaj5iULF7TFBXtUHkK4Hdi5KIKhylUNvFUQGK7dABUuRXGfXgBQIl492RMjWCD0Tau2IgqYjh-fvUelLMMitvboZ5Icdb5RWQ_kDkkGZZrXYGpK9fcyW9g6eqhL0JrSE2t7aDS6H2DT4rpdZlP1Wt9KRl-OH7HPfrc4DgQG4FIAbnASB7IY5atqJ0-RuycxwTZ3-CPvFXNXodytYydsOwHgy3L4AdfYfMXovEQbeXPDgbxfxKJqH8bOpC3zTTtfMYSMDE2xIsuCO_MyDh7SlI6n3QRz8elbPOx0H2kWLqL9dhVlbr0EQhy438bVE0JF1SAAu0lLehAVotn1L_qBqrQEPHM-4XlOSz6QvMMid3KULSTP7wmFF26szt1j1jL7ckvV7w-RWqJI6TMnojY0XRUm_PTw-8yz5YT3EPrxtk9OfX0LvamekpkEGB6mqWhXqHc9s8BMVNFYvogWwkLLvXcqC8K3blYrHuZsfLUJBh4a4PSacwKSvRt9z1wB2CjHBU0-vJ3TlsHAQBP78pkGsBgjm8NSz5CBdV7A0UYX9G-0hxhUxryzV7yzm52-HukKXkbxFsvfQy7oNJvMAGSqjR5C9axl8PGiXOQUAxYnALnLebIddyb9tSG5RfM585MNaUgqqkDKHpawyzexqc7IXIY4zfWofm1uXN1TYgENbfJNQ80gB5j03dH4xg1u8hhN6GDbbbS8JHyqm_Lb31iKFj1Bcp6t0MQujKwJ0DppcBdSNxu5tKYIuRKSbaLjU8vBPv3Pal5zDaUJjTt0mAAWyQYjQH1XMCnjbah8lluAtG5RbtNp__aGXKHddco1gdTLuKvLUqVwn4SpTWvdGbCEwiyPmFMp3-x3JJpWwj1aD5G0yrS_LmSE8d65-YbU0SP4zjkOG6z9TFes7TgENX0R5dO0Sm-IREHpomFQeyZxNdGjuRkzEGvR5SwgpUKkpY1N7qFbdEspfOn-_TUB8OtKwNmru_d0GZKDapdvRuEoLtPNnzucr3kHICM6dgZV7y7AgUieOhOA4x2IANlsvXTU8fhYDrE36y_N6-_dzYqgOlKCt-noWAeYmzZ_DZ3pPh1PejaxRjLoqoiHqgHuzLlq5m6wipffo4d9dkbCdiZl9tzZKr62pa06A4FCQA5vW3ANx7Mz0CjDWpRTmPZU5mOe973k26J-fdzcCqYbfVABwvqvNMUFX4AlHbYy1YcKpVJiV3hqF6XfUUO2NKP0X_tXDun2LTjVeR9EkJAvkH9wEjGDKaC-AYbt-2faQVMVCwgY8epmM2LAK2u-a7lPD4CHuMztKmdYCb0wssV6F3EpBJ05_OYMydizQ1xtwVlhjxskHQUfg5qsnU1cH8KmWwdEb6bUugVALzU7ZnmluJUfI6Bxip5EfzMQlXZN1NGPQ5HLbg1Ixrjb7XJTlWFXiBBxJD3xOddvJ2MkotZx1dNBgMw3ulgS_WUllEr1haNcgWEnFzoIhMUA5CjrcGU7XWNew9LcGf-ieQf3esUPb0UK_jkJ0YXm8E3YocLkU2l4hi0rijceC153xmDRR6ezNyTjrAtSDGFHb1Wje2iOm8EAX_hxg1Jc7o555BjRzPgaoZGz9TaOzhGM_In10gIQ-PHmIxYzeltTqcc4LXOdLpoQYyBLejZmLLr-8siqDM8Cv794ZiF0vtSbXfwg9Xe4DfInayWH4HCeQJ1YE8X3_1t4LN48uRJ0e7eOmjez5f3KDNluB9gaB_1m00)

# Задание 4. Создание и документирование API

[api-telemetry-service](apps/openapi/api-telemetry-service.yaml)

[device-management](apps/openapi/device_management.yml)

# Задание 5. Работа с docker и docker-compose

Перейдите в apps.

Там находится приложение-монолит для работы с датчиками температуры. В README.md описано как запустить решение.

Вам нужно:

1) сделать простое приложение temperature-api на любом удобном для вас языке программирования, которое при запросе /temperature?location= будет отдавать рандомное значение температуры.

Locations - название комнаты, sensorId - идентификатор названия комнаты

```
	// If no location is provided, use a default based on sensor ID
	if location == "" {
		switch sensorID {
		case "1":
			location = "Living Room"
		case "2":
			location = "Bedroom"
		case "3":
			location = "Kitchen"
		default:
			location = "Unknown"
		}
	}

	// If no sensor ID is provided, generate one based on location
	if sensorID == "" {
		switch location {
		case "Living Room":
			sensorID = "1"
		case "Bedroom":
			sensorID = "2"
		case "Kitchen":
			sensorID = "3"
		default:
			sensorID = "0"
		}
	}
```

2) Приложение следует упаковать в Docker и добавить в docker-compose. Порт по умолчанию должен быть 8081

3) Кроме того для smart_home приложения требуется база данных - добавьте в docker-compose файл настройки для запуска postgres с указанием скрипта инициализации ./smart_home/init.sql

Для проверки можно использовать Postman коллекцию smarthome-api.postman_collection.json и вызвать:

- Create Sensor
- Get All Sensors

Должно при каждом вызове отображаться разное значение температуры

Ревьюер будет проверять точно так же.
