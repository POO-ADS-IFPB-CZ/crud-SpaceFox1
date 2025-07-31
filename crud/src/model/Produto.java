package model;

import java.io.Serializable;
import java.util.Objects;

public class Produto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String codigo;
    private String descricao;
    private double preco;

    public Produto(String codigo, String descricao, double preco) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.preco = preco;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    @Override
    public String toString() {
        return "model.model.Produto{" +
                "codigo='" + codigo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", preco=" + preco + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produto model = (Produto) o;
        return  codigo.equals(model.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
