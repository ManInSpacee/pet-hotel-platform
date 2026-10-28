# План по JavaFX-клиенту (модуль `desktop`)

Для второго участника команды. Сначала — как устроен JavaFX, потом — своя часть проекта.
Идти по порядку: каждый этап опирается на предыдущий.

---

## 0. Что за проект и где твоя часть

**Pet Hotel** — система для персонала гостиницы для животных. Основная сущность — **бронирование**:
питомец, вольер, даты, статус, стоимость, особый уход, теги. Подробная модель — в [booking-model.md](booking-model.md).

Система из двух частей:

```
desktop (JavaFX)  ──HTTP + JSON──▶  backend (Spring Boot, REST API)  ──▶  PostgreSQL
   твоя часть                           делает Клим
```

Клиент **не ходит в базу**. Он только отправляет HTTP-запросы в backend и показывает ответы.
Поэтому в `desktop` не нужны ни Spring, ни JDBC — только JavaFX и HTTP.

**Что спросят на защите** (из письма преподавателя): Java Core, коллекции, JavaFX.
Лайв-кодинг: JDBC, ООП, интерфейсы, коллекции, JavaFX. Клиент целиком на твоих темах — разбирайся в каждой строке.

На защите надо уметь объяснить: что такое Stage, Scene, Node; зачем FXML и как он связан с контроллером;
как обновляется TableView и зачем ObservableList; как сделаны поиск и фильтрация.

---

## Этап 1. Основы JavaFX — песочница (1–1,5 дня)

Отдельный маленький проект, не в репозитории команды. Цель — руками пройти все механизмы, которые потом понадобятся.
Тема любая, например список фильмов. Каждый шаг — запустить и убедиться, что работает.

**1.1. Каркас.** Создай в IntelliJ проект JavaFX (New Project → JavaFX, Maven, Java 21).
Разберись, что такое:
- **Stage** — окно;
- **Scene** — содержимое окна;
- **Node** — любой элемент (кнопка, таблица, контейнер). Всё содержимое — дерево Node.

**1.2. FXML и контроллер.** Интерфейс описывается в `.fxml`, логика — в классе-контроллере.
- `fx:controller="..."` в корне FXML — какой класс управляет окном;
- `fx:id="titleField"` + поле `@FXML private TextField titleField;` — связь элемента и поля (имена **посимвольно** одинаковые, иначе поле будет `null`);
- `onAction="#onAddClick"` + метод `@FXML private void onAddClick()` — обработчик кнопки;
- `@FXML private void initialize()` — вызывается после загрузки FXML, когда все поля уже заполнены.

Упражнение: поле ввода + кнопка + `Label`, по кнопке текст из поля попадает в `Label`.

**1.3. Контейнеры.** `VBox` (сверху вниз), `HBox` (в ряд), `BorderPane` (верх/центр/низ/лево/право), `TabPane` (вкладки).

**1.4. Таблица.** `TableView<Movie>` + колонки `TableColumn<Movie, String>`.
- `record Movie(String title, int year)`;
- значения в ячейки: `titleColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().title()));`
- данные: `ObservableList<Movie> movies = FXCollections.observableArrayList();` → `table.setItems(movies);`
- **главное:** таблица *наблюдает* за списком. Добавил в `movies` — строка появилась сама, без перерисовки вручную.

Упражнение: форма над таблицей, «Добавить» кладёт новый фильм в список.

**1.5. Выбор строки и удаление с подтверждением.**
- выбранная строка: `table.getSelectionModel().getSelectedItem()` (если ничего не выбрано — `null`);
- подтверждение: `Alert` типа `CONFIRMATION`, `showAndWait()`, проверить, что нажали OK.

**1.6. Второе окно — форма редактирования.**
- отдельный FXML + свой контроллер;
- загрузка: `FXMLLoader loader = new FXMLLoader(...); Parent root = loader.load(); FormController form = loader.getController();`
- передать данные в форму: метод `form.setMovie(selected)`;
- модальное окно: `new Stage()`, `initModality(Modality.APPLICATION_MODAL)`, `showAndWait()` — код ждёт, пока окно закроют;
- забрать результат: `form.getResult()`.

