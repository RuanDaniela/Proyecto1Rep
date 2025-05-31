# Proyecto: Analizador de Archivos Lisp

Este proyecto en Java permite cargar un archivo `.lisp` desde una dirección dada por el usuario, analizar su contenido y realizar validaciones básicas sobre la sintaxis de las expresiones contenidas en el archivo. El objetivo principal es practicar conceptos de lectura de archivos, estructuras de datos y procesamiento de cadenas.

---

## ¿Qué hace el programa?

- Solicita al usuario la ruta exacta de un archivo `.lisp`.
- Lee el contenido del archivo.
- Verifica si los paréntesis están balanceados.
- Informa si la sintaxis del archivo es válida o si hay errores.

---

## Instrucciones para compilar y ejecutar (para el auxiliar)

Este proyecto fue desarrollado utilizando **Maven**.

### 1. Clonar o descargar el proyecto

```bash
git clone https://github.com/usuario/mi-proyecto-lisp.git
cd mi-proyecto-lisp
2. Compilar el proyecto
Ejecuta el siguiente comando en la raíz del proyecto:

bash
Copiar
Editar
mvn compile
3. Ejecutar el programa
Utiliza el siguiente comando:

bash
Copiar
Editar
mvn exec:java -Dexec.mainClass="org.example.Main"
⚠️ Instrucción importante para la ejecución
Cuando se esté ejecutando el programa, el sistema pedirá que el usuario escriba la ruta del archivo .lisp.

Por ejemplo:

En Windows:

makefile
Copiar
Editar
C:\Users\TuUsuario\Desktop\archivo.lisp

En Mac/Linux:
bash
Copiar
Editar
/home/tuusuario/Escritorio/archivo.lisp

El programa no funciona sin que se proporcione esta ruta, ya que no hay archivo .lisp embebido en el proyecto.

¿Cómo correr las pruebas?
En caso de haber pruebas unitarias (JUnit), se pueden ejecutar con:

bash
Copiar
Editar
mvn test
Autora

Este proyecto fue desarrollado por Daniela Ruano, con la colaboración de ChatGPT (OpenAI) para el diseño y redacción del código y la documentación