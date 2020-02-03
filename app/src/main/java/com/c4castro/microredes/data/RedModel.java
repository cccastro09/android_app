package com.c4castro.microredes.data;

public class RedModel {

    private long id;
    private String color;
    private String foto;
    private String familia;
    private String nDeParedesDeLaEspora;
    private String pais;
    private String tamanioUm;
    private String texturaDeLaEspora;
    private String tombreCientifico;
    private String informacionDeLaEspecie;

    public RedModel(long id, String color, String foto, String familia, String nDeParedesDeLaEspora, String pais, String tamanioUm, String texturaDeLaEspora, String tombreCientifico, String informacionDeLaEspecie) {
        this.id = id;
        this.color = color;
        this.foto = foto;
        this.familia = familia;
        this.nDeParedesDeLaEspora = nDeParedesDeLaEspora;
        this.pais = pais;
        this.tamanioUm = tamanioUm;
        this.texturaDeLaEspora = texturaDeLaEspora;
        this.tombreCientifico = tombreCientifico;
        this.informacionDeLaEspecie = informacionDeLaEspecie;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public String getnDeParedesDeLaEspora() {
        return nDeParedesDeLaEspora;
    }

    public void setnDeParedesDeLaEspora(String nDeParedesDeLaEspora) {
        this.nDeParedesDeLaEspora = nDeParedesDeLaEspora;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getTamanioUm() {
        return tamanioUm;
    }

    public void setTamanioUm(String tamanioUm) {
        this.tamanioUm = tamanioUm;
    }

    public String getTexturaDeLaEspora() {
        return texturaDeLaEspora;
    }

    public void setTexturaDeLaEspora(String texturaDeLaEspora) {
        this.texturaDeLaEspora = texturaDeLaEspora;
    }

    public String getTombreCientifico() {
        return tombreCientifico;
    }

    public void setTombreCientifico(String tombreCientifico) {
        this.tombreCientifico = tombreCientifico;
    }

    public String getInformacionDeLaEspecie() {
        return informacionDeLaEspecie;
    }

    public void setInformacionDeLaEspecie(String informacionDeLaEspecie) {
        this.informacionDeLaEspecie = informacionDeLaEspecie;
    }
}
