# Deportes UNAL: comunidades deportivas y conexiones entre estudiantes

Proyecto de clase de **Estructuras de Datos (2016699), 2026-2**  
Universidad Nacional de Colombia, Sede Bogotá, Facultad de Ingeniería

**Autores:** 
- Juan Diego Cuartas Casas 
- Laura Juliana Espinosa Muñoz
- Marco Antonio García Villamil
- Nicolás Hernández Cardona
- Santiago Neira Lamadrid
- Juan Pablo Sánchez Ibáñez.

---

## Descripción

La Universidad Nacional ofrece a sus estudiantes una gran variedad de deportes (convocatorias temporales, cursos intersemestrales, equipos representativos). El reto es que un estudiante encuentre a quién acudir para aprender un deporte que le interesa, aunque solo llegue a esa persona a través de compañeros con los que comparte otros deportes.

Este sistema agrupa a los estudiantes en **comunidades** según los deportes que practican y le indica a cada uno si está **conectado, directa o indirectamente**, con alguien que practica un deporte de su interés.

**Ejemplo:** si Ana y Luis juegan fútbol, y Luis y Marta juegan rugby, Ana está conectada con Marta a través de Luis. Si a Ana le interesa la natación y Marta la practica, Ana ya tiene a quién acudir.

## Datos de cada estudiante

- **ID:** identificador único.
- **Nombre.**
- **Deportes que practica.**
- **Deportes que le interesan.**
  
La clase `Estudiante` guarda además edad, teléfono, correo, facultad y carrera, y lleva una cola de solicitudes pendientes a deportes.

Dos estudiantes están conectados **directamente** si practican un mismo deporte, e **indirectamente** si hay una cadena de estudiantes que los une compartiendo deportes.

## Producto mínimo viable (MVP)

Programa de consola. Los estudiantes se registran desde el menú o se cargan desde un archivo de texto. La interfaz gráfica no hizo parte del alcance.

| RF  | Requisito |
|-----|-----------|
| RF1 | Registrar un estudiante con su ID, su nombre, los deportes que practica y los que le interesan. |
| RF2 | Consultar los datos de un estudiante por su ID. |
| RF3 | Eliminar un estudiante. |
| RF4 | Agrupar en comunidades a los estudiantes que comparten al menos un deporte. |
| RF5 | Indicar si un estudiante está conectado, directa o indirectamente, con alguien que practica uno de sus deportes de interés, y a través de quién. |
| RF6 | Indicar cuándo esa conexión no existe. |
| RF7 | Contar los estudiantes de cada deporte y listar los deportes de más a menos practicantes. |

### Decisiones de diseño

- Una **comunidad** es un grupo de estudiantes unidos por cadenas de deportes compartidos; quien no comparte deportes con nadie no forma comunidad.
- Si hay varias conexiones posibles, se informa la de **menos intermediarios**.
- Un deporte que el estudiante ya practica no se cuenta entre sus intereses.
- Los nombres de los deportes se comparan **sin tildes ni mayúsculas**.
- Los deportes con la misma cantidad de practicantes se ordenan **alfabéticamente**.

## Diseño del sistema

La clase `SistemaDeportes` atiende los requisitos con tres árboles AVL y con las listas que unen estudiantes y deportes. Esas listas forman un **grafo bipartito implícito**, explicado con más detalle en el informe. Una cola permite recorrer ese grafo por anchura para obtener las comunidades y las conexiones.

Los deportes se clasifican en tres tipos mediante subclases de `Deporte` (intersemestral, representativo y recreativo). Los datos del curso (cupos, horario, entrenador) y los propios de cada tipo están en el código como base para entregas posteriores; los requisitos RF1 a RF7 no los usan.

### Estructuras de datos elegidas

En los costos, *n* es la cantidad de estudiantes, *d* la de deportes y *m* la de parejas (estudiante, deporte que practica).

| Estructura | Uso | Requisitos | Costo |
|------------|-----|------------|-------|
| **AVL de estudiantes** (clave: ID) | Registrar, consultar y eliminar por ID | RF1, RF2, RF3 | O(log n)  |
| **AVL de cada comunidad** (clave: ID) | Guardar a los miembros de una comunidad ordenados por ID | RF4 | O(log n) por miembro |
| **AVL de deportes** (clave: nombre normalizado) | Buscar o crear cada deporte al registrar | RF1 | O(log d) |
| **Listas doblemente enlazadas (DLL)** | Relación estudiante–deporte: cada estudiante tiene una DLL de deportes que practica y otra de los que le interesan; cada deporte tiene una DLL de practicantes | RF1, RF3, RF4, RF5 | Quitar un estudiante de la DLL de un deporte a partir de su referencia: O(1) |
| **Cola** | Recorrido por anchura del grafo bipartito | RF4, RF5, RF6 | O(V + E), con V = n + d y E = m; las comunidades cuestan O(n log n + m) porque cada miembro se inserta en el AVL de su comunidad |
| **AVL del ranking** (clave: número de practicantes, nombre) | Listar deportes de más a menos practicantes con un recorrido inorden | RF7 | Listar: O(d); actualizar un deporte: O(log d) |

