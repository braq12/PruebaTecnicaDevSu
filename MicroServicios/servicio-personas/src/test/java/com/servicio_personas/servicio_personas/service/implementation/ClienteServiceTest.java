package com.servicio_personas.servicio_personas.service.implementation;

import com.servicio_personas.servicio_personas.dtos.ActualizarClienteReq;
import com.servicio_personas.servicio_personas.dtos.ClienteResp;
import com.servicio_personas.servicio_personas.dtos.CrearClienteReq;
import com.servicio_personas.servicio_personas.feign.CuentasClient;
import com.servicio_personas.servicio_personas.jpa.entity.ClienteEntity;
import com.servicio_personas.servicio_personas.jpa.entity.PersonaEntity;
import com.servicio_personas.servicio_personas.jpa.repository.ClienteRepository;
import com.servicio_personas.servicio_personas.jpa.repository.PersonaRepository;
import com.servicio_personas.servicio_personas.mapper.ClienteMapper;
import com.servicio_personas.servicio_personas.mapper.PersonaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ClienteService usando JUnit 5 + Mockito.
 */
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private PersonaRepository personaRepo;
    @Mock
    private ClienteRepository clienteRepo;
    @Mock
    private PersonaMapper personaMapper;
    @Mock
    private ClienteMapper clienteMapper;
    @Mock
    private CuentasClient cuentasClient; // Actualmente no se usa en ClienteService, pero es dependencia final
    @Mock
    private CuentaAutoService cuentaAutoService;

    @InjectMocks
    private ClienteService service;

    private CrearClienteReq reqCrear;
    private PersonaEntity persona;
    private ClienteEntity cliente;
    private ClienteResp clienteResp;

    @BeforeEach
    void setUp() {
        // Datos base
        reqCrear = new CrearClienteReq();
        reqCrear.setNombre("Juan Pérez");
        reqCrear.setGenero("MASCULINO");
        reqCrear.setEdad(30);
        reqCrear.setIdentificacion("DNI-123");
        reqCrear.setDireccion("Calle 123");
        reqCrear.setTelefono("3001234567");
        reqCrear.setContrasena("secreto");
        // por defecto false (no crea cuenta); en tests se cambia cuando aplique

        persona = new PersonaEntity();
        persona.setIdPersona(1L);
        persona.setNombre("Juan Pérez");
        persona.setIdentificacion("DNI-123");

        cliente = new ClienteEntity();
        cliente.setIdCliente(10L);
        cliente.setPersona(persona);
        cliente.setContrasenaHash("hash");
        cliente.setEstado(1);

        clienteResp = new ClienteResp();
        clienteResp.setIdCliente(10L);
        clienteResp.setNombre("Juan Pérez");
        clienteResp.setEstado(1);
    }

    // ========= CREAR =========

    @Test
    void crear_deberiaCrearCliente_yNoDispararCuentaCuandoFlagEsFalse() {
        // given
        when(personaRepo.findByIdentificacion("DNI-123")).thenReturn(Optional.empty());
        when(personaMapper.toEntity(reqCrear)).thenReturn(persona);
        when(personaRepo.save(persona)).thenReturn(persona);
        when(clienteRepo.save(any(ClienteEntity.class))).thenAnswer(inv -> {
            ClienteEntity c = inv.getArgument(0);
            c.setIdCliente(10L);
            return c;
        });
        when(clienteMapper.toResp(any(ClienteEntity.class))).thenReturn(clienteResp);

        // when
        ClienteResp resp = service.crear(reqCrear);

        // then
        assertNotNull(resp);
        assertEquals(10L, resp.getIdCliente());
        verify(cuentaAutoService, never()).crearCuentaInicialAsync(anyLong());
    }

    @Test
    void crear_deberiaLanzar409_siIdentificacionExiste() {
        // given
        when(personaRepo.findByIdentificacion("DNI-123")).thenReturn(Optional.of(persona));

        // when + then
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.crear(reqCrear));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertTrue(ex.getReason().toLowerCase().contains("identificación"));
        verify(personaRepo, never()).save(any());
        verify(clienteRepo, never()).save(any());
    }

    @Test
    void crear_deberiaDispararCreacionCuentaAsync_cuandoFlagTrue() {
        // given
        reqCrear.setCrearCuentaAutomatica(true);
        when(personaRepo.findByIdentificacion("DNI-123")).thenReturn(Optional.empty());
        when(personaMapper.toEntity(reqCrear)).thenReturn(persona);
        when(personaRepo.save(persona)).thenReturn(persona);
        when(clienteRepo.save(any(ClienteEntity.class))).thenAnswer(inv -> {
            ClienteEntity c = inv.getArgument(0);
            c.setIdCliente(10L);
            return c;
        });
        when(clienteMapper.toResp(any(ClienteEntity.class))).thenReturn(clienteResp);

        // when
        ClienteResp resp = service.crear(reqCrear);

        // then
        assertNotNull(resp);
        verify(cuentaAutoService, times(1)).crearCuentaInicialAsync(10L);
    }

    // ========= OBTENER =========

    @Test
    void obtener_deberiaRetornarCliente_siExiste() {
        when(clienteRepo.findDetalleById(10L)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toResp(cliente)).thenReturn(clienteResp);

        ClienteResp resp = service.obtener(10L);

        assertNotNull(resp);
        assertEquals(10L, resp.getIdCliente());
        verify(clienteRepo, times(1)).findDetalleById(10L);
    }

    @Test
    void obtener_deberiaLanzar404_siNoExiste() {
        when(clienteRepo.findDetalleById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.obtener(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    // ========= LISTAR =========

    @Test
    void listar_deberiaMapearPaginaDeClientes() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("idCliente").ascending());
        Page<ClienteEntity> pageEntities = new PageImpl<>(List.of(cliente), pageable, 1);

        when(clienteRepo.buscar("DNI-123", "Juan", pageable)).thenReturn(pageEntities);
        when(clienteMapper.toResp(cliente)).thenReturn(clienteResp);

        Page<ClienteResp> page = service.listar("DNI-123", "Juan", pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals(10L, page.getContent().get(0).getIdCliente());
        verify(clienteRepo, times(1)).buscar("DNI-123", "Juan", pageable);
        verify(clienteMapper, times(1)).toResp(cliente);
    }

    // ========= ACTUALIZAR =========

    @Test
    void actualizar_deberiaActualizarContrasenaYEstado_validos() {
        ActualizarClienteReq req = new ActualizarClienteReq();
        req.setContrasena("nueva");
        req.setEstado(0);

        when(clienteRepo.findDetalleById(10L)).thenReturn(Optional.of(cliente));
        // personaMapper.updatePersonaFromDto(...) es un void → solo verificamos que se invoque
        doNothing().when(personaMapper).updatePersonaFromDto(eq(req), any(PersonaEntity.class));

        when(personaRepo.save(any(PersonaEntity.class))).thenReturn(persona);
        when(clienteRepo.save(any(ClienteEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(clienteMapper.toResp(any(ClienteEntity.class))).thenReturn(clienteResp);

        ClienteResp resp = service.actualizar(10L, req);

        assertNotNull(resp);
        // Verificaciones de efectos
        assertEquals(0, cliente.getEstado());
        assertNotEquals("nueva", cliente.getContrasenaHash()); // Se guarda hash, no la raw
        verify(personaMapper, times(1)).updatePersonaFromDto(eq(req), eq(persona));
        verify(personaRepo, times(1)).save(persona);
        verify(clienteRepo, times(1)).save(cliente);
    }

    @Test
    void actualizar_deberiaLanzar422_siEstadoInvalido() {
        ActualizarClienteReq req = new ActualizarClienteReq();
        req.setEstado(9);

        when(clienteRepo.findDetalleById(10L)).thenReturn(Optional.of(cliente));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.actualizar(10L, req));
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatusCode());
        verify(clienteRepo, never()).save(any());
    }

    @Test
    void actualizar_deberiaLanzar404_siClienteNoExiste() {
        ActualizarClienteReq req = new ActualizarClienteReq();
        when(clienteRepo.findDetalleById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.actualizar(999L, req));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    // ========= DESACTIVAR =========

    @Test
    void desactivar_deberiaPonerEstadoCero_siEstaActivo() {
        cliente.setEstado(1);
        when(clienteRepo.findById(10L)).thenReturn(Optional.of(cliente));
        when(clienteRepo.save(cliente)).thenReturn(cliente);

        service.desactivar(10L);

        assertEquals(0, cliente.getEstado());
        verify(clienteRepo, times(1)).save(cliente);
    }

    @Test
    void desactivar_noHaceNada_siYaEstaInactivo() {
        cliente.setEstado(0);
        when(clienteRepo.findById(10L)).thenReturn(Optional.of(cliente));

        service.desactivar(10L);

        verify(clienteRepo, never()).save(any());
    }

    @Test
    void desactivar_deberiaLanzar404_siNoExiste() {
        when(clienteRepo.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.desactivar(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    // ========= ELIMINAR =========

    @Test
    void eliminar_deberiaBorrar_siExiste() {
        when(clienteRepo.existsById(10L)).thenReturn(true);
        doNothing().when(clienteRepo).deleteById(10L);

        service.eliminar(10L);

        verify(clienteRepo, times(1)).deleteById(10L);
    }

    @Test
    void eliminar_deberiaLanzar404_siNoExiste() {
        when(clienteRepo.existsById(77L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.eliminar(77L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(clienteRepo, never()).deleteById(anyLong());
    }
}
