# Deportes UNAL

Proyecto del curso Estructuras de Datos (2026-2), Universidad Nacional de Colombia, Sede Bogotá.

## Descripción

Sistema para registrar estudiantes con los deportes que practican y los que les interesan. Con esos datos, el sistema agrupa en comunidades a los estudiantes que comparten al menos un deporte, indica si un estudiante está conectado, directa o indirectamente, con alguien que practica uno de los deportes que le interesan (o que esa conexión no existe), da acceso a los datos de un estudiante por su ID, permite eliminarlo y cuenta cuántos estudiantes practican cada deporte, mostrando los deportes en orden.

## Integrantes

- Juan Diego Cuartas Casas
- Laura Juliana Espinosa Muñoz
- Marco Antonio García Villamil
- Nicolás Hernández Cardona
- Santiago Neira Lamadrid
- Juan Pablo Sánchez Ibáñez

## Lenguaje

Java 17 o superior. No se usa Maven ni Gradle; basta con el JDK.

## Instalación

```bash
git clone https://github.com/laurajespm/Proyecto-ED.git
cd Proyecto-ED
javac -encoding UTF-8 -d out $(find src -name "*.java")
```

En Windows, con PowerShell, el último comando es:

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
```

## Estado en la Entrega 1

Ya funcionan la lista doblemente enlazada (`DLL`) y la cola con arreglo circular (`Queue`), que crece cuando se llena. El árbol AVL y el sistema (`SistemaDeportes`) están como plantillas: tienen los atributos y un método por cada requisito funcional del reporte, con un comentario que explica qué estructura va a usar y cuánto debe costar. La lógica se implementa en la Entrega 2.

## Estructura del proyecto

```
Proyecto-ED/
├── README.md
└── src/
    ├── estructuras/
    │   ├── DLL.java              lista doblemente enlazada
    │   ├── DLLNode.java          nodo de la lista
    │   ├── MyQueue.java          interfaz de cola
    │   ├── Queue.java            cola con arreglo circular
    │   └── ArbolAVL.java         árbol AVL de clave y valor (plantilla)
    └── deportes/
        ├── Estudiante.java       ID, nombre, deportes que practica y de interés
        ├── Deporte.java          nombre y lista de practicantes
        ├── ClaveRanking.java     orden de los deportes por número de practicantes
        └── SistemaDeportes.java  un método por requisito funcional (plantilla)
```
