# План: Movie Service + Oscar Service в стиле newdex-backend

## Context

Лабораторная №2: по спекам `docs/movie-service.yaml` и `docs/oscar-service.yaml` нужно реализовать два сервиса.
- **movie-service** — «вызываемый»: JAX-RS, Payara №1.
- **oscar-service** — Spring MVC, Payara №2, вызывает movie-service.

Оба сервиса работают только по HTTPS и должны в точности соответствовать спекам.

Структура модулей уже есть, исходников пока нет: `lib/contract`, `lib/query`, `services/movie-service`, `services/oscar-service`. Целевые версии: Payara 7, Jakarta EE 11, Java 21, Spring Boot 4.1.1.

Образец — newdex-backend:
- слои `api / domain / infrastructure / config`;
- облегчённый CQRS: по бину на операцию — `*Action` (команда) и `*Filter` (запрос) — за фасадом `*Service` без логики;
- доменные модели отдельно от JPA-сущностей, мапперы рукописные.

Портов и адаптеров нет, как и в newdex. Вместо них — строгое направление зависимостей между слоями: `api → domain → infrastructure`. `infrastructure` ничего не знает о `domain`: работает с JPA-сущностями и DTO из `lib/contract`, а перевод в доменную модель делает `domain`. Так зависимости идут в одну сторону, без циклов.

DDD облегчённый:
- агрегат `Movie` с инвариантами задания;
- Value Objects `Coordinates` и `Person`;
- чистый расчёт `RedistributionPlan`.

Без доменных событий, без классов-обёрток для id и без bounded context внутри сервиса.

## Модули `lib/` и направление зависимостей

```
lib/contract           ← ни от чего
lib/query              ← ни от чего (jakarta.persistence-api: compileOnly)
services/movie-service → contract, query (+ jakartaee-api: compileOnly)
services/oscar-service → contract, query (+ Spring Boot)
```

Сервисы друг от друга не зависят. Общие у них только `lib/contract` (shared kernel) и `lib/query`. Всё в `lib/` без Spring и JAX-RS: сервисы на разных фреймворках.

Отдельного `lib/services-common` нет. Обработка ошибок у сервисов на разных фреймворках (JAX-RS `ExceptionMapper` и Spring `@RestControllerAdvice`), поэтому общего кода там почти нет. Исключения и сборка ответов об ошибках живут в каждом сервисе свои. Общий только формат ответа — DTO в `lib/contract/error`.

| Модуль | Пакеты и классы |
|---|---|
| `lib/contract` (`com.itmo.soa.contract`) | `movie`: Movie, MovieInput, MoviePatch, Coordinates, Person, MoviePage, DeletionResult, MovieGenre, MpaaRating, Color, Country. `oscar`: BulkOperationResult, RedistributionResult. `common`: ItemList&lt;T&gt;, PatchField&lt;T&gt; (три состояния поля в PATCH). `error`: ErrorResponse, ValidationErrorResponse, FieldError. Всё на record, без аннотаций |
| `lib/query` (`com.itmo.soa.query`) | QuerySchema/QueryField/FieldType/Operator/Direction, QueryParser (собирает все ошибки сразу: `filter[i]`, `sort[i]`, `page`, `size`), Query (группы OR/AND, сортировка по умолчанию + tie-breaker), Page, EnumSearch, `Filter.format()`/`Sort.format()` для клиентов. `query.jpa.JpaQueryExecutor` выполняет запрос через Criteria API: enum по порядку объявления, LIKE без учёта регистра с экранированием, на null проходят только `isNull` и `ne` |

Правила filter, которые реализует `lib/query` (те, что описывались в спеке):
- одинаковые `field` и `operator` объединяются через OR;
- `isNull` объединяется через OR с `eq` того же поля;
- каждый `ne` — через AND;
- всё остальное — через AND.

## Пакеты внутри сервиса: общий шаблон

```
<root>/
  api/http/...            вход: ресурсы/контроллеры, DTO ⇄ модель, ошибки → HTTP
  domain/<agg>/
    model/                агрегат, VO, доменные исключения
    mapper/               Entity / DTO другого сервиса ⇄ доменная модель
    service/
      <Agg>Service        фасад: только поля Action/Filter (как GamificationConfigService в newdex)
      actions/            <Глагол><Сущ>Action.execute(...)   — команды, в транзакции
      filters/            Get<Сущ>…Filter.execute(...)       — запросы, только чтение
  infrastructure/         БД и клиенты других сервисов; работают с Entity и DTO, о domain не знают
  config/                 бины, свойства
```