**1.7. Удобные поля ввода.** `ComboBox` (выбор из списка, в том числе из enum), `DatePicker` (даты), `Spinner` (числа).
Чем меньше свободного ввода, тем меньше проверок.

**1.8. Поиск и сортировка.** Цепочка из трёх списков:
```
ObservableList (все данные) → FilteredList (по условию) → SortedList (по клику на колонку) → TableView
```
- `FilteredList` + `textField.textProperty().addListener(...)` → при каждом вводе меняется `setPredicate(...)`;
- `sortedList.comparatorProperty().bind(table.comparatorProperty())` — сортировка кликом по заголовку.

**1.9. Ошибки пользователю.** Утилита `Dialogs.showError(String)` — `Alert` типа `ERROR`. Никаких `System.out` и стектрейсов.

Готовый пример по всем пунктам — песочница Клима `javafx-sandbox` (попроси скинуть архивом).
Сначала попробуй сам, потом сверяйся.

---

## Этап 2. HTTP и JSON из Java (полдня)

Клиент говорит с backend запросами. В Java для этого есть встроенный `java.net.http.HttpClient`:

```java
HttpClient http = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/api/bookings"))
        .header("Authorization", "Bearer " + accessToken)
        .GET()
        .build();
HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
// response.statusCode() — 200, 404...;  response.body() — JSON строкой
```

JSON ↔ Java-объекты — библиотека **Jackson**:
```java
ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()); // чтобы понимал LocalDate
List<BookingDto> list = mapper.readValue(json, new TypeReference<List<BookingDto>>() {});
String body = mapper.writeValueAsString(request);
```

Что понять:
- **коды ответа**: 2xx — успех; 400 — неверные данные; 401 — не вошёл / токен истёк; 403 — нет прав; 404 — не найдено; 409 — конфликт;
- при ошибке backend присылает JSON с полем `message` — его и показываем пользователю в `Dialogs.showError`.

**Важно про потоки.** Всё, что касается окон, выполняется в одном потоке JavaFX. Если делать долгий HTTP-запрос прямо в обработчике кнопки — окно «замёрзнет», пока ждёт ответ. Для начала это допустимо; позже — `Task` в фоне и `Platform.runLater(...)`, чтобы вернуть результат в окно.

---

## Этап 3. Своя часть: клиент Pet Hotel

### 3.1. Как работать, пока backend не готов

Backend пишется параллельно, эндпоинты появляются по одному. Чтобы не ждать — **интерфейс**:

```java
public interface BookingApi {
    List<BookingDto> getAll();
    BookingDto create(CreateBookingRequest request);
    void delete(UUID id);
    // ... остальные операции
}
```

Две реализации:
- `FakeBookingApi` — хранит список в памяти, на нём строишь и проверяешь все экраны;
- `HttpBookingApi` — настоящие запросы, когда эндпоинт готов.

Контроллеры знают **только интерфейс**. Подмена реализации — одна строка при запуске.
Это ровно то, что спрашивают про интерфейсы на лайв-кодинге: зачем они и что дают.

### 3.2. Структура модуля

```
desktop/src/main/java/ru/mirea/pethotel/desktop/
├── PetHotelApp.java        ← Application: start(), первое окно
├── Launcher.java           ← main(), вызывает Application.launch (обходит ошибку «JavaFX runtime components are missing»)
├── api/                    ← интерфейсы API, Fake- и Http-реализации, ApiException
├── dto/                    ← record-ы запросов и ответов (зеркало DTO backend)
├── controller/             ← контроллеры окон
└── util/                   ← Dialogs и мелкие помощники
desktop/src/main/resources/ru/mirea/pethotel/desktop/
├── *.fxml
└── app.css
```

Сейчас в `desktop` лежит первая версия Клима, которая работала с базой напрямую через Spring.
Таблицу броней и форму создания из неё можно взять за основу — но Spring и зависимость от `backend` убираются,
а вместо сервисов контроллеры вызывают `BookingApi`.

### 3.3. Экраны — что делаем к сдаче КР2

**К сдаче КР2 нужен только обязательный минимум ТЗ КР2.** Вход, роли, заметки, массовые действия —
это КР3, к ним вернёмся после сдачи. Поиск, фильтры, сортировку, статистику и экспорт клиент делает
**сам** по уже загруженному списку броней — отдельных эндпоинтов для них не нужно.

