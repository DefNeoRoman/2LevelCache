# 2LevelCache
2LevelCache Realization

TestTask
"Create a configurable two-level cache (for caching Objects).  
Level 1 is memory, level 2 is filesystem. 
Config params should let one specify the cache strategies and max sizes of level  1 and 2." 


Разбор по строкам:

"Create a configurable two-level cache (for caching Objects).  
Level 1 is memory, level 2 is filesystem. 
(это условие выполняется через hsqldb стратегия кеширования там уже настроена)

Config params should let one specify 
the cache strategies and max sizes of level  1 and 2." 
(это можно сделать в настройках hsqldb и Spring Boot)

Так как условие задачи размыто, я решил взять Spring Boot(Так как он обладает большим количестовм настроек)
с базой данных HSQLDB(так как параметры там можно настроить)

Это приложение представляет собой хранилище объектов SimpleData
я могу обращаться по id и получать нужный мне объект из хранилища

могу выполнять стандартные crud операции (реализовано через Spring Data Jpa)

Архив можно скачать c GitHub или через maven clean install можно получить jar файл
