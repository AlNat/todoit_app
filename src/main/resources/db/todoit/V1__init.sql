CREATE schema IF NOT EXISTS todoit;

comment on schema todoit is 'Схема для хранения информации по задачам';


------------------------------------------
-- Таблица-справочник по статусам задач --
------------------------------------------

create table IF NOT EXISTS todoit.task_status
(
    id          SERIAL not null primary key,
    name        varchar(50),
    description varchar(255)
);

INSERT INTO todoit.task_status (id, name, description)
VALUES (0, 'PLANNED',  'Запланированная задача'),
       (1, 'DONE',  'Задача выполнена'),
       (2, 'OVERDUE',  'Задача просрочена'),
       (3, 'CANCELED',  'Задача отмена'),
       (4, 'DELETED',  'Задача удалена'),
       (5, 'ARCHIVED',  'Задача архивирована')
ON CONFLICT DO NOTHING;


comment on table todoit.task_status is 'Таблица-справочник статусов задач. Не изменять без изменений приложения!';

comment on column todoit.task_status.id is 'Идентификатор';
comment on column todoit.task_status.name is 'Уникальное имя статуса для идентификации';
comment on column todoit.task_status.description is 'Описание статуса';



----------------------------
-- Таблица хранения задач --
----------------------------

CREATE TABLE IF NOT EXISTS todoit.task
(
    id          bigserial PRIMARY KEY,
    created     timestamp    NOT NULL,
    updated     timestamp    NOT NULL,
    planned     timestamp    NOT NULL,
    title       varchar(255) NOT NULL,
    description TEXT,
    status      integer      NOT NULL REFERENCES todoit.task_status (id),
    color       varchar(7)
);

CREATE INDEX IF NOT EXISTS task_search_idx ON todoit.task(status, planned); -- Под постраничный поисковый запрос
CREATE INDEX IF NOT EXISTS task_status_idx ON todoit.task(status);
CREATE INDEX IF NOT EXISTS task_planned_idx ON todoit.task(planned DESC);
CREATE INDEX IF NOT EXISTS task_created_idx ON todoit.task(created DESC);
CREATE INDEX IF NOT EXISTS task_title_idx ON todoit.task(title);

comment on table todoit.task is 'Таблица для хранения задач';

comment on column todoit.task.created is 'Дата создания';
comment on column todoit.task.updated is 'Последнее изменение';
comment on column todoit.task.planned is 'Дата, на которую задача запланирована';
comment on column todoit.task.title is 'Заголовок задачи';
comment on column todoit.task.description is 'Текст задачи';
comment on column todoit.task.status is 'Статус задачи';
comment on column todoit.task.color is 'Цвет задачи';


--------------------------------
-- Таблица вложений к задачам --
--------------------------------

CREATE TABLE IF NOT EXISTS todoit.attachment
(
    id      bigserial PRIMARY KEY,
    created timestamp NOT NULL,
    data    bytea     NOT NULL,
    size    bigint    NOT NULL,
    name    varchar   NOT NULL,
    task_id bigint    NOT NULL REFERENCES todoit.task (id)
);

CREATE INDEX IF NOT EXISTS attachment_created_idx ON todoit.attachment(created DESC);


comment on table todoit.attachment is 'Таблица вложений';

comment on column todoit.attachment.data is 'Данные вложения';
comment on column todoit.attachment.created is 'Дата создания';
comment on column todoit.attachment.size is 'Размер';
comment on column todoit.attachment.name is 'Оригинальное имя файла';
comment on column todoit.attachment.task_id is 'ID задачи, к которой прикреплено вложение';
