# Storage Helper
📦📝🗄️🗃️ Отчетность расхода складского инвентаря

<details>
  <summary><b>🏞️Скриншоты</b></summary>
    <p align="center">
      <img width="30%" height="30%" src="https://github.com/lavdev4/StorageHelper/assets/103329075/ee901853-9e3d-4f98-ace0-f03b63d0fc39">
    </p>
    <p align="center">
      <img width="30%" height="30%" src="https://github.com/lavdev4/StorageHelper/assets/103329075/08666e69-7a2c-4694-8a65-762a77a6de26">
    </p>
    <p align="center">
      <img width="30%" height="30%" src="https://github.com/lavdev4/StorageHelper/assets/103329075/77228b02-7b88-4f0f-8c42-a9a5e7b31de7">
    </p>
    <p align="center">
      <img width="30%" height="30%" src="https://github.com/lavdev4/StorageHelper/assets/103329075/92f72c9d-d0a4-4c94-8367-a8298c4524c0">
    </p>
    <p align="center">
      <img width="30%" height="30%" src="https://github.com/lavdev4/StorageHelper/assets/103329075/e227ccfb-dfc5-412b-9451-b6d3f50e5cde">
    </p>
    <p align="center">
      <img width="30%" height="30%" src="https://github.com/lavdev4/StorageHelper/assets/103329075/c68df2a3-d028-4f92-bd47-2f1dc9791f8b">
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
