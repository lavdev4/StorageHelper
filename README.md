# Storage Helper
📦📝🗄️🗃️ Отчетность расхода складского инвентаря

<details>
  <summary><b>🏞️Скриншоты</b></summary>
    <p align="center">
      <img width="20%" height="20%" src="https://github.com/lavdev4/StorageHelper/assets/103329075/44c4d06d-a8e0-4b2a-87da-4ce7097a2c0b">
    </p>
    <p align="center">
      <img width="20%" height="20%" src="https://github.com/lavdev4/StorageHelper/assets/103329075/c2f48a69-8995-4e4a-b738-c1c61c8444bb">
    </p>
    <p align="center">
      <img width="20%" height="20%" src="https://github.com/lavdev4/StorageHelper/assets/103329075/392e6c40-46ea-4c51-9f8d-8529155d8819">
    </p>
    <p align="center">
      <img width="20%" height="20%" src="https://github.com/lavdev4/StorageHelper/assets/103329075/b45cfb72-459a-4665-b84d-ae109b7be6d3">
    </p>
    <p align="center">
      <img width="20%" height="20%" src="https://github.com/lavdev4/StorageHelper/assets/103329075/5f3c520a-9681-4b62-a597-4f499d2c8021">
    </p>
    <p align="center">
      <img width="20%" height="20%" src="https://github.com/lavdev4/StorageHelper/assets/103329075/476b8c2c-8687-4ac6-b7ac-e9b6c5e15bab">
    </p>
</details>

## Описание: 
**Работа выполнена в процессе первого изучения Android SDK как экспериментальный учебный проект.** Приложение представляет из себя помощник учета расхода какого-либо материала из условного хранилища. 

Имеются 3 экрана: 
1) Экран ввода данных. Тут выбираются материалы, которые должны быть списаны из хранилища, и их количество. Выбранные материалы должны быть определены в группу. Группой может быть подразделение, к которому приписаны данные материалы. К примеру, это может быть отдел магазина, в котором данные материалы были проданы. Когда список готов, производится списание материала с предварительным указанием даты списания. После этого данные о списании появляются на экране "Отчет" а также списываются со склада. 
2) Экран отчета. Тут можно посмотреть всю историю расхода материала по датам с разбиением на группы. 
3) Экран склада. Тут находятся все материалы, с которыми мы ведем работу. Если на экране "Склад" ничего нет, то на экране "Внести" также ничего не будет. Галочка "Учитывать наличие на складе" во включенном состоянии ограничивает возможность списать больше материала, чем есть на "складе".

## Использованные инструменты:
Room, RxJava, Navigation achitecture component, Bottom navigation, Android Spannable