Кто что делает:

| Часть | Кто | Что внутри |
|---|---|---|
| `ApiClient` (HTTP + JSON) | Клим | запросы к backend, ошибки → `ApiException` с `message` |
| Таблица броней | Клим | `TableView`: код, питомец, владелец, вольер, даты, статус, стадия, стоимость. «Обновить» |
| Форма создания и редактирования | Клим | модальное окно, питомец и вольер из `ComboBox`, два `DatePicker`, флажок «особый уход», комментарий |
| Удаление с подтверждением | Клим | `Alert` CONFIRMATION, ошибки backend — в `Dialogs.showError` |
| **Поиск, 2 фильтра, 2 сортировки** | **ты** | поиск по коду/кличке/владельцу; фильтры по статусу и по периоду дат (или особому уходу); сортировка кликом по колонкам дат и стоимости. Цепочка `ObservableList → FilteredList → SortedList` |
| **Статистика, 5+ показателей** | **ты** | отдельная вкладка: всего броней, по статусам, сейчас в гостинице (`stage = IN_HOTEL`), средняя / мин / макс стоимость, доля с особым уходом. Считается по списку через stream |
| **Экспорт в CSV** | **ты** | кнопка, `FileChooser` (путь выбирает пользователь), выгрузка текущего — отфильтрованного — списка |
| **CSS** | **ты** | `app.css`: кнопки, таблица, формы, заголовки, вид ошибок |

Твои части не зависят от `ApiClient`: делай их на `FakeBookingApi` со списком в памяти,
а когда таблица Клима заработает по REST — подключим.

#### Следующая задача: клиент по REST (28.10)

Твои `BookingDto`, `BookingApi`, `FakeBookingApi`, `BookingStatistics` — хорошая основа. Теперь клиент
переходит на настоящий backend. Порядок важен: шаги 1–3 — общий фундамент, после них Клим переводит контроллеры.

1. **Влей свою ветку в `main`** (или открой PR) — чтобы `BookingApi` и `BookingDto` стали общими.
2. **Убери из `desktop` Spring и зависимость от `backend`.** Клиент больше не ходит в базу.
   - `desktop/pom.xml`: удалить зависимость `backend`; добавить `com.fasterxml.jackson.core:jackson-databind`
     и `com.fasterxml.jackson.datatype:jackson-datatype-jsr310` (версия 2.19.2, как в pom из ТЗ КР2);
   - удалить `PetHotelDesktopApplication`, `application.properties`, `database.properties`;
   - `PetHotelFxApplication`: убрать Spring-контекст (`init`, `stop`, `setControllerFactory`) — FXML грузится обычным `FXMLLoader`;
   - новый `Launcher` с `main`, который вызывает `Application.launch(PetHotelFxApplication.class)`;
   - `javafx-maven-plugin` → `mainClass` = `ru.mirea.pethotel.desktop.Launcher`.
3. **Интерфейс и DTO — общий контракт:**
   ```java
   public interface BookingApi {
       List<BookingDto> getAll();                                   // GET    /api/bookings
       BookingDto create(CreateBookingRequest request);             // POST   /api/bookings
       BookingDto update(UUID id, UpdateBookingRequest request);    // PUT    /api/bookings/{id}
       void delete(UUID id);                                        // DELETE /api/bookings/{id}
       List<PetDto> getPets();                                      // GET    /api/pets
       List<EnclosureDto> getEnclosures();                          // GET    /api/enclosures
   }
   ```
   - в `BookingDto` добавить `petId`, `enclosureId`, `comment`, `version` (без них не работает форма редактирования);
   - новые `record` в `dto`: `CreateBookingRequest`, `UpdateBookingRequest`, `PetDto`, `EnclosureDto` — поля из раздела 3.4;
   - `FakeBookingApi` дописать под все методы (список в памяти).
   - **Запушь сразу после шага 3** — Клим начинает переводить контроллеры.
