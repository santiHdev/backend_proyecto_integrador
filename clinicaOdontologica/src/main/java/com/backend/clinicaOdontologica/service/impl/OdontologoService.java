package com.backend.clinicaOdontologica.service.impl;

import com.backend.clinicaOdontologica.dto.OdontologoDto;
import com.backend.clinicaOdontologica.entity.Odontologo;
import com.backend.clinicaOdontologica.exceptions.ResourceNotFoundException;
import com.backend.clinicaOdontologica.repository.OdontologoRepository;
import com.backend.clinicaOdontologica.service.IOdontologoService;
import com.backend.clinicaOdontologica.utils.JsonPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OdontologoService implements IOdontologoService {
    private final Logger LOGGER = LoggerFactory.getLogger(OdontologoService.class);
    private final ObjectMapper objectMapper;
    private final OdontologoRepository odontologoRepository;

    @Autowired
    public OdontologoService(ObjectMapper objectMapper, OdontologoRepository odontologoRepository) {
        this.objectMapper = objectMapper;
        this.odontologoRepository = odontologoRepository;
    }


    public OdontologoDto buscarOdontologoPorId(Long id) {
        Odontologo odontologoBuscado = odontologoRepository.findById(id).orElse(null);
        OdontologoDto odontologoDto = null;
        if (odontologoBuscado != null) {
            odontologoDto = objectMapper.convertValue(odontologoBuscado, OdontologoDto.class);
            LOGGER.info("Odontologo encontrado: {}", JsonPrinter.toString(odontologoDto));
        } else {
            LOGGER.info("El id no se encuentra registrado en la base de datos");
        }
        return odontologoDto;
    }


    public List<OdontologoDto> listarOdontologos() {
        List<OdontologoDto> odontologos = odontologoRepository.findAll().stream()
                .map(o -> objectMapper.convertValue(o, OdontologoDto.class)).toList();
        LOGGER.info("Listado de todos los odontologos: {}", JsonPrinter.toString(odontologos));
        return odontologos;
    }

    @Override
    public OdontologoDto registrarOdontologo(Odontologo odontologo) {
        Odontologo odontologoGuardado = odontologoRepository.save(odontologo);
        LOGGER.info("Odontologo guardado: {}", JsonPrinter.toString(odontologoGuardado));
        return objectMapper.convertValue(odontologoGuardado, OdontologoDto.class);
    }

    @Override
    public OdontologoDto actualizarOdontologo(Odontologo odontologo) {
        Odontologo odontologoActualizar = odontologoRepository.findById(odontologo.getId()).orElse(null);
        OdontologoDto odontologoDtoActualizado = null;
        if (odontologoActualizar != null) {
            odontologoActualizar = odontologo;
            odontologoRepository.save(odontologoActualizar);
            odontologoDtoActualizado = objectMapper.convertValue(odontologoActualizar, OdontologoDto.class);
            LOGGER.warn("Odontologo actualizado: {}", JsonPrinter.toString(odontologoDtoActualizado));
        } else {
            LOGGER.error("No fue posible actualizar los datos ya que el odontologo no se encuentra registrado");
        }
        return odontologoDtoActualizado;
    }

    @Override
    public void eliminarOdontologo(Long id) throws ResourceNotFoundException {
        if (buscarOdontologoPorId(id) != null) {
            odontologoRepository.deleteById(id);
            LOGGER.warn("Se ha eliminado el odontologo con id: {}", id);
        } else {
            LOGGER.error("No se ha encontrado el odontologo con id " + id);
            throw new ResourceNotFoundException("No se ha encontrado el odontologo con id " + id);
        }
    }


}
