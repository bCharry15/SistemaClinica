/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.edu.unicauca.service;

import co.edu.unicauca.model.Usuario;
import co.edu.unicauca.repository.IUsuarioRepository;
import co.edu.unicauca.util.PasswordUtil;
import java.util.List;

/**
 *
 * @author jpuen
 */
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void crear(String username, String password, Usuario.Rol rol, Usuario adminActivo) {
        if (adminActivo == null || !adminActivo.esAdmin()) {
            throw new SecurityException("Solo un ADMIN puede crear nuevos usuarios.");
        }
        if (usuarioRepository.existeUsername(username)) {
            throw new IllegalArgumentException("El usuario '" + username + "' ya existe.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
        Usuario nuevo = new Usuario(0, username, PasswordUtil.hashear(password), rol);
        usuarioRepository.guardar(nuevo);
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.obtenerTodos();
    }

    @Override
    public void eliminar(int id, Usuario adminActivo) {
        if (adminActivo == null || !adminActivo.esAdmin()) {
            throw new SecurityException("Solo un ADMIN puede eliminar usuarios.");
        }
        if (id == adminActivo.getId()) {
            throw new IllegalArgumentException("No puedes eliminar tu propia cuenta.");
        }
        usuarioRepository.eliminar(id);
    }
}
