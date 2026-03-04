/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.edu.unicauca.model;

/**
 *
 * @author jpuen
 */
public class Usuario {

    public enum Rol {
        ADMIN,
        USER
    }

    public enum Estado {
        ACTIVO,
        DESACTIVADO
    }

    private int id;
    private String username;
    private String passwordHash;
    private Rol rol;
    private Estado estado;

    public Usuario() {
        this.estado = Estado.ACTIVO;
    }

    public Usuario(int id, String username, String passwordHash, Rol rol) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.estado = Estado.ACTIVO;
    }

    public Usuario(int id, String username, String passwordHash, Rol rol, Estado estado) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public boolean esAdmin() { return this.rol == Rol.ADMIN; }
    
    public boolean esActivo() { return this.estado == Estado.ACTIVO; }

    @Override
    public String toString() {
        return username + " [" + rol.name() + "] - " + estado.name();
    }
}

