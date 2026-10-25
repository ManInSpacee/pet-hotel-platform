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

### 3.3. Экраны — в этом порядке

Первые четыре — обязательный минимум. Дальше — по мере готовности backend.

1. **Бронирования** — `TableView`: код, питомец, владелец, вольер, даты, статус, стоимость, особый уход. Кнопка «Обновить».
2. **Создание и редактирование брони** — модальная форма: питомец и вольер из `ComboBox` (id руками не вводить), два `DatePicker`, флажок «особый уход», комментарий, теги. Ошибки backend — в `Dialogs.showError`.
3. **Архив вместо удаления** — «В архив» с подтверждением, «Восстановить», фильтр «показывать архивные».
4. **Поиск, фильтры, сортировка** — поиск по коду/кличке/владельцу; фильтры по статусу, вольеру, особому уходу, тегу, цене «от/до»; сортировка кликом по колонкам.
5. **Вход** — окно логина до главного окна; хранить access- и refresh-токены; при 401 — попытка обновить токен, не вышло — снова логин. Смена временного пароля при первом входе.
6. **Заметки к брони** — список, добавить, изменить, удалить, закрепить.
7. **Аналитика** — вкладка с показателями: всего, активные, архивные, с особым уходом, средняя стоимость, разбивка по статусам и вольерам.
8. **Массовые действия** — выбор нескольких строк, «в архив», «сменить статус», «добавить/снять тег».
9. **CSV** — экспорт текущего списка через `FileChooser` (путь выбирает пользователь), импорт с предпросмотром.
10. **Администрирование** (только для роли ADMIN) — пользователи, тарифы, вольеры, журнал аудита.
11. **CSS** — оформление кнопок, таблиц, форм в `app.css`.

### 3.4. Контракт с backend (черновик, уточняется)

Адреса и поля DTO договариваем с Климом. Когда эндпоинт готов — он появится в Swagger: `http://localhost:8080/swagger-ui.html`.

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