4. **`HttpBookingApi implements BookingApi`** — настоящие запросы:
   - `java.net.http.HttpClient`, базовый адрес `http://localhost:8080/api` — в константе или в `app.properties`, не размазывать по коду;
   - Jackson `ObjectMapper` с `JavaTimeModule` (иначе не поймёт `LocalDate`);
   - ответ 2xx → разобрать JSON в DTO; иначе → бросить `ApiException` с полем `message` из JSON ошибки
     (формат — раздел 3.4), чтобы контроллер показал его в `Dialogs.showError`;
   - проверка: маленький `main`, который вызывает `getAll()` при запущенном backend и печатает брони.

Твои пункты из таблицы выше (фильтр, CSV, вкладка статистики, CSS) — после этого.

#### С чего начать прямо сейчас

Главное правило: **не трогай `MainController`, `main-view.fxml` и `BookingFormController`** — Клим переделывает их под REST.
Всё своё делай в **новых файлах**, тогда при слиянии не будет конфликтов. Работай в ветке `feature/desktop-kr2`,
коммить маленькими шагами и пушь хотя бы раз в день.

Сначала — части на **чистой Java, без JavaFX**. Их можно проверить обычным `main` или JUnit-тестом, без окон и без backend.
Это тренировка коллекций, циклов и stream — то, что преподаватель даёт писать в блокноте.

| Шаг | Файл (пакет `ru.mirea.pethotel.desktop`) | Что сделать |
|---|---|---|
| 1 | `dto/BookingDto.java` | `record` с полями из `BookingResponse` (раздел 3.4): `id` (UUID), `code`, `petName`, `ownerName`, `enclosureNumber`, `startDate`, `endDate`, `status`, `stage`, `totalPrice` (BigDecimal), `specialCare`. Статус и стадию пока можно хранить строками. **Запушь сразу** — эти же DTO понадобятся Климу |
| 2 | `api/BookingApi.java` + `api/FakeBookingApi.java` | интерфейс с `List<BookingDto> getAll()`; фейковая реализация отдаёт ~15 броней: разные статусы, стадии, даты, цены, с особым уходом и без |
| 3 | `service/BookingStatistics.java` | методы по списку броней: всего; количество по каждому статусу (`Map`); сейчас в гостинице (`stage = IN_HOTEL`); средняя, минимальная, максимальная стоимость; доля с особым уходом в процентах. **Сделай каждый метод дважды — циклом и через stream**, на защите спросят оба способа |
| 4 | `service/BookingFilter.java` | собирает `Predicate<BookingDto>` из: строки поиска (код, кличка, владелец, без учёта регистра), статуса (или «любой»), периода «с — по» (или пусто). Проверь на фейковом списке: `list.stream().filter(predicate)` |
| 5 | `service/CsvExporter.java` | `void export(List<BookingDto> bookings, Path file)`: заголовок + строка на бронь, разделитель `;`. Экранирование: если в значении есть `;` или кавычка — взять значение в кавычки, а кавычки внутри удвоить. Кодировка UTF-8 |
| 6 | `statistics-view.fxml` + `controller/StatisticsController.java` | вкладка статистики: показатели из шага 3 в `Label` или маленькой таблице |
| 7 | `app.css` | кнопки, таблица, формы, заголовки, вид ошибки |

Шаги 3–5 потом подключаются в главное окно одной-двумя строками: `filteredList.setPredicate(filter.build(...))`,
`exporter.export(sortedList, chosenFile)`. Это сделаем вместе при склейке.

Если этап 1 (песочница JavaFX) ещё не пройден — шаги 1–5 его не требуют, их можно делать параллельно с песочницей.

Статистика и экспорт — хорошая тренировка коллекций и циклов: на защите это спрашивают в первую очередь.

**После КР2 (для КР3):** вход и токены, архив и восстановление, заметки, массовые действия,
импорт CSV, администрирование.

### 3.4. Контракт с backend

**Уже работает** (без авторизации, она появится позже):