La **cola** también se usa dentro de las clases: cada `Deporte` tiene una cola de solicitudes y otra de espera de inscripción, y cada `Estudiante` una de solicitudes pendientes. Es un arreglo circular que duplica su tamaño al llenarse, así que el número con que se crea es el tamaño inicial y no un límite.

**Justificación resumida**

- **AVL:** busca, inserta y elimina en O(log n) en el peor caso. Una lista costaría O(n) por búsqueda; un arreglo ordenado inserta y elimina en O(n); un árbol binario de búsqueda sin balancear se degenera en lista si los ID llegan en orden. Una tabla hash daría O(1) en promedio, pero es un tema posterior del curso y se evaluará en la Entrega 3.
- **DLL:** cada estudiante guarda el nodo que ocupa en la DLL de cada deporte que practica, por lo que al eliminarlo se quita en O(1). Además hacen de listas de adyacencia, así que no hace falta un grafo aparte: unir a los estudiantes entre sí exigiría k(k−1)/2 aristas por deporte con k practicantes, en lugar de k.  Cada nodo recuerda a qué lista pertenece, de modo que borrar un nodo que ya fue borrado o que es de otra lista lanza una excepción en lugar de dañar la lista, y esa comprobación sigue siendo O(1).
- **Cola:** el recorrido por anchura visita a los estudiantes por niveles, así que el primero encontrado que practica un deporte de interés es el de menos intermediarios. Si la cola se vacía sin encontrarlo, la conexión no existe. Una pila daría un recorrido en profundidad y no aseguraría el camino más corto. Para saber si un estudiante o un deporte ya se visitó no se usan conjuntos de `java.util`: cada uno guarda el número del último recorrido que pasó por él, y comparar ese número cuesta O(1).
- **AVL del ranking:** al cambiar la cantidad de practicantes, el deporte se saca y se vuelve a insertar en O(log d). Un heap daría el deporte con más practicantes en O(1), pero listarlos todos en orden costaría O(d log d) en cada consulta.

## Flujo general

1. Al iniciar, se pueden cargar los estudiantes desde un archivo de texto. Las líneas con error se informan y se saltan.
2. El menú de consola atiende cada opción hasta que se elige salir (opción 0):
   1. **Registrar (RF1):** consulta el AVL de estudiantes para rechazar un ID repetido, busca o crea cada deporte en el AVL de deportes, agrega al estudiante a las DLL correspondientes, actualiza el AVL del ranking e inserta al estudiante.
   2. **Consultar (RF2):** busca en el AVL de estudiantes y muestra sus datos, o avisa que no existe.
   3. **Eliminar (RF3):** elimina del AVL de estudiantes, lo quita de la DLL de cada deporte que practicaba y actualiza el ranking.
   4. **Comunidades (RF4):** recorre a los estudiantes en orden de ID; desde cada uno sin visitar hace un recorrido por anchura con la cola. Un grupo de dos o más estudiantes es una comunidad.
   5. **Conexión (RF5 y RF6):** encola al estudiante que hace la consulta y recorre por niveles; cada estudiante se revisa al descubrirlo, y el recorrido termina con el primero que practique un deporte de interés (conexión encontrada, con el camino) o cuando la cola se vacía (no hay conexión).
   6. **Ranking (RF7):** recorrido inorden del AVL del ranking.
   7. Además: cargar un archivo, listar a todos los estudiantes, ver los practicantes de un deporte y dibujar el AVL de estudiantes (para pocos datos).


## Estructura del repositorio

