# Manual de uso: Deportes UNAL

Este manual explica cómo usar el programa de consola opción por opción. Los ejemplos usan los 16 estudiantes de `datos/estudiantes.txt`.

## Compilar y abrir el programa

Desde la carpeta del proyecto:

```bash
javac -encoding UTF-8 -d out $(find src test -name "*.java")
java -cp out app.Main datos/estudiantes.txt
```

El archivo es opcional. Si se pasa, el programa lo carga al iniciar y muestra cuántos estudiantes leyó:

```
16 estudiantes cargados desde datos/estudiantes.txt
```

Luego aparece el menú. La primera línea dice cuántos estudiantes y deportes hay registrados en ese momento:

```
=== Deportes UNAL (16 estudiantes, 11 deportes) ===
1. Registrar un estudiante (RF1)
2. Consultar un estudiante por ID (RF2)
3. Eliminar un estudiante (RF3)
4. Ver las comunidades deportivas (RF4)
5. Buscar conexión con un deporte de interés (RF5 y RF6)
6. Ver los deportes ordenados por cantidad de practicantes (RF7)
7. Cargar estudiantes desde un archivo
8. Listar todos los estudiantes
9. Ver los practicantes de un deporte
10. Dibujar el árbol AVL de estudiantes
0. Salir
```

Se escribe el número de la opción y se presiona Enter. Si se escribe algo que no es un número, el programa lo vuelve a pedir.

## 1. Registrar un estudiante (RF1)

Pide el ID, el nombre, los deportes que practica y los que le interesan. Las listas de deportes van separadas por coma y pueden quedar vacías.

```
ID: 2001
Nombre: Prueba Uno
Deportes que practica (separados por coma): Fútbol, Ajedrez
Deportes que le interesan (separados por coma): Natación
Registrado: Prueba Uno (ID 2001) | practica: [Fútbol, Ajedrez] | le interesan: [Natación]
```

Los nombres de los deportes se comparan sin tildes, mayúsculas ni espacios de más: "Fútbol", "futbol" y " FUTBOL " son el mismo deporte. Si el ID ya existe, el programa muestra un error y no registra a nadie. Un deporte que el estudiante ya practica no se guarda como interés.

## 2. Consultar un estudiante por ID (RF2)

```
ID: 1001
Ana Torres (ID 1001) | practica: [Fútbol, Atletismo] | le interesan: [Natación]
```

Si el ID no existe, responde "No existe un estudiante con ese ID."

## 3. Eliminar un estudiante (RF3)

```
ID: 2001
Estudiante eliminado.
```

El estudiante sale también de la lista de practicantes de cada deporte, así que las comunidades, las conexiones y el conteo de practicantes se actualizan.

## 4. Ver las comunidades deportivas (RF4)

Una comunidad es un grupo de estudiantes unidos, directa o indirectamente, por deportes que practican en común. Quien no comparte deportes con nadie no forma comunidad.

```
Comunidad 1 (8 estudiantes): Ana Torres, Luis Peña, Marta Díaz, Carlos Rojas, Sofía Gómez, Andrés Castillo, Valentina Ruiz, Natalia Cruz
Comunidad 2 (3 estudiantes): Julián Moreno, Camila Herrera, Mateo Vargas
Comunidad 3 (3 estudiantes): Laura Ortiz, Daniel Suárez, Paula Jiménez
```

## 5. Buscar conexión con un deporte de interés (RF5 y RF6)

Indica si el estudiante está conectado con alguien que practica uno de los deportes que le interesan, a través de quién y con cuántos intermediarios. Si hay varias conexiones, muestra la de menos intermediarios. Entre corchetes va el deporte que comparte cada pareja del camino.

Conexión indirecta:

```
ID del estudiante: 1001
Ana Torres puede contactar a Marta Díaz (ID 1003), que practica Natación. Conexión indirecta, con 1 intermediario: Ana Torres -[Fútbol]- Luis Peña -[Rugby]- Marta Díaz
```

Conexión directa:

```
ID del estudiante: 1012
Daniel Suárez puede contactar a Laura Ortiz (ID 1011), que practica Ajedrez. Conexión directa: Daniel Suárez -[Tenis de mesa]- Laura Ortiz
```

Cuando no existe la conexión (RF6):

```
ID del estudiante: 1004
No existe conexión para Carlos Rojas: ningún estudiante conectado con Carlos Rojas practica [Taekwondo].
```

Si el estudiante no tiene deportes de interés, el programa también lo indica.

## 6. Ver los deportes ordenados por cantidad de practicantes (RF7)

```
1. Natación: 3 estudiantes
2. Ajedrez: 2 estudiantes
3. Atletismo: 2 estudiantes
...
11. Ultimate: 1 estudiante
```

Los deportes con la misma cantidad de practicantes salen en orden alfabético.

## 7. Cargar estudiantes desde un archivo

Pide la ruta de un archivo de texto en UTF-8 con una línea por estudiante:

```
id;nombre;deportes que practica;deportes que le interesan
1001;Ana Torres;Fútbol,Atletismo;Natación
1016;Felipe Ramírez;;Baloncesto
```

Las líneas vacías y las que empiezan con `#` se ignoran. Si una línea tiene un error (ID repetido, ID que no es número o un campo de menos), el programa dice el número de la línea y sigue con las demás. Si el archivo no existe:

```
Ruta del archivo: no-existe.txt
No se pudo leer el archivo: no-existe.txt (No such file or directory)
0 estudiantes cargados desde no-existe.txt
```

## 8. Listar todos los estudiantes

Muestra a todos los estudiantes ordenados por ID, con sus deportes e intereses:

```
Ana Torres (ID 1001) | practica: [Fútbol, Atletismo] | le interesan: [Natación]
Luis Peña (ID 1002) | practica: [Fútbol, Rugby] | le interesan: [Voleibol]
...
```

## 9. Ver los practicantes de un deporte

```
Deporte: futbol
2 practicantes: Ana Torres, Luis Peña
```

## 10. Dibujar el árbol AVL de estudiantes

Muestra el árbol de lado: la raíz queda a la izquierda, el subárbol derecho arriba y entre corchetes va la altura de cada nodo. Sirve para ver que el árbol está balanceado. Con más de 64 estudiantes solo se muestra la altura.

## 0. Salir

Termina el programa.

## Pruebas

Además del programa, el proyecto tiene cuatro clases de prueba. Cada una imprime PASÓ o FALLÓ por caso:

```bash
java -cp out pruebas.PruebasSistema
java -cp out pruebas.PruebasNormalizacion
java -cp out pruebas.PruebasComunidades
java -cp out deportes.PruebasRF2RF7
```

- `PruebasSistema`: las estructuras (lista, cola y AVL) y los requisitos RF1 a RF7.
- `PruebasNormalizacion`: que un deporte escrito de formas distintas se reconozca como uno solo y el orden del ranking.
- `PruebasComunidades`: cuándo se forma una comunidad, cómo se unen dos grupos y cómo se separan al eliminar a un estudiante.
- `PruebasRF2RF7`: la consulta por ID y el ranking por separado.