| Действие | Запрос | Ответ |
|---|---|---|
| список броней | `GET /api/bookings` | 200, массив `BookingResponse` |
| одна бронь | `GET /api/bookings/{id}` | 200, `BookingResponse`; 404 если нет |
| создать | `POST /api/bookings`, тело `CreateBookingRequest` | 201, `BookingResponse` |
| изменить (только PENDING и ACCEPTED) | `PUT /api/bookings/{id}`, тело `UpdateBookingRequest` | 200, `BookingResponse` с новой ценой и версией; 409 если версия устарела |
| удалить (только PENDING) | `DELETE /api/bookings/{id}` | 204 |
| питомцы | `GET /api/pets`, `GET /api/pets/{id}` | `{ "id", "ownerName", "name", "species", "size" }` |
| вольеры | `GET /api/enclosures`, `GET /api/enclosures/{id}` | `{ "id", "number", "size", "active" }` |
| владельцы | `GET /api/owners`, `GET /api/owners/{id}` | `{ "id", "login", "fullName", "phone" }` |

`UpdateBookingRequest` — полное новое состояние (форма отправляет все поля, питомца менять нельзя):
```json
{ "enclosureId": 3, "startDate": "2026-11-14", "endDate": "2026-11-21",
  "specialCare": false, "comment": "текст или null", "version": 0 }
```
`version` — та, что пришла в `BookingResponse` при загрузке. После успешного `PUT` версия растёт на 1.

`CreateBookingRequest` — что отправляет клиент:
```json
{ "petId": 1, "enclosureId": 3, "startDate": "2026-11-14", "endDate": "2026-11-21",
  "specialCare": true, "comment": "текст до 500 символов" }
```
`specialCare` и `comment` можно не присылать.

`BookingResponse` — что приходит:
```json
{ "id": "0c5db73a-f597-4d6a-b351-26236149ad5f", "code": "BK-000045",
  "petId": 1, "petName": "Рекс", "ownerName": "Анна Смирнова",
  "enclosureId": 3, "enclosureNumber": 103,
  "startDate": "2026-11-14", "endDate": "2026-11-21",
  "status": "PENDING", "stage": "NOT_APPLICABLE",
  "totalPrice": 6720.00, "specialCare": true, "comment": "текст", "tags": [],
  "archived": false, "archivedAt": null,
  "createdAt": "2026-10-25T18:33:14", "updatedAt": "2026-10-25T18:33:14", "version": 0 }
```
- `status`: PENDING, ACCEPTED, DENIED, CANCELLED, COMPLETED;
- `stage` (вычисляется сервером): NOT_APPLICABLE, UPCOMING, IN_HOTEL, FINISHED;
- `version` — **сохранить** и присылать обратно при изменении брони (защита от одновременного редактирования, иначе 409).

**Любая ошибка** приходит в одном формате — показывать пользователю `message`, а при ошибках полей — ещё и `fields`:
```json
{ "status": 400, "message": "Ошибка валидации", "fields": { "startDate": "не должно равняться null" } }
```

**Планируется** — адреса и поля уточняются. Когда эндпоинт готов, он появится в Swagger: `http://localhost:8080/swagger-ui.html`.

| Действие | Запрос |
|---|---|
| вход / обновление токена / выход | `POST /api/auth/login`, `/refresh`, `/logout` |
| текущий пользователь | `GET /api/auth/me` |
| список броней (фильтры, страницы) | `GET /api/bookings?page=0&size=20&status=...` |
| одна бронь | `GET /api/bookings/{id}` |
| создать / изменить | `POST /api/bookings`, `PUT /api/bookings/{id}` |
| в архив / восстановить | `DELETE /api/bookings/{id}`, `POST /api/bookings/{id}/restore` |
| сменить статус | `POST /api/bookings/{id}/accept`, `/deny`, `/cancel`, `/complete` |
| заметки | `GET/POST /api/bookings/{id}/notes` |
| массовые действия | `POST /api/bookings/bulk` |
| CSV | `GET /api/bookings/export`, `POST /api/bookings/import/preview`, `/apply` |
| аналитика | `GET /api/analytics/summary` |
| справочники | `GET /api/enclosures`, `GET /api/pets`, `GET /api/tariffs` |

---

## Правила работы

- **Ветки.** Своя ветка на задачу (`feature/bookings-table`), в `main` — только рабочий код.
- **Коммиты** — маленькие и осмысленные: `feat(desktop): bookings table`.
- **Каждую строку можешь объяснить.** На защите спросят любого из команды по любой части.
- **Ошибки — пользователю.** Никаких пустых `catch`, `System.out` и стектрейсов в окне.
- **Пароли и адреса** — не в коде, а в `application.properties` клиента (адрес API).
