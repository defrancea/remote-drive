### Maven build

Windows packaging:
```
mvn clean package -P pkg-windows
```

Test bootstrap (Windows):
```
mvn clean test -P run, windows-dependencies
```