**Зависимости:**
```
api ──► domain ──► infrastructure
 │                     ▲
 └─────────────────────┘   (api может использовать исключения infrastructure)
```
- `api` вызывает фасады `domain` и маппит `domain.model` → DTO ответа;
- `domain` напрямую вызывает репозиторий или клиент из `infrastructure`, как Actions в newdex;
- `infrastructure` не импортирует ни `api`, ни `domain`;
- `domain` не импортирует `api`;
- `config` собирает бины и может видеть всё.

Вышележащий слой может обращаться к нижележащим, обратно — никогда.

**Из библиотек:**
- `domain` использует `lib/contract` (enum и входные DTO, как Actions в newdex принимают `*Request`) и `lib/query` (`Query`, `Page`);
- `api` маппит `domain.model` → `contract` через статические `*Mapper`.

## movie-service: JAX-RS + CDI + JPA, `com.itmo.soa.movie`

```
MovieServiceApplication                    @ApplicationPath("/")
api/http/
  movie/MovieResource                      @Path("/movies"): GET list (filter, sort, page, size — строками), POST (201 + Location),
                                           GET/PATCH/DELETE /{id}, DELETE /by-genre/{genre}
  dictionary/DictionaryResource            GET /movies/genres | mpaa-ratings | colors | countries ?search → ItemList
  movie/MovieMapper                        Movie → contract.Movie, Page<Movie> → MoviePage
  json/MoviePatchReader                    MessageBodyReader: JsonObject → MoviePatch (разные «не передано» и null; неизвестные поля, id, creationDate → 400)
  json/JsonbConfigProvider                 ContextResolver<Jsonb>: withNullValues(true) — чтобы в ответе было "genre": null
  exception/                               ExceptionMapper'ы: ValidationException/QueryValidationException → 400, MovieNotFoundException → 404,
                                           битый JSON или неверный тип / нечисловой id → 400 (location path/body), всё остальное → 500;
                                           ErrorResponses — сборка ErrorResponse/ValidationErrorResponse с Instant из Clock,
                                           QueryValidationException → FieldError (location query)
domain/movie/
  model/ValidationException, MovieNotFoundException   доменные исключения (ValidationException хранит List<FieldError>)
  model/Movie                              агрегат: static create(MovieInput, LocalDate), apply(MoviePatch); проверяет все инварианты
                                           и бросает ValidationException со всеми нарушениями (location body, field = путь в JSON)
  model/Coordinates, Person                VO (x, y not null; name не пустой, passportID ≥ 5, nationality not null)
  mapper/MovieEntityMapper                 MovieEntity ⇄ Movie (в domain: infrastructure о Movie не знает)
  service/MovieService                     фасад
  service/actions/  CreateMovieAction, PatchMovieAction, DeleteMovieAction, DeleteMoviesByGenreAction   (@Transactional, CDI @ApplicationScoped)
  service/filters/  GetMovieByIdFilter, GetMoviesPageFilter (QueryParser + MovieQuerySchema), SearchDictionaryFilter (EnumSearch)
  service/MovieQuerySchema                 поля filter/sort из спеки; default creationDate;asc, tie-breaker id;asc, size 20/100
infrastructure/storage/movie/
  model/MovieEntity                        @Entity "movies" + @Embeddable CoordinatesEmbeddable, ScreenwriterEmbeddable (колонки screenwriter_*)
  JpaMovieRepository                       save, findById, delete, deleteByGenre, findPage(Query) → Page<MovieEntity> через JpaQueryExecutor
config/ClockProducer                       @Produces Clock
resources/META-INF/persistence.xml         jta-data-source jdbc/movies (PostgreSQL, ресурс Payara); схема — db/migration/V1__movies.sql, Flyway в @Startup @Singleton
webapp/WEB-INF/payara-web.xml              context-root "/" — пути /movies, как в спеке
```

- `POST` генерирует `id` (через БД) и `creationDate` (`LocalDate.now(clock)`).
- Если в `PATCH` всё валидно, но тело пустое, ответ `400` (`minProperties: 1`).