```text
Proyecto-ED/
├── README.md
├── datos/
│   └── estudiantes.txt              16 estudiantes de ejemplo
├── resultados/
│   ├── graficar.py                  dibuja las gráficas a partir de tiempos.csv
│   ├── tiempos.csv                  mediciones del Analizador
│   └── tiempos_*.png / *.csv        gráficas, tabla y razones de crecimiento
├── src/
│   ├── estructuras/
│   │   ├── DLL.java                 lista doblemente enlazada (iterable)
│   │   ├── DLLNode.java             nodo de la lista
│   │   ├── MyQueue.java             interfaz de cola
│   │   ├── Queue.java               cola con arreglo circular que crece
│   │   └── ArbolAVL.java            árbol AVL de clave y valor
│   ├── deportes/
│   │   ├── SistemaDeportes.java     un método por requisito funcional (RF1 a RF7)
│   │   ├── Estudiante.java          ID, nombre, datos de contacto, deportes que practica y de interés
│   │   ├── Deporte.java             nombre, tipo, horario, cupos, entrenador, practicantes y colas
│   │   ├── CursoIntersemestral.java, DeporteRepresentativo.java, Recreativo.java   tipos de deporte
│   │   ├── Comunidad.java           grupo de estudiantes que comparten deportes (AVL por ID)
│   │   ├── ClaveRanking.java        orden de los deportes por número de practicantes
│   │   ├── NormalizadorNombre.java  compara nombres sin tildes, mayúsculas ni espacios de más
│   │   └── Solicitud.java           solicitud de un estudiante a un deporte
│   ├── app/
│   │   └── Main.java                menú de consola
│   └── analisis/
│       ├── GeneradorDatos.java      datos de prueba al azar con semilla
│       └── Analizador.java          mide los tiempos de cada operación
└── test/
    ├── deportes/PruebasRF2RF7.java  pruebas de RF2 y RF7
    └── pruebas/PruebasSistema.java  pruebas de las estructuras y de RF1 a RF7 (52 casos)
```

Las estructuras de datos están implementadas desde cero; no se usan las colecciones de `java.util`. El código del sistema solo usa de Java el `Scanner` y la lectura de archivos del menú, y `java.text.Normalizer` para quitar las tildes. Las pruebas y el analizador usan `Random` para generar datos.

## Requisitos, compilación y ejecución

Java 17 o superior; no se usa Maven ni Gradle, basta con el JDK. Las gráficas necesitan Python 3 con matplotlib (opcional).

```bash
git clone https://github.com/laurajespm/Proyecto-ED.git
cd Proyecto-ED
javac -encoding UTF-8 -d out $(find src test -name "*.java")
```

En Windows, con PowerShell, el último comando es:

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src,test -Filter *.java).FullName
```

### Programa

```bash
java -cp out app.Main datos/estudiantes.txt
```

El archivo es opcional. Tiene una línea por estudiante con cuatro campos separados por punto y coma (`id;nombre;deportes que practica;deportes que le interesan`); los deportes de cada lista van separados por coma y cualquiera de las dos listas puede quedar vacía. Las líneas vacías y las que empiezan con `#` se ignoran. El archivo se lee en UTF-8.

Con los datos de ejemplo, la opción 4 muestra tres comunidades (de 8, 3 y 3 estudiantes); la opción 5 con el ID 1001 muestra que Ana Torres llega a Marta Díaz, que practica natación, a través de Luis Peña; con 1012 sale una conexión directa y con 1004 se informa que no existe conexión.

### Pruebas

```bash
java -cp out pruebas.PruebasSistema
java -cp out deportes.PruebasRF2RF7
```

`PruebasSistema` imprime PASÓ o FALLÓ por cada caso y termina con código 1 si alguno falla. Incluye casos borde de la lista, la cola y el AVL (eliminar hojas, nodos con un hijo y con dos hijos, 20 000 operaciones al azar y la altura del AVL), cada requisito funcional y una prueba que compara 12 000 conexiones con un cálculo independiente de la distancia mínima. `PruebasRF2RF7` prueba RF2 y RF7 por separado.

### Mediciones

```bash
java -Xmx6g -cp out analisis.Analizador
python3 resultados/graficar.py
```

El analizador genera estudiantes al azar con semilla fija y mide cada operación con `System.nanoTime` para n = 1 000, 10 000, 100 000 y 1 000 000 (opciones al inicio de `Analizador.java`, por ejemplo `--tamanos=10000,100000 --vueltas=5`). Guarda `resultados/tiempos.csv`, y `graficar.py` dibuja una gráfica por estructura con la curva teórica ajustada, una tabla y las razones de crecimiento entre tamaños.

## Estado del proyecto

- **Entrega 1:** reporte con comprensión del problema, MVP, estructuras elegidas y flujo general, más las estructuras base y las plantillas del código.
- **Entrega 2:** sistema completo con RF1 a RF7, menú de consola, datos de ejemplo, pruebas y mediciones de tiempo con sus gráficas.

## Referencias

1. N. Rhodes, "Basic Data Structures," Department of Computer Science and Engineering, University of California, San Diego.
2. M. A. Weiss, *Data Structures and Algorithm Analysis in Java*, 3.ª ed. Boston, MA, EE. UU.: Pearson, 2012.
3. T. H. Cormen, C. E. Leiserson, R. L. Rivest y C. Stein, *Introduction to Algorithms*, 4.ª ed. Cambridge, MA, EE. UU.: MIT Press, 2022.
