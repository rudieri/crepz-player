/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musica;

/**
 *
 * @author manchini
 */
public class Musica {

   
   
    private Integer id;
    private String nome;
    private String autor;
    private String genero;
    private String compositor;
    private String Album;
    private String caminho;
    private String img;
    private int size;
    private int number;
   

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public boolean setNome(String nome) {
        if (nome == null || nome.equals("")) {
            System.out.println("\n------- NOME VAZIO -------\n");
            return false;
        } else {
            this.nome = nome;
            return true;
        }
    }

    /**
     * @return the autor
     */
    public String getAutor() {
        return autor;
    }

    /**
     * @param autor the autor to set
     */
    public void setAutor(String autor) {
        if(autor==null && autor.equals("") ){
            if(this.autor==null){
                this.autor="Não definido!";
            }
        }else{
            this.autor = autor;
        }
    }

    public void setCompositor(String compositor){
       if(compositor==null && compositor.equals("") ){
            if(this.compositor==null){
                this.compositor="Não definido!";
            }
        }else{
            this.compositor = compositor;
        }
    }
    public String getCompositor(){
        return compositor;
    }

    /**
     * @return the genero
     */
    public String getGenero() {
        return genero;
    }

    /**
     * @param genero the genero to set
     */
    public void setGenero(String genero) {
        if (genero != null) {
            if (genero.indexOf("(") == 0) {
                try {
                    setGenero(new Integer(genero.replace("(", "").replace(")", "")));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                this.genero = genero;
            }
        }
        else{
            if(this.genero==null){
                this.genero="Não definido!";
            }
        }
    }

    /**
     * @return the Album
     */
    public String getAlbum() {
        return Album;
    }

    /**
     * @param Album the Album to set
     */
    public void setAlbum(String Album) {
        if (Album != null) {
            this.Album = Album;
        } else {
            if(this.Album==null){
                this.Album = "Não definido!";
            }
        }
    }

    /**
     * @return the caminho
     */
    public String getCaminho() {
        if (caminho != null) {
            return caminho.replace("<&aspas>", "'");
        } else {
            return null;
        }
    }

    /**
     * @param caminho the caminho to set
     */
    public void setCaminho(String caminho) {

        this.caminho = caminho.replace("\\", "/").replace("//", "/").replace("'", "<&aspas>");
    }

    /**
     * @return the img
     */
    public String getImg() {
        if (img != null) {
            return img.replace("<&aspas>", "'");
        } else {
            return "";
        }
    }

    /**
     * @param img the img to set
     */
    public void setImg(String img) {
        if (!(img == null)) {
            this.img = img.replace("\\", "/").replace("//", "/").replace("'", "<&aspas>");
        }

    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

   

    public  void setGenero(int genre) {
        if (genre >= MusicaGerencia.generos.length || genre < 0) {
            genero = "";
            return;
        }
        try {
            genero = MusicaGerencia.generos[genre];
        } catch (Exception b) {
            genero = "";
            b.printStackTrace();
        }
    }

    public  void setSize(int s) {
        this.size = s;
    }

    public int getSize() {
        return this.size;
    }

    private void setNumero(int n) {
        this.number = n;
    }

    public int getNumero() {
        return number;
    }

   
}
