<h1>BeefCore</h1>
Ядро на Spring Boot с поддержкой подгрузки плагинов, главный класс которого является бином, а также по иерархии доступны бины из контекстов выше.

Стандартные файлы для плагина:<p>
plugin.json:
```json
{
  "mainClass": "path.to.mainclass.MainClass",
  "name": "DefaultProject",
  "version": "$version"
}
```
Плагины поддерживают свои конфиги:<p>
config.json:
```json
{
}
```
