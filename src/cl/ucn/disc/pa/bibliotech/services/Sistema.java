/*
 * Copyright (c) 2023. Programacion Avanzada, DISC, UCN.
 */

package cl.ucn.disc.pa.bibliotech.services;

import cl.ucn.disc.pa.bibliotech.model.Libro;
import cl.ucn.disc.pa.bibliotech.model.Socio;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The Sistema.
 *
 * @author Programacion Avanzada.
 */
public final class Sistema {

    /**
     * Procesador de JSON.
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * The list of Socios.
     */
    private Socio[] socios;

    /**
     * The list of Libros.
     */
    private Libro[] libros;

    /**
     * Socio en el sistema.
     */
    private Socio socio;

    /**
     * The Sistema.
     */
    public Sistema() throws IOException {

        // no hay socio logeado.
        this.socios = new Socio[0];
        this.libros = new Libro[0];
        this.socio = null;

        // carga de los socios y libros.
        try {
            this.cargarInformacion();
        } catch (FileNotFoundException ex) {
            // no se encontraron datos, se agregar los por defecto.

            // creo un socio
            this.socios = Utils.append(this.socios, new Socio("John", "Doe", "john.doe@ucn.cl", 1, "john123"));

            // creo un libro y lo agrego al arreglo de libros.
            this.libros = Utils.append(this.libros, new Libro("1491910771", "Head First Java: A Brain-Friendly Guide", " Kathy Sierra", "Programming Languages", 0, 0));

            // creo otro libro y lo agrego al arreglo de libros.
            this.libros = Utils.append(this.libros, new Libro("1491910771", "Effective Java", "Joshua Bloch", "Programming Languages", 0, 0));

        } finally {
            // guardo la informacion.
            this.guardarInformacion();
        }

    }

    /**
     * Activa (inicia sesion) de un socio en el sistema.
     *
     * @param numeroDeSocio a utilizar.
     * @param contrasenia   a validar.
     */
    public void iniciarSession(final int numeroDeSocio, final String contrasenia) {

        // el numero de socio siempre es positivo.
        if (numeroDeSocio <= 0) {
            throw new IllegalArgumentException("El numero de socio no es valido!");
        }

        int poscionSocio = 0;
        // TODO: buscar el socio dado su numero.
        //se recorre el arreglo de socios
        for (int i = 0; i < socios.length; i++) {
            Socio socio = socios[i];
            //se verifica que el numero de socio proporcionado se encuentre en el arreglo de socios
            if (socio.getNumeroDeSocio() == numeroDeSocio) {
                //Se guarda la posicion del socio encontrado
                poscionSocio = i;
            }
        }
        boolean contreseniaCorrecta = false;
        // TODO: verificar su clave.
        if (socios[poscionSocio].getContrasenia().equals(contrasenia)) {
            contreseniaCorrecta = true;
        }

        // TODO: asignar al atributo socio el socio encontrado.
        if (contreseniaCorrecta) {
            this.socio = socios[poscionSocio];
        }
    }


    /**
     * Cierra la session del Socio.
     */
    public void cerrarSession() {
        this.socio = null;
    }

    /**
     * Metodo que mueve un libro de los disponibles y lo ingresa a un Socio.
     *
     * @param isbn del libro a prestar.
     */
    public void realizarPrestamoLibro(final String isbn) throws IOException {
        // el socio debe estar activo.
        if (this.socio == null) {
            throw new IllegalArgumentException("Socio no se ha logeado!");
        }

        // busco el libro.
        Libro libro = this.buscarLibro(isbn);

        // si no lo encontre, lo informo.
        if (libro == null) {
            throw new IllegalArgumentException("Libro con isbn " + isbn + " no existe o no se encuentra disponible.");
        }

        // agrego el libro al socio.
        this.socio.agregarLibro(libro);


        // TODO: eliminar el libro de los disponibles
        //Se recorre el arreglo de libros
        for (int i = 0; i < libros.length; i++) {
            Libro libroAEliminar = libros[i];
            //se verifica que el isbn proporcionado se encuentre en el arreglo de libros
            if (libroAEliminar.getIsbn().equals(isbn)) {
                //Se elimina el libro en la poscion que corresponde con el isbn ingresado
                libros[i] = null;
            }
        }


        // se actualiza la informacion de los archivos
        this.guardarInformacion();

    }

