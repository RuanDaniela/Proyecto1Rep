# Proyecto: Analizador de Archivos Lisp

Este proyecto en Java permite cargar un archivo `.lisp` desde una dirección dada por el usuario, analizar su contenido y realizar validaciones básicas sobre la sintaxis de las expresiones contenidas en el archivo. El objetivo principal es practicar conceptos de lectura de archivos, estructuras de datos y procesamiento de cadenas.

## ¿Qué hace el programa?

- Solicita al usuario la ruta exacta de un archivo `.lisp`.
- Lee el contenido del archivo.
- Verifica si los paréntesis están balanceados.
- Informa si la sintaxis del archivo es válida o si hay errores.

---

## ¿Cómo usar este proyecto?

Este proyecto usa **Maven** como herramienta de construcción. A continuación se presentan los pasos para compilar y correr el programa:

### 1. Clona o descarga este repositorio.

```bash
git clone https://github.com/usuario/mi-proyecto-lisp.git
cd mi-proyecto-lisp
2. Compila el proyecto con Maven:
bash
Copiar
Editar
mvn compile
3. Ejecuta el programa:
Puedes ejecutar la clase Main desde línea de comandos, por ejemplo:

bash
Copiar
Editar
mvn exec:java -Dexec.mainClass="org.example.Main"
⚠️ IMPORTANTE: Cuando el programa te pida la dirección del archivo .lisp, debes ingresar la ruta absoluta o relativa al proyecto.

Ejemplo:
Si el archivo está en el escritorio y usas Windows:

makefile
Copiar
Editar
C:\Users\TuUsuario\Desktop\archivo.lisp

Si estás en Linux o Mac:

bash
Copiar
Editar
/home/tuusuario/Escritorio/archivo.lisp

¿Cómo correr las pruebas?
Si este proyecto incluye pruebas unitarias, puedes ejecutarlas con:

bash
Copiar
Editar
mvn test
Notas para el auxiliar (o persona que revisa el proyecto)
Asegúrese de tener Java y Maven instalados.

El archivo .lisp no está embebido en el proyecto. El programa pedirá que se escriba la ruta manualmente al momento de la ejecución.

Si desea probar con un archivo propio, puede copiar cualquier contenido Lisp válido (como listas con paréntesis balanceados) y guardarlo en un archivo .lisp para usarlo en la prueba.

Autor
Este proyecto fue realizado por Daniela Ruano, con la ayuda de ChatGPT (de OpenAI) para estructurar el código 