## oscar-service: Spring Boot 4 MVC WAR, `com.itmo.soa.oscar`

```
OscarServiceApplication                    SpringBootServletInitializer
api/http/oscar/OscarController             POST /oscar/movies/reward-r, POST /oscar/genres/redistribute-rewards/{from-genre}/{to-genre}
api/http/exception/OscarExceptionHandler   @RestControllerAdvice: InvalidGenreException → 400, NoTargetMoviesException → 409,
                                           MovieServiceUnavailableException → 503, всё остальное → 500; формат — DTO из lib/contract/error
domain/oscar/
  model/InvalidGenreException, NoTargetMoviesException   доменные исключения
  model/AwardedMovie                       (id, oscarsCount)
  model/RedistributionPlan                 чистый расчёт: у исходных остаётся по 1, остальное делится поровну, остаток — по возрастанию id;
                                           нет целевых → NoTargetMoviesException
  mapper/AwardedMovieMapper                contract.Movie → AwardedMovie
  service/OscarService                     фасад
  service/actions/RewardRRatedMoviesAction       читает все фильмы рейтинга R → +1 каждому → BulkOperationResult
  service/actions/RedistributeOscarsAction       проверяет жанры (enum, from ≠ to → 400) → план → применение → RedistributionResult
  service/OscarsChangeApplier              применяет изменения и при сбое возвращает уже изменённым фильмам старые значения
infrastructure/integration/movie/MovieServiceClient   клиент на RestClient: findAllByMpaaRating(r), findAllByGenre(g) → List<contract.Movie>,
                                           setOscarsCount(id, n);
                                           GET /movies?filter=…&sort=id;asc&size=100 по всем страницам до totalPages,
                                           фильтр собирается через Filter.format(); PATCH /movies/{id} {"oscarsCount": n};
                                           таймаут, обрыв или 5xx → MovieServiceUnavailableException;
                                           400 → IllegalStateException (500); 404 на PATCH → MovieGoneException
infrastructure/integration/movie/MovieServiceUnavailableException, MovieGoneException   исключения клиента
config/http/MovieServiceIntegrationConfig  RestClient: base-url, таймауты, SSL bundle (truststore с сертификатом Payara №1)
config/properties/MovieServiceProperties   @ConfigurationProperties("movie-service")
resources/application.yaml                 movie-service.base-url, ssl bundle
webapp/WEB-INF/payara-web.xml, beans.xml   context-root "/", class-loader delegate=false, CDI discovery none
```

Как обрабатывается `404` на `PATCH`:
- в `reward-r` фильм пропускается и не учитывается в счётчике;
- в `redistribute` все изменения откатываются, ответ `500`.

## Кто кого вызывает

- **Фронтенд → movie-service:** все 10 операций `/movies...`.
- **Фронтенд → oscar-service:** 2 операции `/oscar...`.
- **oscar-service → movie-service:** только `GET /movies` с фильтром и `PATCH /movies/{id}`.
- **movie-service** никого не вызывает.

## Порядок реализации

1. `lib/contract`.
2. `lib/query`.
3. movie-service: домен → JPA-репозиторий → actions/filters → ресурсы, reader, мапперы ошибок → persistence.xml, миграция.
4. oscar-service: `RedistributionPlan` → `MovieServiceClient` → actions с откатом → контроллер и обработчик ошибок.
5. Сборка: в `build.gradle.kts` добавить зависимости через `gradle/libs.versions.toml`: `lib/query` — jakarta.persistence-api (compileOnly), movie-service — flyway.

Тесты не пишем.

Развёртывание на helios (два домена Payara с разными портами, только HTTPS, truststore) и фронтенд — следующий этап, в этот план не входит.

## Verification

- `./gradlew build` — все модули компилируются, WAR-файлы собираются.
- Ручная проверка на локальной Payara 7 (два домена, PostgreSQL): через `curl -k` пройти примеры из обеих спек — `201` + `Location`; `400` со всеми `errors[]` (фильтры, тело, `id`); `404`, `204`; справочники `{items}`; `reward-r` и `redistribute` с проверкой результата через `GET /movies`; остановить movie-service → oscar-service отвечает `503`.
