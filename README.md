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

La clase `SistemaDeportes` atiende los requisitos con tres árboles AVL y con las listas que unen estudiantes y deportes. Esas listas forman un **grafo bipartito implícito** Explicado con profundiad en el informe. Una cola permite recorrer ese grafo por anchura para obtener las comunidades y las conexiones.

### Estructuras de datos elegidas

En los costos, *n* es la cantidad de estudiantes, *d* la de deportes y *m* la de parejas (estudiante, deporte que practica).

| Estructura | Uso | Requisitos | Costo |
|------------|-----|------------|-------|
| **AVL de estudiantes** (clave: ID) | Registrar, consultar y eliminar por ID | RF1, RF2, RF3 | O(log n)  |
| **AVL de deportes** (clave: nombre normalizado) | Buscar o crear cada deporte al registrar | RF1 | O(log d) |
| **Listas doblemente enlazadas (DLL)** | Relación estudiante–deporte: cada estudiante tiene una DLL de deportes que practica y otra de los que le interesan; cada deporte tiene una DLL de practicantes | RF1, RF3, RF4, RF5 | Quitar un estudiante de la DLL de un deporte a partir de su referencia: O(1) |
| **Cola** | Recorrido por anchura del grafo bipartito | RF4, RF5, RF6 | O(V + E), con V = n + d y E = m |
| **AVL del ranking** (clave: número de practicantes, nombre) | Listar deportes de más a menos practicantes con un recorrido inorden | RF7 | Listar: O(d); actualizar un deporte: O(log d) |

**Justificación resumida**

- **AVL:** busca, inserta y elimina en O(log n) en el peor caso. Una lista costaría O(n) por búsqueda; un arreglo ordenado inserta y elimina en O(n); un árbol binario de búsqueda sin balancear se degenera en lista si los ID llegan en orden. Una tabla hash daría O(1) en promedio, pero es un tema posterior del curso y se evaluará en la Entrega 3.
- **DLL:** cada estudiante guarda el nodo que ocupa en la DLL de cada deporte que practica, por lo que al eliminarlo se quita en O(1). Además hacen de listas de adyacencia, así que no hace falta un grafo aparte: unir a los estudiantes entre sí exigiría k(k−1)/2 aristas por deporte con k practicantes, en lugar de k.
- **Cola:** el recorrido por anchura visita a los estudiantes por niveles, así que el primero encontrado que practica un deporte de interés es el de menos intermediarios. Si la cola se vacía sin encontrarlo, la conexión no existe. Una pila daría un recorrido en profundidad y no aseguraría el camino más corto.
- **AVL del ranking:** al cambiar la cantidad de practicantes, el deporte se saca y se vuelve a insertar en O(log d). Un heap daría el deporte con más practicantes en O(1), pero listarlos todos en orden costaría O(d log d) en cada consulta.

## Flujo general

1. Al iniciar, se pueden cargar los estudiantes desde un archivo de texto. Las líneas con error se informan y se saltan.
2. El menú de consola atiende cada opción hasta que se elige salir (opción 0):
   1. **Registrar (RF1):** consulta el AVL de estudiantes para rechazar un ID repetido, busca o crea cada deporte en el AVL de deportes, agrega al estudiante a las DLL correspondientes, actualiza el AVL del ranking e inserta al estudiante.
   2. **Consultar (RF2):** busca en el AVL de estudiantes y muestra sus datos, o avisa que no existe.
   3. **Eliminar (RF3):** elimina del AVL de estudiantes, lo quita de la DLL de cada deporte que practicaba y actualiza el ranking.
   4. **Comunidades (RF4):** recorre a los estudiantes en orden de ID; desde cada uno sin visitar hace un recorrido por anchura con la cola. Un grupo de dos o más estudiantes es una comunidad.
   5. **Conexión (RF5 y RF6):** encola al estudiante que hace la consulta y saca estudiantes de la cola hasta encontrar uno que practique un deporte de interés (conexión encontrada) o hasta que la cola se vacíe (no hay conexión).
   6. **Ranking (RF7):** recorrido inorden del AVL del ranking.


## Estructura del repositorio
```text
Proyecto-ED/   
├──CursoIntersemestral.java
├──DLL.java
├──DLLNode.java
├──Deporte.java
├── DeporteRepresentativo   
├── Estudiante.java
├── GestorDeportes.java
├── MyQueue.java
├── Queue.java
├── Recreativo.java
├── solicitud.java
└── README.md
```

## Requisitos, compilación y ejecución

Fase final de compilaciòn aùn en proceso:

```bash
git clone https://github.com/laurajespm/Proyecto-ED.git
cd Proyecto-ED

```

## Estado del proyecto
A partir del desarrollo actualmente utilizado, se completò la **Entrega 1**

- **Entrega 1:** reporte con comprensión del problema, MVP, estructuras elegidas y flujo general.

## Referencias

1. N. Rhodes, "Basic Data Structures" Department of Computer Science and Engineering, University of California, San Diego.
4. M. A. Weiss, *Data Structures and Algorithm Analysis in Java*, 3.ª ed. Boston, MA, EE. UU.: Pearson, 2012.
5. T. H. Cormen, C. E. Leiserson, R. L. Rivest y C. Stein, *Introduction to Algorithms*, 4.ª ed. Cambridge, MA, EE. UU.: MIT Press, 2022.
