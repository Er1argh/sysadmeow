# SysAdMeow 🐾

**SysAdMeow** — учебный Android-маркетплейс разовых услуг удалённой настройки
сетевого ПО. Исполнители представлены как sysadmin-коты.

## Возможности

- регистрация и вход через Firebase Authentication (Email/Password);
- роли `CLIENT` и `ADMIN` (sysadmin-кот);
- каталог опубликованных специалистов;
- фильтрация по категориям VPN, MikroTik, Cisco, Linux и Security;
- профиль пользователя;
- создание, редактирование, сохранение черновика и публикация карточки
  sysadmin-кота;
- корзина с защитой от повторного добавления одного специалиста;
- оплата через beGateway / bePaid Android SDK;
- создание оплаченного заказа в Cloud Firestore;
- локальное уведомление об успешном оформлении заказа;
- отзывы и оценки 1–5;
- запрет отзыва самого себя и повторного отзыва;
- агрегированный рейтинг с обновлением в Firestore transaction;
- постраничная загрузка отзывов;
- Firestore Security Rules.

## Технологии

- Kotlin
- Android Views + XML
- MVVM
- StateFlow
- RecyclerView / CardView
- Firebase Authentication
- Cloud Firestore
- beGateway Android SDK 2.6.0
- JDK 17
- Android Gradle Plugin 8.8.2
- Kotlin 2.3.0

Package / applicationId:

`com.example.netadminmarketplace`

## Структура

```text
app/
  src/main/java/com/example/netadminmarketplace/
    model/
    data/
    util/
    notification/
    ui/
      auth/
      main/
      detail/
      cart/
      checkout/
      payment/
      profile/
  src/main/res/
firestore.rules
build.gradle.kts
settings.gradle.kts
gradlew
gradlew.bat
```

## Запуск проекта

### 1. Клонировать репозиторий

```bash
git clone https://github.com/Er1argh/sysadmeow.git
cd sysadmeow
```

### 2. Создать Firebase-проект

Создайте Android-приложение в Firebase Console с package name:

`com.example.netadminmarketplace`

Включите:

- Authentication → Email/Password
- Cloud Firestore

### 3. Добавить Firebase-конфигурацию

Скачайте `google-services.json` из Firebase Console и положите:

```text
app/google-services.json
```

Этот файл специально не хранится в публичном репозитории.

### 4. Применить правила Firestore

Используйте файл:

```text
firestore.rules
```

Не заменяйте правила временным `allow read, write: if true`.

### 5. Открыть в Android Studio

Откройте корень репозитория и дождитесь Gradle Sync.

### 6. Запустить

Выберите эмулятор или Android-устройство с Android 7.0 (API 24) или выше.

## Тестовый сценарий

Создайте два Firebase-аккаунта:

1. `ADMIN` — заполнить и опубликовать карточку специалиста.
2. `CLIENT` — открыть каталог, добавить специалиста в корзину, пройти тестовую
   оплату и оставить отзыв.

## Оплата

Проект использует учебный test-flow beGateway / bePaid.

Важное замечание:

- в репозитории не должны находиться merchant secrets, приватные ключи или
  production credentials;
- production checkout token и серверные операции должны выполняться на backend;
- текущая версия предназначена для учебной работы.

## Безопасность

Firestore Security Rules запрещают, в частности:

- чтение чужого приватного профиля;
- самостоятельное изменение роли;
- публикацию карточки пользователем `CLIENT`;
- ручное изменение рейтинга владельцем карточки;
- отзыв самому себе;
- повторный отзыв;
- рейтинг вне диапазона 1–5;
- создание заказа от чужого UID.

## Документация

Подробное описание архитектуры, модели данных, сценариев, оплаты, отзывов и
диаграмм приведено в сопровождающей документации проекта.
