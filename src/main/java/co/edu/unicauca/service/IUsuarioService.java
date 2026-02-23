/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package co.edu.unicauca.service;

import co.edu.unicauca.model.Usuario;
import java.util.List;

/**
 *
 * @author jpuen
 */
public interface IUsuarioService {
    void crear(String username, String password, Usuario.Rol rol, Usuario adminActivo);
    List<Usuario> listarTodos();
    void eliminar(int id, Usuario adminActivo);
    void cambiarEstado(int id, Usuario.Estado estado, Usuario adminActivo);
}
