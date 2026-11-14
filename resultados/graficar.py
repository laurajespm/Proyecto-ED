# Lee resultados/tiempos.csv y dibuja las graficas. Uso: python graficar.py [archivo.csv] [prefijo]
import csv
import math
import os
import sys

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt

def leer(ruta):
    with open(ruta, encoding="utf-8", errors="replace", newline="") as archivo:
        lineas = [linea for linea in archivo if not linea.startswith("#")]
    filas = []
    for f in csv.DictReader(lineas):
        filas.append({
            "estructura": f["estructura"],
            "operacion": f["operacion"],
            "n": int(f["n"]),
            "d": int(f["d"]) if f["d"] else None,
            "mediana": float(f["mediana_ns_por_op"]),
            "minimo": float(f["minimo_ns_por_op"]),
            "maximo": float(f["maximo_ns_por_op"]),
            "teorica": f["teorica"],
        })
    return filas

def modelo(fila):
    t = fila["teorica"]
    if t == "O(1)":
        return 1.0
    if t == "O(log n)":
        return math.log2(fila["n"])
    if t in ("O(n)", "O(n+m)"):
        return float(fila["n"])
    if t == "O(n log n)":
        return fila["n"] * math.log2(fila["n"])
    if t == "O(d)" and fila["d"] is not None:
        return float(fila["d"])
    return None

def agrupar(filas):
    grupos = {}
    for f in filas:
        grupos.setdefault((f["estructura"], f["operacion"]), []).append(f)
    for lista in grupos.values():
        lista.sort(key=lambda f: f["n"])
    return grupos

def exponente(lista):
    if len(lista) < 2:
        return None
    xs = [math.log(f["n"]) for f in lista]
    ys = [math.log(max(f["mediana"], 1e-9)) for f in lista]
    mx = sum(xs) / len(xs)
    my = sum(ys) / len(ys)
    return sum((x - mx) * (y - my) for x, y in zip(xs, ys)) / sum((x - mx) ** 2 for x in xs)

def esperado(teorica):
    if teorica == "O(1)":
        return "0"
    if teorica == "O(log n)":
        return "cerca de 0 (crece despacio)"
    if teorica in ("O(n)", "O(n+m)"):
        return "1"
    if teorica == "O(n log n)":
        return "un poco más de 1"
    if teorica == "O(d)":
        return "0 si d es fijo, 1 si d crece con n"
    return "-"

def sube_poco(lista):
    t = lista[0]["teorica"]
    if t in ("O(1)", "O(log n)"):
        return True
    return t == "O(d)" and len({f["d"] for f in lista}) == 1

def dibujar(estructura, grupos, ruta_salida):
    operaciones = [clave for clave in grupos if clave[0] == estructura]
    columnas = min(3, len(operaciones))
    filas_fig = math.ceil(len(operaciones) / columnas)
    fig, ejes = plt.subplots(filas_fig, columnas, figsize=(5 * columnas, 3.8 * filas_fig), squeeze=False)

    for ax, clave in zip(ejes.flat, operaciones):
        lista = grupos[clave]
        n = [f["n"] for f in lista]
        y = [f["mediana"] for f in lista]
        abajo = [f["mediana"] - f["minimo"] for f in lista]
        arriba = [f["maximo"] - f["mediana"] for f in lista]
        ax.errorbar(n, y, yerr=[abajo, arriba], marker="o", capsize=3, label="medido (mediana, mín. y máx.)")

        teoricos = [modelo(f) for f in lista]
        if all(t is not None for t in teoricos):

            constante = math.exp(sum(math.log(max(f["mediana"], 1e-9) / t) for f, t in zip(lista, teoricos)) / len(lista))
            ax.plot(n, [constante * t for t in teoricos], linestyle="--", label=lista[0]["teorica"] + " (ajustada)")

        ax.set_xscale("log")
        if sube_poco(lista):

            ax.set_ylim(bottom=0)
        else:
            ax.set_yscale("log")
        ax.set_title(clave[1])
        ax.set_xlabel("n (tamaño de la entrada)")
        ax.set_ylabel("tiempo por llamada (ns)")
        ax.grid(True, alpha=0.3)
        ax.legend(fontsize=8)

    for ax in list(ejes.flat)[len(operaciones):]:
        ax.set_visible(False)
    fig.suptitle(estructura)
    fig.tight_layout()
    fig.savefig(ruta_salida, dpi=150)
    plt.close(fig)
    print("guardada", ruta_salida)

def crecimiento(grupos, ruta_salida):
    with open(ruta_salida, "w", encoding="utf-8", newline="") as archivo:
        w = csv.writer(archivo)
        w.writerow(["estructura", "operacion", "n_anterior", "n", "razon_observada", "razon_teorica"])
        for (estructura, operacion), lista in grupos.items():
            for a, b in zip(lista, lista[1:]):
                ta, tb = modelo(a), modelo(b)
                razon_teorica = "%.2f" % (tb / ta) if ta is not None and tb is not None else ""
                razon = "%.2f" % (b["mediana"] / a["mediana"]) if a["mediana"] > 0 else ""
                w.writerow([estructura, operacion, a["n"], b["n"], razon, razon_teorica])
    print("guardada", ruta_salida)

def tabla(grupos, ruta_salida):
    tamanos = sorted({f["n"] for lista in grupos.values() for f in lista})
    with open(ruta_salida, "w", encoding="utf-8", newline="") as archivo:
        w = csv.writer(archivo)
        w.writerow(["operacion", "teorica"] + ["n=%d" % n for n in tamanos])
        for (estructura, operacion), lista in grupos.items():
            por_n = {f["n"]: f["mediana"] for f in lista}
            w.writerow([estructura + "." + operacion, lista[0]["teorica"]]
                       + ["%.1f" % por_n[n] if n in por_n else "" for n in tamanos])
    print("guardada", ruta_salida)

def main():
    carpeta = os.path.dirname(os.path.abspath(__file__))
    ruta = sys.argv[1] if len(sys.argv) > 1 else os.path.join(carpeta, "tiempos.csv")
    base = os.path.splitext(os.path.basename(ruta))[0]
    prefijo = sys.argv[2] if len(sys.argv) > 2 else os.path.join(os.path.dirname(os.path.abspath(ruta)), base)

    if not os.path.exists(ruta):
        sys.exit("No encuentro " + ruta + ". Primero hay que correr analisis.Analizador, que es el que lo crea.")
    filas = leer(ruta)
    if not filas:
        sys.exit("El archivo no tiene mediciones: " + ruta)
    grupos = agrupar(filas)

    for estructura in sorted({f["estructura"] for f in filas}):
        dibujar(estructura, grupos, "%s_%s.png" % (prefijo, estructura))
    tabla(grupos, prefijo + "_tabla.csv")
    crecimiento(grupos, prefijo + "_crecimiento.csv")

    print()
    print("%-34s %-10s %s" % ("operación", "exponente", "esperado según la complejidad"))
    for (estructura, operacion), lista in grupos.items():
        e = exponente(lista)
        print("%-34s %-10s %s" % (estructura + "." + operacion, "-" if e is None else "%.2f" % e, esperado(lista[0]["teorica"])))

if __name__ == "__main__":
    main()