    /**
     * Obtiene un String que representa el listado completo de libros disponibles.
     *
     * @return the String con la informacion de los libros disponibles.
     */
    public String obtegerCatalogoLibros() {

        StringBuilder sb = new StringBuilder();
        for (Libro libro : this.libros) {
            sb.append("Titulo    : ").append(libro.getTitulo()).append("\n");
            sb.append("Autor     : ").append(libro.getAutor()).append("\n");
            sb.append("ISBN      : ").append(libro.getIsbn()).append("\n");
            sb.append("Categoria : ").append(libro.getCategoria()).append("\n");
            sb.append("Calificacion : ").append(libro.getCalificacion()).append("\n");
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Metodo que busca un libro en los libros disponibles.
     *
     * @param isbn a buscar.
     * @return el libro o null si no fue encontrado.l
     */
    private Libro buscarLibro(final String isbn) {
        // recorro el arreglo de libros.
        for (Libro libro : this.libros) {
            // si lo encontre, retorno el libro.
            if (libro.getIsbn().equals(isbn)) {
                return libro;
            }
        }
        // no lo encontre, retorno null.
        return null;
    }

    /**
     * Lee los archivos libros.json y socios.json.
     *
     * @throws FileNotFoundException si alguno de los archivos no se encuentra.
     */
    private void cargarInformacion() throws FileNotFoundException {

        // trato de leer los socios y los libros desde el archivo.
        this.socios = GSON.fromJson(new FileReader("socios.json"), Socio[].class);
        this.libros = GSON.fromJson(new FileReader("libros.json"), Libro[].class);
    }

    /**
     * Guarda los arreglos libros y socios en los archivos libros.json y socios.json.
     *
     * @throws IOException en caso de algun error.
     */
    private void guardarInformacion() throws IOException {

        // guardo los socios.
        try (FileWriter writer = new FileWriter("socios.json")) {
            GSON.toJson(this.socios, writer);
        }

        // guardo los libros.
        try (FileWriter writer = new FileWriter("libros.json")) {
            GSON.toJson(this.libros, writer);
        }

    }

    public String obtenerDatosSocioLogeado() {
        if (this.socio == null) {
            throw new IllegalArgumentException("No hay un Socio logeado");
        }

        return "Nombre: " + this.socio.getNombreCompleto() + "\n"
                + "Correo Electronico: " + this.socio.getCorreoElectronico();
    }

    /**
     * Cambia la contraseña del socio
     *
     * @param contrasenia contraseña a establecer
     * @return true si se cambia la contraseña, si no return false
     */
    public boolean contraseniaCambiada(String contrasenia) {
        //Se piden los datos del socio
        String datos = obtenerDatosSocioLogeado();
        //Se separan los datos pata obtener el nombre
        String[] partes = datos.split(" ");
        String nombreSocio = partes[1];
        //se busca el socio por su nombre
        for (int i = 0; i < socios.length; i++) {
            Socio socio = socios[i];
            //se verifica que el nombre coincida con el socio
            if (socio.getNombre().equals(nombreSocio)) {
                //Se guarda la posicion del socio encontrado
                int poscionSocio = i;
                //se cambia la contraseña
                socios[poscionSocio].setContrasenia(contrasenia);
                try {
                    this.guardarInformacion();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Metodo que cambia el correo del socio
     *
     * @param correoNuevo correo a establecer
     * @return true si se cambia el correo , si no return false
     */
    public boolean correoCambiado(String correoNuevo) {
        Utils.validarEmail(correoNuevo);
        //Se piden los datos del socio
        String datos = obtenerDatosSocioLogeado();
        //Se separan los datos pata obtener el correo
        String[] partes = datos.split(" ");
        String correoSocio = partes[4];
        //se busca el socio por su correo
        for (int i = 0; i < socios.length; i++) {
            Socio socio = socios[i];
            //se verifica que el correo  coincida con el socio
            if (socio.getCorreoElectronico().equals(correoSocio)) {
                //Se guarda la posicion del socio encontrado
                int poscionSocio = i;
                //se cambia la contraseña
                socios[poscionSocio].setCorreoElectronico(correoNuevo);
                try {
                    this.guardarInformacion();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Permite calificar un libro
     *
     * @param calificacion calificacion dada al libro
     * @param isbn         codigo para identificar el libro
     * @return true cuando se termina de calificar
     */
    public boolean calificacionLibro(float calificacion, String isbn) {
        // se busca el libro por su isbn
        Libro libroCalificado = buscarLibro(isbn);
        //se aunmenta el numero de calificaciones
        libroCalificado.setNumeroCalificaciones(libroCalificado.getNumeroCalificaciones() + 1);
        //se calcula la calificacion del libro usando un promedio
        libroCalificado.setCalificacion(libroCalificado.getCalificacion() + calificacion / libroCalificado.getNumeroCalificaciones());
        return true;

    }
}
