package etsiitdevs.com.palioblanco.api;

/**
 * Created by jpblo on 17/06/17.
 */

public class ResultadoFiltro {
    public Marcha marcha;
    public Autor autor;
    public Usuario usuario;
    public Lista lista;
    public String title;


    public int tipo;

    public static final int MARCHA = 0;
    public static final int AUTOR = 1;
    public static final int USUARIO = 2;
    public static final int LISTA = 3;
    public static final int TITLE = 4;

}
