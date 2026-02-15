package com.example.interpretels.models;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "signs")
public class Sign {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nombre;
    private String descripcion;
    private String imagenNombre;
    private String categoria;

    public Sign(String nombre, String descripcion, String imagenNombre, String categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenNombre = imagenNombre;
        this.categoria = categoria;
    }

        public int getId () {
            return id;
        }

        public void setId ( int id){
            this.id = id;
        }

        public String getNombre () {
            return nombre;
        }

        public void setNombre (String nombre){
            this.nombre = nombre;
        }

        public String getDescripcion () {
            return descripcion;
        }

        public void setDescripcion (String descripcion){
            this.descripcion = descripcion;
        }

        public String getImagenNombre () {
            return imagenNombre;
        }

        public void setImagenNombre (String imagenNombre){
            this.imagenNombre = imagenNombre;
        }

        public String getCategoria () {
            return categoria;
        }

        public void setCategoria (String categoria){
            this.categoria = categoria;
        }


}
