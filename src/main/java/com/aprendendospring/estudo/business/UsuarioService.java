package com.aprendendospring.estudo.business;

import com.aprendendospring.estudo.infrastructure.entity.Usuario;
import com.aprendendospring.estudo.infrastructure.exception.ConflictException;
import com.aprendendospring.estudo.infrastructure.exception.ResourceNotFoundException;
import com.aprendendospring.estudo.infrastructure.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // USADO PARA OS CAMPOS INICIALIZADOS COM FINAL
public class UsuarioService {



    private final UsuarioRepository usuarioRepository; // @RequeredArgsConstructor
    private final PasswordEncoder passwordEncoder;

    public Usuario salvaUsuario(Usuario usuario) {
        try {
            emailExiste(usuario.getEmail());
            // TRANSFORMA A SENHA DO USUARIO EM UMA SENHA CRIPTOGRAFADA
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            return usuarioRepository.save(usuario);
        } catch (ConflictException e) {
            throw new ConflictException("Email já cadastrado", e.getCause());
        }
    }

    public void emailExiste(String email) {
        try {
            boolean existe = verificaEmailExistente(email);
            if (existe) {
                throw new ConflictException("Email já cadastrado" + email);
            }
        } catch (ConflictException e) {
            throw new ConflictException("Email já cadastrado" + e.getCause());
        }
    }

    public boolean verificaEmailExistente(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email não encontrado" + email));
    }
    @Transactional
    public void deletaUsuarioPorEmail(String email) {
        usuarioRepository.deleteByEmail(email);
    }
}
