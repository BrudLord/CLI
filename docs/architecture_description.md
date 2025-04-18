# Описание компонентов CLI приложения


## 1. Главный модуль: CLI
Класс `CLI` является точкой входа в приложение. Он реализует цикл **Read-Execute-Print Loop (REPL)**:
- Считывает ввод пользователя.
- Передает строку модулю `InputParser` для преобразования в последовательность команд.
- Исполняет команды через `Executor`.
- Обрабатывает исключения, предотвращая аварийное завершение (кроме команды `exit`).
- Все команды выполняются последовательно в рамках одного потока.
---

## 2. Парсинг ввода: InputParser
Класс преобразует сырую строку ввода в структурированные команды:
- Создает объекты команд (наследники `Command`) с использованием токенов (определяет тип команды по первому токену и создает объект команды через метод createCommand()).
- InputParser получает на вход сырую строку, дальше поочереди вызывает Quote Parser, Substitutor и Pipe Parser. Как результат он получает блоки, каждый блок содержит команду и блоки соединяются друг с другом через PIPE. Далее он перебирает конструкторы имеющихся команд (в порядке указанном в 5 пункте), пока не получит информации об успехе (В любом случае это произойдет при UnknownCommand)


---

## 3. Вспомогательные парсеры
1. **Quote Parser**:
   - Токенизирует подкоманды с учетом пробелов и кавычек (`"`, `'`). Таким образом в результате работы данного парсера из исходных данных будут выделены в отдельные сущности все строки, заключенные в кавычки.
2. **Substitutor**:
   - Заменяет переменные окружения (например, `$PATH`) на их значения из `Context` для оператора `$`.
3. **Pipe Parser**:
   - Разделяет команды по оператору `|`.
---

## 4. Исполнение команд: Executor
Управляет выполнением команд:
- Обновляет переменные окружения при необходимости (оператор `=`).
-  Последовательно запускает команды, связывая их потоки:
   - Первая команда получает стандартный ввод (или данные из файла).
   - Каждая следующая команда читает из output предыдущей через input поток.
   - Результат последней команды передается в стандартный вывод или файл.

---

## 5. Интерфейс команд: Command
**Структура команд**:
- **Поля**:
  - `args`: аргументы команды (например, `["file.txt"]` для `cat`).
  - `input`/`output`: потоки ввода/вывода.
- **Методы**:
  - `execute()`: выполняет логику команды.
  - `setInputStream()`/`setOutputStream()`: настраивает потоки.

**Наследники Command**:
- `cat [FILE]` — вывести на экран содержимое файла
- `echo` — вывести на экран свой аргумент (или аргументы)
- `wc [FILE]` — вывести количество строк, слов и байт в файле
- `pwd` — распечатать текущую директорию
- `exit` — выйти из интерпретатора
- `assignment` - присваивание значения переменной окружения
- `unknownCommand` - для внешних команд 


---

## 6. Context
Хранит переменные окружения в словаре `envVars`:
- **Методы**:
  - `getVar(x)`: возвращает значение переменной `x` (или пустую строку).
  - `setVar(x, val)`: присваивает значение `val` переменной `x` (создает при отсутствии).

---

## 7. Вспомогательные компоненты
- **Token**:
  - Хранит тип (строка, переменная) и значение.
  - Используется `Quote Parser`, `InputParser` и `Substitutor` для промежуточной обработки.

---

## Взаимодействие компонентов (пример)
1. Пользователь вводит `echo $PATH`.
2. `CLI` передает строку в `InputParser`.
3. `InputParser`:
   - Разбивает строку на токены.
   - Заменяет `$PATH` на значение из `Context.envVars`.
4. Создается объект `EchoCommand` и передается в `Executor`.
5. `Executor` выполняет команду и выводит результаы на экран.


